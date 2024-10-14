package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XDataArray;
import xlight.engine.list.XList;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XLoadEntity {

    private XDataArray<XEntity, XEntityLoadNode> entitiesToAttach;

    public void load(XWorld world, XScene scene, boolean isSubScene) {
        System.out.println("LOAD SCENE:");
        entitiesToAttach = new XDataArray<>(new XPool<>() {
            @Override
            protected XEntityLoadNode newObject() {
                return new XEntityLoadNode();
            }
        });

        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
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
                        loadEntity(entityService, registerManager, poolController, entityMap);
                    }
                }
            }

            attachEntities(entityService, poolController, isSubScene ? scene : null);
        }
    }

    public XEntity loadEntityAndAttach(XEntityService entityService, XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap) {
        XEntity entity = loadEntity(entityService, registerManager, poolController, entityMap);
        attachEntities(entityService, poolController, null);
        return entity;
    }

    private void attachEntities(XEntityService entityService, XPoolController poolController, XScene subScene) {
        for(int i = 0; i < entitiesToAttach.getSize(); i++) {
            XEntityLoadNode node = entitiesToAttach.getNode(i);
            XEntity entity = node.getValue();

            if(subScene != null) {
                XSceneComponent sceneComponent = poolController.obtainObject(XSceneComponent.class);
                sceneComponent.scenePath = subScene.getPath();
                sceneComponent.fileHandleType = XAssetUtil.getFileTypeValue(subScene.getFileType());
                sceneComponent.entityId = node.entityJsonId;
                entity.attachComponent(sceneComponent);
            }
            entityService.attachEntity(entity);
        }
        entitiesToAttach.clear();
    }

    private XEntity loadEntity(XEntityService entityService, XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap) {
        // TODO remove loading recursive
        XEntity entity = setupEntity(entityService, registerManager, poolController, entityMap);
        if(entity == null) {
            return null;
        }
        XDataMapArray childEntitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childDataMap : list) {
                int entityType = childDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntity childEntity = loadEntity(entityService, registerManager, poolController, childDataMap);
                    if(childEntity != null) {
                        childEntity.setParent(entity);
                    }
                }
            }
        }
        return entity;
    }

    private XEntity setupEntity(XEntityService entityService, XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap) {
        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int entityJsonId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");
        XEntity entity = entityService.obtain(entityJsonId);
        if(entity == null) {
            // Getting an entity with the same ID as the scene did not work because its already being used.
            // Use a new entity
            entity = entityService.obtain();
        }
        XEntityLoadNode node = entitiesToAttach.add(entity);
        node.entityJsonId = entityJsonId;
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        if(componentArray != null) {
            addEntityComponents(registerManager, poolController, entity, componentArray, false, entityJsonId);
        }
        return entity;
    }

    private void addEntityComponents(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMapArray componentArray, boolean isSubScene, int entityJsonId) {
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
                    addSceneComponents(registerManager, poolController, entity, sceneComponent.entityId, sceneComponent.scenePath, sceneComponent.fileHandleType);
                }
                catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private void addSceneComponents(XRegisterManager registerManager, XPoolController poolController, XEntity entity, int sceneEntityId, String scenePath, int fileHandleType) {
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
                    addEntityComponents(registerManager, poolController, entity, componentsDataMap, true, -1);
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



    public static class XEntityLoadNode extends XDataArray.XDataArrayNode<XEntity> {
        public int entityJsonId = -1;

        @Override
        public void onReset() {
            super.onReset();
            entityJsonId = -1;
        }
    }
}