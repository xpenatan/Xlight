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
        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(entitiesArray != null) {
                int size = entitiesArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap entityMap = entitiesArray.get(i);
                    addEntity(entityService, registerManager, poolController, entitiesToAttach, entityMap);
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
        XEntity entity = addEntity(entityService, registerManager, poolController, entitiesToAttach, entityMap);
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

        initEntitySceneComponent(registerManager, poolController, entitiesToAttach);

        // If the sub scene is updated, we need to add the missing sub scene entities because the current scene don't have them
        initMissingEntityChildren(entityService, registerManager, poolController, entitiesToAttach);

        // Add components to all entities. If it has a scene component then it compare it with the other scene
        initEntitiesComponents(registerManager, poolController, entitiesToAttach);

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
    }

    private static void initEntitiesComponents(
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach
    ) {
        for(XEntityLoadNode node : entitiesToAttach.getNodeList()) {
            XEntity entity = node.getValue();
            XDataMap entityMap = node.entityMap;
            if(node.sceneEntityMap != null) {
                XDataMapArray subSceneComponentArray = XSceneMapUtils.getComponentArrayFromEntityMap(node.sceneEntityMap);
                if(subSceneComponentArray != null) {
                    for(int i = 0; i < subSceneComponentArray.getSize(); i++) {
                        XDataMap subSceneComponentMap = subSceneComponentArray.get(i);
                        Class<?> componentType = XSceneMapUtils.getComponentTypeFromComponentMap(registerManager, subSceneComponentMap);
                        // If current entity don't have the component we get it from the subSceneComponent map.
                        XDataMap componentMap = XSceneMapUtils.getComponentMapFromEntityMap(registerManager, entityMap, componentType);
                        if(componentMap == null) {
                            componentMap = subSceneComponentMap;
                        }
                        addEntityComponent(registerManager, poolController, entity, componentMap);
                    }
                }
                XDataMapArray sceneComponentArray = XSceneMapUtils.getComponentArrayFromEntityMap(node.entityMap);
                if(sceneComponentArray != null) {
                    for(int i = 0; i < sceneComponentArray.getSize(); i++) {
                        XDataMap subSceneComponentMap = sceneComponentArray.get(i);
                        Class<?> componentType = XSceneMapUtils.getComponentTypeFromComponentMap(registerManager, subSceneComponentMap);
                        XDataMap componentMap = XSceneMapUtils.getComponentMapFromEntityMap(registerManager, entityMap, componentType);
                        if(componentMap != null) {
                            addEntityComponent(registerManager, poolController, entity, componentMap);
                        }
                    }
                }
            }
            else {
                // Add components
                addEntityComponents(registerManager, poolController, entity, entityMap);
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

    private void initEntitySceneComponent(
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach
    ) {
        for(XEntityLoadNode node : entitiesToAttach.getNodeList()) {
            XEntity entity = node.getValue();
            XDataMap entityMap = node.entityMap;
            XDataMap sceneComponentMap = XSceneMapUtils.getComponentMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
            if(sceneComponentMap != null) {
                addEntityComponent(registerManager, poolController, entity, sceneComponentMap);
                XSceneComponent sceneComponent = entity.getComponent(XSceneComponent.class);
                XDataMap sceneEntityMap = getEntityMap(poolController, sceneComponent);
                if(sceneEntityMap != null) {
                    node.sceneEntityMap = sceneEntityMap;
                    node.entityJsonId = sceneComponent.entityId;
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
            XEntity parent = node.getValue();
            int childrenSize = parent.getChildList().getSize();
            XDataMap sceneEntityMap = node.sceneEntityMap;
            if(sceneEntityMap != null && childrenSize > 0) {
                XDataMapArray entityArray = XSceneMapUtils.getEntityChildrenArray(sceneEntityMap);
                if(entityArray != null) {
                    for(int i = 0; i < entityArray.getSize(); i++) {
                        XDataMap childEntityMap = entityArray.get(i);
                        int childSceneEntityId = XSceneMapUtils.getEntityID(childEntityMap);
                        boolean containsChildEntity = containsSceneEntityId(entityService, parent, childSceneEntityId);
                        if(!containsChildEntity) {
                            XEntity childEntity = addEntity(entityService, registerManager, poolController, tempEntitiesToAttach, childEntityMap);
                            if(childEntity != null) {
                                childEntity.setParent(parent);
                            }
                        }
                    }
                }
            }
        }

        // We add missing sub scene entities that was added later in the current scene
        XList<XEntityLoadNode> list = tempEntitiesToAttach.getNodeList();
        for(XEntityLoadNode tmpNode : list) {
            XEntity entity = tmpNode.getValue();
            XEntity parent = entity.getParent();
            if(parent != null && !tmpNode.error) {
                XSceneComponent parentSceneComponent = parent.getComponent(XSceneComponent.class);
                if(parentSceneComponent != null) {
                    XEntityLoadNode rootNode = entitiesToAttach.add(entity);
                    rootNode.entityMap = tmpNode.entityMap;
                    rootNode.sceneEntityMap = tmpNode.sceneEntityMap;
                    rootNode.entityJsonId = tmpNode.entityJsonId;
                    rootNode.error = tmpNode.error;
                    tmpNode.entityMap = null;
                    tmpNode.sceneEntityMap = null;
                    // Need to add scene component because this entity is new
                    XSceneComponent sceneComponent = poolController.obtainObject(XSceneComponent.class);
                    sceneComponent.scenePath = parentSceneComponent.scenePath;
                    sceneComponent.fileHandleType = parentSceneComponent.fileHandleType;
                    sceneComponent.entityId = entity.getLoadId(); // Since we added entity from a file, we can use the loaded id
                    entity.attachComponent(sceneComponent);
                }
            }
        }
        tempEntitiesToAttach.clear();
    }

    private static XEntity addEntity(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XDataMap entityMap
    ) {
        XEntityLoadNode node = setupEntity(entityService, entitiesToAttach, entityMap, true);
        if(node == null) {
            return null;
        }
        XEntity entity = node.getValue();

        addEntityChildren(entityService, registerManager, poolController, entitiesToAttach, entity, entityMap);
        return entity;
    }

    private static void addEntityChildren(
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataArray<XEntity, XEntityLoadNode> entitiesToAttach,
            XEntity parent,
            XDataMap entityMap
    ) {
        XDataMapArray childEntitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if(childEntitiesArray != null) {
            XList<XDataMap> list = childEntitiesArray.getList();
            for(XDataMap childEntityMap : list) {
                int entityType = childEntityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntity childEntity = addEntity(entityService, registerManager, poolController, entitiesToAttach, childEntityMap);
                    if(childEntity != null) {
                        childEntity.setParent(parent);
                    }
                }
            }
        }
    }

    private static XEntityLoadNode setupEntity(XEntityService entityService, XDataArray<XEntity, XEntityLoadNode> entitiesToAttach, XDataMap entityMap, boolean obtainIfFails) {
        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int entityJsonId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");
        XEntityImpl entity = (XEntityImpl)entityService.obtain(entityJsonId);
        if(entity == null) {
            // Getting an entity with the same ID as the scene did not work because its already being used.
            // Use a new entity
            if(obtainIfFails) {
                entity = (XEntityImpl)entityService.obtain();
            }
            else {
                return null;
            }
        }
        XDataMap cloneEntityMap = entityMap.clone();
        entity.loadId = entityJsonId;
        XLoadEntity2.XEntityLoadNode node = entitiesToAttach.add(entity);
        node.entityMap = cloneEntityMap;
        entity.setVisible(isVisible);
        entity.setName(entityName);
//            entity.setTag(tag);
        entity.setSavable(true);

        return node;
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

    /**
     * Must free entity
     */
    private static XDataMap getEntityMap(XPoolController poolController, XSceneComponent sceneComponent) {
        if(sceneComponent.entityId == -1 || sceneComponent.scenePath == null || sceneComponent.scenePath.trim().isEmpty()) {
            return null;
        }

        Files.FileType fileType = XAssetUtil.getFileTypeEnum(sceneComponent.fileHandleType);
        FileHandle fileHandle = Gdx.files.getFileHandle(sceneComponent.scenePath, fileType);
        if(fileHandle.exists()) {
            String jsonStr = fileHandle.readString();
            XDataMap sceneDataMap = XDataMap.obtain(poolController);
            sceneDataMap.loadJson(jsonStr);
            XDataMap entityMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneComponent.entityId);
            if(entityMap != null) {
                entityMap = entityMap.clone();
            }
            sceneDataMap.free();
            return entityMap;
        }
        else {
            System.err.println("Scene path not found: " + fileType + " - " + sceneComponent.scenePath);
        }
        return null;
    }

    public static class XEntityLoadNode extends XDataArray.XDataArrayNode<XEntity> {
        public XDataMap entityMap;
        public XDataMap sceneEntityMap; // if entity contains XSceneComponent, this datamap will be the loaded entity map
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
            if(sceneEntityMap != null) {
                sceneEntityMap.free();
                sceneEntityMap = null;
            }
        }
    }
}