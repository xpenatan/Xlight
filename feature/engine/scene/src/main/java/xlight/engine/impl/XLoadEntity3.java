package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Map;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

public class XLoadEntity3 {
    private final Map<String, XScene> loadedScenes = new HashMap<>(); // Cache for already loaded scenes

    public void load(XWorld world, XScene scene, boolean isSubScene) {
        load(world, scene, isSubScene, null);
    }

    private void load(XWorld world, XScene scene, boolean isSubScene, XEntity parentEntity) {
        System.out.println("LOAD SCENE: " + scene.getPath());

        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        String path = scene.getPath();
        XDataMap sceneDataMap = scene.getSceneDataMap();

        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if (sceneType == XSceneTypeValue.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if (entitiesArray != null) {
                for (int i = 0; i < entitiesArray.getSize(); i++) {
                    XDataMap entityMap = entitiesArray.get(i);
                    addEntity(world, entityService, registerManager, poolController, entityMap, path, parentEntity);
                }
            }
        }

        if (isSubScene) {
            loadedScenes.put(scene.getPath(), scene);
        }
    }

    public XEntity loadEntityAndAttach(XWorld world, XDataMap entityMap) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XEntity entity = addEntity(world, entityService, registerManager, poolController, entityMap, null, null);
        if (entity != null) {
            entityService.attachEntity(entity);
        }
        return entity;
    }

    private XEntity addEntity(
            XWorld world,
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XDataMap entityMap,
            String curPath,
            XEntity parentEntity
    ) {
        XEntity entity = createEntity(world, entityService, registerManager, poolController, entityMap, curPath);
        if (entity == null) {
            return null;
        }

        if (parentEntity != null) {
            entity.setParent(parentEntity);
        }

        addEntityChildren(world, entityService, registerManager, poolController, entity, entityMap, curPath);
        return entity;
    }

    private void addEntityChildren(
            XWorld world,
            XEntityService entityService,
            XRegisterManager registerManager,
            XPoolController poolController,
            XEntity parent,
            XDataMap entityMap,
            String curPath
    ) {
        XDataMapArray childEntitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
        if (childEntitiesArray != null) {
            for (int i = 0; i < childEntitiesArray.getSize(); i++) {
                XDataMap childEntityMap = childEntitiesArray.get(i);
                int entityType = childEntityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if (entityType == XSceneTypeValue.ENTITY.getValue()) {
                    XEntity childEntity = addEntity(world, entityService, registerManager, poolController, childEntityMap, curPath, parent);
                    if (childEntity != null) {
                        childEntity.setParent(parent);
                    }
                }
            }
        }
    }

    private XEntity createEntity(XWorld world, XEntityService entityService, XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap, String curPath) {
        String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
        boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
        boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
        int entityJsonId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");

        XEntityImpl entity = (XEntityImpl)entityService.obtain(entityJsonId);
        if (entity == null) {
            // Getting an entity with the same ID as the scene did not work because its already being used.
            // Use a new entity
            entity = (XEntityImpl)entityService.obtain();
        }

        entity.loadId = entityJsonId;
        entity.setVisible(isVisible);
        entity.setName(entityName);
        entity.setSavable(true);

        // Add components
        addEntityComponents(registerManager, poolController, entity, entityMap);

        // Load sub-scene if SceneComponent is present
        XDataMap sceneComponentMap = XSceneMapUtils.getComponentMapFromEntityMap(registerManager, entityMap, XSceneComponent.class);
        if (sceneComponentMap != null) {
            addEntityComponent(registerManager, poolController, entity, sceneComponentMap);
            XSceneComponent sceneComponent = entity.getComponent(XSceneComponent.class);
            loadSubScene(world, entity, sceneComponent);
        }

        return entity;
    }

    private void loadSubScene(XWorld world, XEntity entity, XSceneComponent sceneComponent) {
        if (sceneComponent.entityId == -1 || sceneComponent.scenePath == null || sceneComponent.scenePath.trim().isEmpty()) {
            return;
        }

        String scenePath = sceneComponent.scenePath;
        if (!loadedScenes.containsKey(scenePath)) {
            // Load the sub-scene
            Files.FileType fileType = XAssetUtil.getFileTypeEnum(sceneComponent.fileHandleType);
            FileHandle fileHandle = Gdx.files.getFileHandle(sceneComponent.scenePath, fileType);
            if (fileHandle.exists()) {
                XPoolController poolController = world.getGlobalData(XPoolController.class);
                XSceneImpl subScene = new XSceneImpl(poolController);
                subScene.setPath(scenePath);
                subScene.setFileType(fileType);
                load(world, subScene, true, entity);
            } else {
                System.err.println("Scene path not found: " + fileType + " - " + sceneComponent.scenePath);
            }
        } else {
            // Sub-scene already loaded, clone entities from the loaded scene
            XScene loadedScene = loadedScenes.get(scenePath);
            XDataMap subSceneDataMap = loadedScene.getSceneDataMap();
            XDataMapArray entitiesArray = subSceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if (entitiesArray != null) {
                for (int i = 0; i < entitiesArray.getSize(); i++) {
                    XDataMap subEntityMap = entitiesArray.get(i);
                    // Clone the entity and attach to the current entity
                    XEntity clonedEntity = loadEntityAndAttach(world, subEntityMap);
                    if (clonedEntity != null) {
                        clonedEntity.setParent(entity);
                    }
                }
            }
        }
    }

    private void addEntityComponents(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMap entityMap) {
        XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        if (componentArray != null) {
            for (int i = 0; i < componentArray.getSize(); i++) {
                XDataMap componentMap = componentArray.get(i);
                addEntityComponent(registerManager, poolController, entity, componentMap);
            }
        }
    }

    private void addEntityComponent(XRegisterManager registerManager, XPoolController poolController, XEntity entity, XDataMap componentMap) {
        Class<?> componentType = XSceneMapUtils.getComponentTypeFromComponentMap(registerManager, componentMap);
        if (componentType != null) {
            if (!entity.containsComponent(componentType)) {
                XComponent component = obtainComponent(poolController, componentMap, componentType);
                if (component != null) {
                    entity.attachComponent(component);
                }
            }
        }
    }

    private XComponent obtainComponent(XPoolController poolController, XDataMap componentMap, Class<?> type) {
        Object o = poolController.obtainObject(type);
        if (o instanceof XComponent) {
            XComponent component = (XComponent) o;
            XDataMap dataMap = componentMap.getDataMap(XSceneKeys.DATA.getKey());
            if (dataMap != null && component instanceof XDataMapListener) {
                ((XDataMapListener) component).onLoad(dataMap);
            }
            return component;
        } else {
            return null;
        }
    }
}