package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
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
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XLoadEntity2 {

    private final ObjectMap<String, XDataMap> loadedSubScenes = new ObjectMap<>(); // Cache for already loaded scenes

    private XDataArray<XEntity, XEntityLoadNode> entitiesToAttach;
    private XDataArray<XEntity, XEntityLoadNode> tempEntitiesToAttach;

    public XLoadEntity2() {
        entitiesToAttach = new XDataArray<>(new XPool<>() {
            @Override
            protected XLoadEntity2.XEntityLoadNode newObject() {
                return new XLoadEntity2.XEntityLoadNode();
            }
        });
        tempEntitiesToAttach = new XDataArray<>(new XPool<>() {
            @Override
            protected XLoadEntity2.XEntityLoadNode newObject() {
                return new XLoadEntity2.XEntityLoadNode();
            }
        });
    }

    public void load(XWorld world, XScene scene, boolean isSubScene) {
        System.out.println("LOAD SCENE:");

        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        XDataMap sceneDataMap = scene.getSceneDataMap();

        String jsonStr = sceneDataMap.saveJsonStr();
        System.out.print(jsonStr);

        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(entitiesArray != null) {
                int size = entitiesArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap entityMap = entitiesArray.get(i);
                    addEntity(entityService, registerManager, poolController, entitiesToAttach, entityMap, true);
                }
            }
        }
        XScene subScene = isSubScene ? scene : null;
        initEntities(entityService, registerManager, poolController, entitiesToAttach, subScene);
    }

    public XEntity loadEntityAndAttach(XWorld world, XDataMap entityMap) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XEntityLoadNode node = addEntity(entityService, registerManager, poolController, entitiesToAttach, entityMap, true);
        if(node == null) {
            return null;
        }
        XEntity entity = node.getValue();
        initEntities(entityService, registerManager, poolController, entitiesToAttach, null);
        return entity;
    }

    private void initEntities(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XScene subScene
    ) {
        XList<XEntityLoadNode> list = entitiesToAttach.getNodeList();

        // If the sub scene is updated, we need to add the missing sub scene entities because the current scene don't have them
        initMissingEntityChildren(entityService, registerManager, poolController, entitiesToAttach);

        // Add components to all entities. If it has a scene component then it compare it with the other scene
        initEntitiesSceneComponents(registerManager, poolController, entitiesToAttach);

        // Attach every entity
        for(XEntityLoadNode node : list) {
            if(!node.error) {
                XEntity entity = node.getValue();
                entityService.attachEntity(entity);
                if(subScene != null) {
                    // When scene is added to the current scene. We add a SceneComponent for it to know that these entities are from another file
                    XSceneComponent sceneComponent = poolController.obtainObject(XSceneComponent.class);
                    sceneComponent.scenePath = subScene.getPath();
                    sceneComponent.fileHandleType = XAssetUtil.getFileTypeValue(subScene.getFileType());
                    sceneComponent.entityId = entity.getLoadId(); // Since we added entity from a file, we can use the loaded id
                    entity.attachComponent(sceneComponent);
                }
            }
        }
        entitiesToAttach.clear();
        ObjectMap.Entries<String, XDataMap> it = loadedSubScenes.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, XDataMap> entry = it.next();
            String key = entry.key;
            XDataMap value = entry.value;
            value.free();
        }
        loadedSubScenes.clear();
    }

    private void initEntitiesSceneComponents(
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach
    ) {
        for(XEntityLoadNode node : entitiesToAttach.getNodeList()) {
            XEntity mainEntity = node.getValue();
            XDataMap entityMap = node.entityMap;
            initEntitySceneComponent(registerManager, poolController, mainEntity, entityMap);
        }
    }

    private void initEntitySceneComponent(
            XRegisterManager registerManager,
            XPoolController poolController,
            XEntity mainEntity,
            XDataMap entityMap
    ) {
        XDataMap sceneComponentMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentMap != null) {
            String entityScenePath = sceneComponentMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int sceneComponentEntityId = sceneComponentMap.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
            XDataMap sceneDataMap = loadedSubScenes.get(entityScenePath);
            if(sceneDataMap != null) {
                XDataMap subSceneEntityMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneComponentEntityId);
                if(subSceneEntityMap != null) {
                    addEntityComponents(registerManager, poolController, mainEntity, subSceneEntityMap);
                    initEntitySceneComponent(registerManager, poolController, mainEntity, subSceneEntityMap);
                }
            }
        }
    }

    private void initMissingEntityChildren(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach
    ) {
        for(XEntityLoadNode node : entitiesToAttach.getNodeList()) {
            XEntity entity = node.getValue();
            XDataMap entityMap = node.entityMap;
            initMissingEntityChildren(entityService, registerManager, poolController, tempEntitiesToAttach, entity, entityMap);
        }
        addTempArrayToEntityArray(registerManager, poolController);
    }

    private void addTempArrayToEntityArray(XRegisterManager registerManager, XPoolController poolController) {
        // We add missing sub scene entities that was added later in the current scene
        XList<XEntityLoadNode> list = tempEntitiesToAttach.getNodeList();
        for(XEntityLoadNode tmpNode : list) {
            XEntity entity = tmpNode.getValue();
            int loadId = entity.getLoadId();
            XEntity lastSceneEntity = entity;
            XEntity root = entity;
            while(root != null) {
                XEntity r = root.getParent();
                if(r == null) {
                    break;
                }
                XSceneComponent sceneComponent = r.getComponent(XSceneComponent.class);
                if(sceneComponent != null) {
                    lastSceneEntity = r;
                }
                root = r;
            }

            if(!tmpNode.error) {
                XEntityLoadNode rootNode = entitiesToAttach.add(entity);
                rootNode.entityMap = tmpNode.entityMap;
                rootNode.entityJsonId = tmpNode.entityJsonId;
                rootNode.error = tmpNode.error;
                tmpNode.entityMap = null;

                XSceneComponent lastSceneComponent = lastSceneEntity.getComponent(XSceneComponent.class);

                XDataMap sceneComponentMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, rootNode.entityMap, XSceneComponent.class);
                XSceneComponent rootSceneComponent = root.getComponent(XSceneComponent.class);
//                if(lastSceneComponent != null) {
//                    String scenePath = lastSceneComponent.scenePath;
//                    int fileHandleType = lastSceneComponent.fileHandleType;
//                    XSceneComponent sceneComponent = poolController.obtainObject(XSceneComponent.class);
//                    sceneComponent.entityId = loadId;
//                    sceneComponent.scenePath = scenePath;
//                    sceneComponent.fileHandleType = fileHandleType;
//                    entity.attachComponent(sceneComponent);
//                }
//                else {
//                    if(lastSceneComponent != null) {
//                        String entityScenePath = sceneComponentMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
//                        int sceneComponentEntityId = sceneComponentMap.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
//                        int type = sceneComponentMap.getInt(XSceneComponent.MAP_FILE_TYPE, -1);
//
//                        String scenePath = rootSceneComponent.scenePath;
//                        int fileHandleType = rootSceneComponent.fileHandleType;
//                        XSceneComponent sceneComponent = poolController.obtainObject(XSceneComponent.class);
//                        sceneComponent.entityId = loadId;
//                        sceneComponent.scenePath = entityScenePath;
//                        sceneComponent.fileHandleType = type;
//                        entity.attachComponent(sceneComponent);
//                    }
//                }


            }
        }
        tempEntitiesToAttach.clear();
    }

    private void initMissingEntityChildren(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XEntity parent,
            XDataMap entityMap
    ) {
        XDataMap sceneComponentMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentMap != null) {
            String entityScenePath = sceneComponentMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int sceneComponentEntityId = sceneComponentMap.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
            int sceneFileType = sceneComponentMap.getInt(XSceneComponent.MAP_FILE_TYPE, -1);
            XDataMap sceneDataMap = loadedSubScenes.get(entityScenePath);
            if(sceneDataMap != null) {
                XDataMap sceneEntityMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneComponentEntityId);
                if(sceneEntityMap != null) {
                    XDataMapArray entityArray = XSceneMapUtils.getEntityArray(sceneEntityMap);
                    if(entityArray != null) {
                        int size = entityArray.getSize();
                        for(int i = 0; i < size; i++) {
                            XDataMap childEntityMap = entityArray.get(i);
                            int childSceneEntityId = XSceneMapUtils.getEntityID(childEntityMap);
                            boolean containsChildEntity = containsSceneEntityId(entityService, parent, childSceneEntityId);
                            if(!containsChildEntity) {
                                XEntityLoadNode childNode = addEntity(entityService, registerManager, poolController, entitiesToAttach, childEntityMap, false);
                                XDataMap subchildEntityMap = childNode.entityMap;
                                XEntity childEntity = childNode.getValue();
                                childEntity.setParent(parent);

                                XDataMap componentMap = XSceneMapUtils.addComponent(registerManager, poolController, childNode.entityMap, XSceneComponent.class);
                                if(componentMap != null) {
                                    XDataMap componentDataMap = XSceneMapUtils.putComponentDataMap(poolController, componentMap);
                                    componentDataMap.put(XSceneComponent.MAP_SCENE_PATH, entityScenePath);
                                    componentDataMap.put(XSceneComponent.MAP_ENTITY_ID, childEntity.getLoadId());
                                    componentDataMap.put(XSceneComponent.MAP_FILE_TYPE, sceneFileType);
                                }
                                XList<XIntSetNode> childList = childEntity.getChildList();
                                for(XIntSetNode node : childList) {
                                    int childId = node.getKey();
                                    XEntity child = childEntity.getChild(childId);
                                    int loadId = child.getLoadId();

                                    XDataMap childEntityMap2 = XSceneMapUtils.getEntityMapFromEntities(childNode.entityMap, loadId);
                                    if(childEntityMap2 != null) {
                                        XDataMap childComponentMap = XSceneMapUtils.addComponent(registerManager, poolController, childEntityMap2, XSceneComponent.class);
                                        if(childComponentMap != null) {
                                            XDataMap componentDataMap = XSceneMapUtils.putComponentDataMap(poolController, childComponentMap);
                                            componentDataMap.put(XSceneComponent.MAP_SCENE_PATH, entityScenePath);
                                            componentDataMap.put(XSceneComponent.MAP_ENTITY_ID, child.getLoadId());
                                            componentDataMap.put(XSceneComponent.MAP_FILE_TYPE, sceneFileType);
                                        }
                                    }
                                }

//                                initMissingEntityChildren(entityService, registerManager, poolController, entitiesToAttach, childEntity, subchildEntityMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean containsSceneEntityId(XEntityService entityService, XEntity parent, int sceneEntityId) {
        XIntSetNode cur = parent.getChildHead();
        while(cur != null) {
            int id = cur.getKey();
            XEntity childEntity = entityService.getEntity(id);
            XSceneComponent sceneComponent = childEntity.getComponent(XSceneComponent.class);
            if(sceneComponent != null) {
                if(sceneComponent.entityId == sceneEntityId) {
                    return true;
                }
            }
            cur = cur.getNext();
        }
        return false;
    }

    private XEntityLoadNode addEntity(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XDataMap entityMap,
            boolean addComponents
    ) {
        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int entityJsonId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");
        XEntityImpl entity = (XEntityImpl)entityService.obtain(entityJsonId);
        if(entity == null) {
            entity = (XEntityImpl)entityService.obtain();
        }
        entity.loadId = entityJsonId;
        XLoadEntity2.XEntityLoadNode node = entitiesToAttach.add(entity);
        node.entityMap = entityMap.clone(); // Need to clone because the parent may release this later.
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        if(addComponents) {
            addEntityComponents(registerManager, poolController, entity, entityMap);
        }

        XDataMap sceneComponentDataMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentDataMap != null) {
            String entityScenePath = sceneComponentDataMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int entityFileHandleType = sceneComponentDataMap.getInt(XSceneComponent.MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
            loadAllSubScenes(registerManager, poolController, entityScenePath, entityFileHandleType);
        }

        addEntityChildren(entityService, registerManager, poolController, entitiesToAttach, entity, entityMap, addComponents);
        return node;
    }

    private void loadAllSubScenes(XRegisterManager registerManager, XPoolController poolController, String scenePath, int fileHandleType) {
        Files.FileType fileType = XAssetUtil.getFileTypeEnum(fileHandleType);
        FileHandle fileHandle = Gdx.files.getFileHandle(scenePath, fileType);
        if(fileHandle.exists() && !loadedSubScenes.containsKey(scenePath)) {
            String jsonStr = fileHandle.readString();
            XDataMap sceneDataMap = XDataMap.obtain(poolController);
            sceneDataMap.loadJson(jsonStr);
            loadedSubScenes.put(scenePath, sceneDataMap);

            XDataMapArray sceneEntityArray = XSceneMapUtils.getEntityArray(sceneDataMap);
            if(sceneEntityArray != null) {
                for(int i = 0; i < sceneEntityArray.getSize(); i++) {
                    XDataMap entityMap = sceneEntityArray.get(i);
                    loadSubSceneFromEntity(registerManager, poolController, entityMap);
                }
            }
        }
    }

    private void loadSubSceneFromEntity(XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap) {
        XDataMap sceneComponentDataMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentDataMap != null) {
            String entityScenePath = sceneComponentDataMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int entityFileHandleType = sceneComponentDataMap.getInt(XSceneComponent.MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
            loadAllSubScenes(registerManager, poolController, entityScenePath, entityFileHandleType);
        }
        XDataMapArray childEntityArray = XSceneMapUtils.getEntityArray(entityMap);
        if(childEntityArray != null) {
            for(int i = 0; i < childEntityArray.getSize(); i++) {
                XDataMap childEntityMap = childEntityArray.get(i);
                loadSubSceneFromEntity(registerManager, poolController, childEntityMap);
            }
        }
    }

    private void addEntityChildren(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XEntity parent,
            XDataMap entityMap,
            boolean addComponents
    ) {
        XDataMapArray childEntitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childEntityMap : list) {
                int entityType = childEntityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntityLoadNode node = addEntity(entityService, registerManager, poolController, entitiesToAttach, childEntityMap, addComponents);
                    if(node != null) {
                        XEntity childEntity = node.getValue();
                        childEntity.setParent(parent);
                    }
                }
            }
        }
    }

    private static void addEntityComponents(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMap entityMap) {
        XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        if(componentArray != null) {
            int size = componentArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap componentMap = componentArray.get(i);
                addEntityComponent(registerManager, poolController, entity, componentMap);
            }
        }
    }

    private static void addEntityComponent(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMap componentMap) {
        Class<?> componentType = XSceneMapUtils.getComponentTypeFromComponentMap(registerManager, componentMap);
        if(componentType != null) {
            boolean haveComponent = entity.containsComponent(componentType);
            // Add component only if it's not set. Components loaded from sub scene may return false
            if(!haveComponent) {
                XComponent component = obtainComponent(poolController, componentMap, componentType);
                if(component != null) {
                    entity.attachComponent(component);
                }
            }
        }
    }

    private static XComponent obtainComponent(XPoolController poolController, XDataMap componentMap, Class<?> type) {
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
        public XDataMap entityMap;
        public int entityJsonId = -1;
        public boolean error;

        @Override
        public void onReset() {
            super.onReset();
            entityJsonId = -1;
            error = false;
            if(entityMap != null) {
                entityMap.free();
                entityMap = null;
            }
        }
    }
}