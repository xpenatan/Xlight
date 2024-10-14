package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import xlight.engine.core.asset.XAssetUtil;
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
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XLoadEntity {

    private boolean loadSubScene;

    private Array<XEntity> entitiesToAttach = new Array<>();

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
                    XDataMap entityMap = entitiesArray.get(i);
                    int entityType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                    if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                        loadEntityAndAdd(world, scene, entitiesToAttach, poolController, entityMap);
                    }
                }
            }

            XEntityService entityService = world.getWorldService().getEntityService();
            for(int i = 0; i < entitiesToAttach.size; i++) {
                XEntity entity = entitiesToAttach.get(i);
                entityService.attachEntity(entity);
            }
            entitiesToAttach.clear();
        }
    }

    private XEntity loadEntityAndAdd(XWorld world, XScene scene, Array<XEntity> tmpEntities, XPoolController poolController, XDataMap entityMap) {
        // TODO remove loading recursive
        XEntity entity = loadEntity(world, scene, tmpEntities, poolController, entityMap);
        if(entity == null) {
            return null;
        }
        XDataMapArray childEntitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childDataMap : list) {
                int entityType = childDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntity childEntity = loadEntityAndAdd(world, scene, tmpEntities, poolController, childDataMap);
                    if(childEntity != null) {
                        childEntity.setParent(entity);
                    }
                }
            }
        }
        return entity;
    }

    private XEntity loadEntity(XWorld world, XScene scene, Array<XEntity> tmpEntities, XPoolController poolController, XDataMap entityMap) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int entityJsonId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");
        XEntity entity = entityService.obtain(entityJsonId);
        if(entity == null) {
            return null;
        }
        tmpEntities.add(entity);
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        if(componentArray != null) {
            addEntityComponents(scene, registerManager, poolController, entity, componentArray, false, entityJsonId);
        }
        return entity;
    }

    private void addEntityComponents(XScene scene, XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMapArray componentArray, boolean isSubScene, int entityJsonId) {
        int size = componentArray.getSize();
        for(int i = 0; i < size; i++) {
            XDataMap componentMap = componentArray.get(i);
            Class<?> componentType = XSceneMapUtils.getComponentTypeFromComponentMap(registerManager, componentMap);
            if(componentType != null) {
                boolean haveComponent = entity.containsComponent(componentType);
                // Add component only if it's not set. Components loaded from sub scene may return false
                if(!haveComponent) {
                    XComponent component = loadComponent(poolController, componentMap, componentType);
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
                    sceneComponent.entityId = entityJsonId;
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
            XDataMap sceneEntityDataMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneEntityId);
            if(sceneEntityDataMap != null) {
                XDataMapArray componentsDataMap = sceneEntityDataMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
                if(componentsDataMap != null) {
                    addEntityComponents(scene, registerManager, poolController, entity, componentsDataMap, true, -1);
                }
            }
            sceneDataMap.free();
        }
        else {
            System.err.println("Scene path not found: " + fileHandle + " - " + scenePath);
        }
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