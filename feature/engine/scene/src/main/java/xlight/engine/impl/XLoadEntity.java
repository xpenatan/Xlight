package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XLoadEntity {

    private boolean loadSubScene;

    public void load(XWorld world, XScene scene, boolean isSubScene) {
        System.out.println("LOAD SCENE:");
        loadSubScene = isSubScene;

        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XDataMap sceneDataMap = scene.getSceneDataMap();
        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(entitiesArray != null) {
                int size = entitiesArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap entityDataMap = entitiesArray.get(i);
                    int entityType = entityDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                    if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                        loadEntityAndInit(world, scene, poolController, entityDataMap);
                    }
                }
            }
        }
    }

    private XEntity loadEntityAndInit(XWorld world, XScene scene, XPoolController poolController, XDataMap entityDataMap) {
        // TODO remove loading recursive
        XEntityService entityService = world.getWorldService().getEntityService();
        XEntity entity = loadEntity(world, scene, poolController, entityDataMap);
        entityService.attachEntity(entity);
        XDataMapArray childEntitiesArray = entityDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childDataMap : list) {
                int entityType = childDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntity childEntity = loadEntityAndInit(world, scene, poolController, childDataMap);
                    if(childEntity != null) {
                        childEntity.setParent(entity);
                    }
                }
            }
        }
        return entity;
    }

    private XEntity loadEntity(XWorld world, XScene scene, XPoolController poolController, XDataMap entityDataMap) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        String entityName = entityDataMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityDataMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityDataMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int jsonId = entityDataMap.getInt(XSceneKeys.JSON_ID.getKey(), -1);
        String tag = entityDataMap.getString(XSceneKeys.TAG.getKey(), "");

        XEntity entity = entityService.obtain();
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        XDataMapArray componentsDataMap = entityDataMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        if(componentsDataMap != null) {
            addEntityComponents(scene, registerManager, poolController, entity, componentsDataMap, false);
        }
        return entity;
    }

    private void addEntityComponents(XScene scene, XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMapArray componentsDataMap, boolean isSubScene) {
        int size = componentsDataMap.getSize();
        for(int i = 0; i < size; i++) {
            XDataMap componentDataMap = componentsDataMap.get(i);
            int sceneType = componentDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
            if(sceneType == XSceneTypeValue.COMPONENT.getValue()) {
                Class<?> componentType = getComponentType(registerManager, componentDataMap);
                if(componentType != null) {
                    XComponent component = loadComponent(poolController, componentDataMap, componentType);
                    entity.attachComponent(component);
                }
            }
        }

        if(!isSubScene) {
            XSceneComponent sceneComponent = entity.getComponent(XSceneComponent.class);

            if(sceneComponent != null) {
                // search for component entityId in scene path add all recursive components from it
                try {
                    addSceneComponents(scene, registerManager, poolController, entity, sceneComponent.entityId, sceneComponent.scenePath, sceneComponent.fileHandleType);
                }
                catch(Throwable t) {
                    t.printStackTrace();
                }
            }
            else {
                if(loadSubScene) {
                    sceneComponent = poolController.obtainObject(XSceneComponent.class);
                    sceneComponent.scenePath = scene.getPath();
                    sceneComponent.fileHandleType = XAssetUtil.getFileTypeValue(scene.getFileType());
                    entity.attachComponent(sceneComponent);
                }
            }
        }
    }

    private void addSceneComponents(XScene scene, XRegisterManager registerManager, XPoolController poolController, XEntity entity, int sceneEntityId, String scenePath, int fileHandleType) {
        if(sceneEntityId == -1 || scenePath == null || scenePath.trim().isEmpty()) {
            return;
        }

        Files.FileType fileType = XAssetUtil.getFileTypeEnum(fileHandleType);
        FileHandle fileHandle = Gdx.files.getFileHandle(scenePath, fileType);
        if(fileHandle.exists()) {
            String jsonStr = fileHandle.readString();
            XDataMap sceneDataMap = XDataMap.obtain(poolController);
            sceneDataMap.loadJson(jsonStr);
            XDataMap sceneEntityDataMap = getSceneEntityMap(sceneEntityId, sceneDataMap);
            if(sceneEntityDataMap != null) {
                XDataMapArray componentsDataMap = sceneEntityDataMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
                if(componentsDataMap != null) {
                    addEntityComponents(scene, registerManager, poolController, entity, componentsDataMap, true);
                }
            }
            sceneDataMap.free();
        }
        else {
            System.err.println("Scene path not found: " + fileHandle + " - " + scenePath);
        }
    }

    private XDataMap getSceneEntityMap(int sceneEntityId, XDataMap sceneDataMap) {
        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(entitiesArray != null) {
                // TODO change to a int map?
                int size = entitiesArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap entityDataMap = entitiesArray.get(i);
                    int entityType = entityDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                    if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                        int jsonId = entityDataMap.getInt(XSceneKeys.JSON_ID.getKey(), -1);
                        if(sceneEntityId == jsonId) {
                            return entityDataMap;
                        }
                    }
                }
            }

        }
        return null;
    }

    private Class<?> getComponentType(XRegisterManager registerManager, XDataMap componentDataMap) {
        int key = componentDataMap.getInt(XSceneKeys.CLASS.getKey(), -1);
        if(key != -1) {
            XMetaClass registeredClass = registerManager.getRegisteredClass(key);
            if(registeredClass != null) {
                return registeredClass.getType();
            }
        }
        return null;
    }

    private XComponent loadComponent(XPoolController poolController, XDataMap componentMap, Class<?> componentType) {
        XComponent component = obtainComponent(poolController, componentMap, componentType);
        if(component != null) {
            XDataMap dataMap = componentMap.getDataMap(XSceneKeys.DATA.getKey());
            if(dataMap != null && component instanceof XDataMapListener) {
                ((XDataMapListener)component).onLoad(dataMap);
            }
            return component;
        }
        else {
            throw new RuntimeException("Wrong component type: " + componentType);
        }
    }

    private XComponent obtainComponent(XPoolController poolController, XDataMap componentMap, Class<?> type) {
        Object o = poolController.obtainObject(type);
        if(o instanceof XComponent) {
            XComponent component = (XComponent)o;
            XDataMap dataMap = componentMap.getDataMap(XSceneKeys.DATA.getKey());
            if(dataMap != null && component instanceof XDataMapListener) {
                ((XDataMapListener)component).onLoad(dataMap);
            }
            return component;
        }
        else {
            return null;
        }
    }
}