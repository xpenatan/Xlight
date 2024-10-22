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
import xlight.engine.list.XIntMap;
import xlight.engine.list.XList;
import xlight.engine.list.XObjectMapListRaw;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XLoadEntity2 {

    private final XObjectMapListRaw<String, XDataMap, XSceneNode> loadedSubScenes; // Cache for already loaded scenes

    private final XDataArray<XEntity, XEntityLoadNode> entitiesToAttach;

    public XLoadEntity2() {
        entitiesToAttach = new XDataArray<>(new XPool<>() {
            @Override
            protected XLoadEntity2.XEntityLoadNode newObject() {
                return new XLoadEntity2.XEntityLoadNode();
            }
        });
        loadedSubScenes = new XObjectMapListRaw<>(new XPool<XSceneNode>() {
            @Override
            protected XSceneNode newObject() {
                return new XSceneNode();
            }
        });
    }

    public void load(XWorld world, XScene scene, boolean isSubScene) {
        System.out.println("LOAD SCENE:");

        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        XDataMap sceneDataMap = scene.getSceneDataMap();

        if(XSceneMapUtils.isSceneMap(sceneDataMap)) {
            String jsonStr = sceneDataMap.saveJsonStr();
            System.out.print(jsonStr);

            loadEntities(entityService, registerManager, poolController, sceneDataMap);
            XScene subScene = isSubScene ? scene : null;
            initEntities(entityService, poolController, entitiesToAttach, subScene);
        }
    }

    public XEntity loadEntityAndAttach(XWorld world, XDataMap entityMap) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XEntity entity = null;
        if(XSceneMapUtils.isEntityMap(entityMap)) {
            loadEntities(entityService, registerManager, poolController, entityMap);
            entity = entitiesToAttach.get(0);
            initEntities(entityService, poolController, entitiesToAttach, null);
        }
        return entity;
    }

    private void loadEntities(XEntityService entityService,  XRegisterManager registerManager, XPoolController poolController, XDataMap dataMap) {
        // If the sub scene is updated, we need to add the missing sub scene entities because the current scene don't have them
        initMissingEntityChildren(registerManager, poolController, dataMap);

        // Add all entities from the data map that is possible to add.
        // Entities with Scene component is also added if they exist but needs to process is in another pass
        XDataMapArray entitiesArray = XSceneMapUtils.getEntityArray(dataMap);
        if(entitiesArray != null) {
            int size = entitiesArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap entityMap = entitiesArray.get(i);
                if(XSceneMapUtils.isEntityMap(entityMap)) {
                    addEntity(entityService, registerManager, poolController, entitiesToAttach, entityMap, true);
                }
            }
        }

        // Add components to all entities. If it has a scene component then it compare it with the other scene
        initEntitiesSceneComponents(registerManager, poolController, entitiesToAttach);
    }

    private void initEntities(
            XEntityService entityService,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XScene subScene
    ) {
        XList<XEntityLoadNode> list = entitiesToAttach.getNodeList();
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
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataMap dataMap
    ) {

        XSceneNode rootNode = new XSceneNode();
        XSceneMapUtils.addAllEntities(rootNode.entityMap, dataMap);
        XList<XDataMap> list = rootNode.entityMap.getList();
        for(XDataMap entityMap : list) {
            fixSubScenes(registerManager, poolController, rootNode, entityMap);
        }
    }

    private boolean fixSubScenes(XRegisterManager registerManager, XPoolController poolController, XSceneNode node, XDataMap entityMap) {
        // Does not support yet if subscene changes parent entities. Need to re-add the scene if the tree changes
        XDataMap sceneComponentMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        boolean returnValue = false;
        if(sceneComponentMap != null) {
            String entityScenePath = sceneComponentMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int sceneComponentEntityId = sceneComponentMap.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
            int fileHandleType = sceneComponentMap.getInt(XSceneComponent.MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
            XSceneNode subNode = loadSubScenes(poolController, entityScenePath, fileHandleType);
            if(subNode != null) {
                XDataMap sceneDataMap = subNode.getValue();
                XDataMap sceneEntityMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneComponentEntityId);
                if(sceneEntityMap != null) {
                    fixSubScenes(registerManager, poolController, subNode, sceneEntityMap);
                    XDataMapArray sceneEntityArray = XSceneMapUtils.getEntityArray(sceneEntityMap);
                    if(sceneEntityArray != null) {
                        int size = sceneEntityArray.getSize();
                        for(int i = 0; i < size; i++) {
                            XDataMap childArrayEntityMap = sceneEntityArray.get(i);
                            int childSceneEntityId = XSceneMapUtils.getEntityID(childArrayEntityMap);
                            if(childSceneEntityId != -1) {
                                XDataMap found = XSceneMapUtils.getEntityMapIdFromSceneComponent(registerManager, entityMap, childSceneEntityId);
                                boolean containsChildEntity = found != null;
                                if(!containsChildEntity) {
                                    int id = node.getUnusedId();
                                    String entityName = XSceneMapUtils.getEntityName(childArrayEntityMap);
                                    XDataMap newEntityMap = XSceneMapUtils.addEntity(poolController, id, entityName);
                                    XDataMap componentMap = XSceneMapUtils.addComponent(registerManager, poolController, newEntityMap, XSceneComponent.class);
                                    XDataMap componentDataMap = XSceneMapUtils.putComponentDataMap(poolController, componentMap);
                                    if(componentDataMap != null) {
                                        componentDataMap.put(XSceneComponent.MAP_ENTITY_ID, childSceneEntityId);
                                        componentDataMap.put(XSceneComponent.MAP_SCENE_PATH, entityScenePath);
                                        componentDataMap.put(XSceneComponent.MAP_FILE_TYPE, fileHandleType);
                                        node.entityMap.put(id, newEntityMap);
                                        XSceneMapUtils.addChildEntityMap(entityMap, newEntityMap);
                                        fixSubScenes(registerManager, poolController, subNode, childArrayEntityMap);
                                        returnValue = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        XDataMapArray sceneEntityArray = XSceneMapUtils.getEntityArray(entityMap);
        if(sceneEntityArray != null) {
            int size = sceneEntityArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap childArrayEntityMap = sceneEntityArray.get(i);
                boolean ret = fixSubScenes(registerManager, poolController, node, childArrayEntityMap);
                if(ret) {
                    returnValue = true;
                }
            }
        }

        //TODO this may not be needed
//        if(returnValue) {
//            fixSubScenes(registerManager, poolController, node, entityMap);
//        }
        return returnValue;
    }

    private XEntityLoadNode addEntity(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XDataMap entityMap,
            boolean addComponents
    ) {
        int entityId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);

        XDataMap sceneComponentData = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentData != null) {
            String scenePath = sceneComponentData.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int fileHandleType = sceneComponentData.getInt(XSceneComponent.MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
            int sceneEntityId = sceneComponentData.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
            XSceneNode node = loadSubScenes(poolController, scenePath, fileHandleType);
            if(node == null) {
                // Cant add this entity because sub scene has not been found.
                return null;
            }
            if(!node.entityMap.containsKey(sceneEntityId)) {
                // Cant add this entity because entity does not exist in sub scene
                return null;
            }
            // TODO need to validate if this entity and sceneEntityId entity is the same.
            // If the subscene remove sceneEntityId and create a new entity with the same id we cannot add this entity
            // Maybe hash of combination of timestamp and instance hashcode will fix this
        }

        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "Empty Entity");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");
        XEntityImpl entity = (XEntityImpl)entityService.obtain(entityId);
        if(entity == null) {
            entity = (XEntityImpl)entityService.obtain();
        }
        entity.loadId = entityId;
        XLoadEntity2.XEntityLoadNode node = entitiesToAttach.add(entity);
        node.entityMap = entityMap;
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        if(addComponents) {
            addEntityComponents(registerManager, poolController, entity, entityMap);
        }

        addEntityChildren(entityService, registerManager, poolController, entitiesToAttach, entity, entityMap, addComponents);
        return node;
    }

    private void loadAllSubScenes(XRegisterManager registerManager, XPoolController poolController, String scenePath, int fileHandleType) {
        XSceneNode node = loadSubScenes(poolController, scenePath, fileHandleType);
        if(node != null) {
            XDataMap sceneDataMap = node.getValue();
            XDataMapArray sceneEntityArray = XSceneMapUtils.getEntityArray(sceneDataMap);
            if(sceneEntityArray != null) {
                for(int i = 0; i < sceneEntityArray.getSize(); i++) {
                    XDataMap entityMap = sceneEntityArray.get(i);
                    loadAllSubScene(registerManager, poolController, entityMap);
                    XDataMapArray childEntityArray = XSceneMapUtils.getEntityArray(entityMap);
                    if(childEntityArray != null) {
                        for(int j = 0; j < childEntityArray.getSize(); j++) {
                            XDataMap childEntityMap = childEntityArray.get(j);
                            loadAllSubScene(registerManager, poolController, childEntityMap);
                        }
                    }
                }
            }
        }
    }

    private void loadAllSubScene(XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap) {
        XDataMap sceneComponentDataMap = XSceneMapUtils.getComponentDataMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if(sceneComponentDataMap != null) {
            String entityScenePath = sceneComponentDataMap.getString(XSceneComponent.MAP_SCENE_PATH, "");
            int entityFileHandleType = sceneComponentDataMap.getInt(XSceneComponent.MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
            loadAllSubScenes(registerManager, poolController, entityScenePath, entityFileHandleType);
        }
    }

    private XSceneNode loadSubScenes(XPoolController poolController, String scenePath, int fileHandleType) {
        XSceneNode node = loadedSubScenes.getNode(scenePath);
        if(node != null) {
            return node;
        }
        Files.FileType fileType = XAssetUtil.getFileTypeEnum(fileHandleType);
        FileHandle fileHandle = Gdx.files.getFileHandle(scenePath, fileType);
        if(fileHandle.exists()) {
            String jsonStr = fileHandle.readString();
            XDataMap sceneDataMap = XDataMap.obtain(poolController);
            sceneDataMap.loadJson(jsonStr);
            loadedSubScenes.put(scenePath, sceneDataMap);
            node = loadedSubScenes.getNode(scenePath);
            XSceneMapUtils.addAllEntities(node.entityMap, sceneDataMap);
            return node;
        }
        return null;
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
        XDataMapArray childEntitiesArray = XSceneMapUtils.getEntityArray(entityMap);
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childEntityMap : list) {
                if(XSceneMapUtils.isEntityMap(childEntityMap)) {
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
        XDataMapArray componentArray = XSceneMapUtils.getComponents(entityMap);
        if(componentArray != null) {
            int size = componentArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap componentMap = componentArray.get(i);
                addEntityComponent(registerManager, poolController, entity, componentMap);
            }
        }
    }

    private static void addEntityComponent(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMap componentMap) {
        Class<?> componentType = XSceneMapUtils.getComponentClassFromComponentMap(registerManager, componentMap);
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
            XDataMap dataMap = XSceneMapUtils.getComponentDataMapFromComponentMap(componentMap);
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
            entityMap = null;
        }
    }
    public static class XSceneNode extends XObjectMapListRaw.XObjectDataMapNode<String, XDataMap, XSceneNode> {
        public XIntMap<XDataMap> entityMap = new XIntMap<>();

        public int getUnusedId() {
            int id = 1;
            while(entityMap.containsKey(id)) {
                id++;
            }
            return id;
        }

        @Override
        public void onReset() {
            entityMap.clear();
            XDataMap sceneDataMap = getValue();
            if(sceneDataMap != null) {
                sceneDataMap.free();
            }
            super.onReset();
        }
    }
}