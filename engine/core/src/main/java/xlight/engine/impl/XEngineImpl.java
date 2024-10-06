package xlight.engine.impl;

import xlight.engine.core.XEngineEvent;
import xlight.engine.core.ecs.XPreferencesManager;
import xlight.engine.asset.ecs.manager.XAssetManager;
import xlight.engine.core.XEngine;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XGameWorldComponent;
import xlight.engine.g3d.ecs.component.XGLTFComponent;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.init.ecs.service.XInitFeatureService;
import xlight.engine.json.ecs.manager.XJsonManager;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.scene.XScene;
import xlight.engine.scene.ecs.component.XSceneComponent;
import xlight.engine.scene.ecs.manager.XSceneManager;
import xlight.engine.transform.ecs.component.XLocalTransformComponent;
import xlight.engine.transform.ecs.component.XTransformComponent;
import xlight.engine.ecs.component.XUIWorldComponent;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.XWorld;

public class XEngineImpl implements XEngine {

    XECSWorldImpl world;

    public XEngineImpl() {
        world = new XECSWorldImpl();
        setupEngine();
    }

    @Override
    public XWorld getWorld() {
        return world;
    }

    @Override
    public void update(float deltaTime) {
        world.update(deltaTime);
        world.tickUpdate();
    }

    @Override
    public void render() {
        world.tickRender();
        world.tickUI();
    }

    @Override
    public void dispose() {
        XWorldService worldService = world.getWorldService();
        worldService.getEntityService().clear();
        worldService.getEventService().clear();
        world.update(2);
        worldService.getEventService().sendEvent(XEngineEvent.EVENT_DISPOSE, null, null, false);
        world = null;
    }

    private void setupEngine() {
        // Setup Camera
        XCameraManagerImpl cameraManager = new XCameraManagerImpl();
        world.attachManager(XCameraManager.class, cameraManager);

        // Setup Asset
        XAssetManagerImpl assetManager = new XAssetManagerImpl();
        XAssetServiceImpl assetService = new XAssetServiceImpl(assetManager);
        world.attachService(XAssetServiceImpl.class, assetService);
        world.attachService(XInitFeatureService.class, new XInitFeatureServiceImpl());

        world.attachManager(XAssetManager.class, assetManager);
        world.attachManager(XJsonManager.class, new XJsonManagerImpl());
        world.attachManager(XPoolManager.class, new XPoolManagerImpl());
        world.attachManager(XSceneManager.class, new XSceneManagerImpl());
        world.attachManager(XPreferencesManager.class, new XPreferencesManagerImpl());
        world.attachManager(XRegisterManager.class, new XRegisterManagerImpl(world));

        world.update(1);

        registerComponents();
    }

    private void registerComponents() {
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        world.getWorldService().getComponentService().registerComponent(XRender3DComponent.class); // Generic component use component service

        XMetaClass metaClass;
        metaClass = registerManager.registerClass(2, XSceneComponent.class, new XPool<>() { protected XComponent newObject() { return new XSceneComponent(); } });
        metaClass.setMetaClassGroup(null);
        metaClass = registerManager.registerClass(3, XGameWorldComponent.class, new XPool<>() { protected XComponent newObject() { return new XGameWorldComponent(); } });
        metaClass.setMetaClassGroup("World type");
        metaClass = registerManager.registerClass(4, XUIWorldComponent.class, new XPool<>() { protected XComponent newObject() { return new XUIWorldComponent(); } });
        metaClass.setMetaClassGroup("World type");
        metaClass = registerManager.registerClass(5, XTransformComponent.class, new XPool<>() { protected XComponent newObject() { return new XTransformComponent(); } });
        metaClass.setMetaClassGroup("Position");
        metaClass.setName("Transform");
        metaClass = registerManager.registerClass(6, XCameraComponent.class, new XPool<>() { protected XComponent newObject() { return new XCameraComponent(); } });
        metaClass.setMetaClassGroup("Camera");
        metaClass = registerManager.registerClass(7, XGLTFComponent.class, new XPool<>() { protected XComponent newObject() { return new XGLTFComponent(); } });
        metaClass.setMetaClassGroup("g3d");
        metaClass.setParentType(XRender3DComponent.class);
        metaClass = registerManager.registerClass(8, XLocalTransformComponent.class, new XPool<>() { protected XComponent newObject() { return new XLocalTransformComponent(); } });
        metaClass.setMetaClassGroup("Position");
        metaClass.setName("Local Transform");

        XPoolController poolController = world.getGlobalData(XPoolController.class);
        poolController.registerPool(XScene.class, new XPool<>() { protected XScene newObject() { return new XSceneImpl(poolController); } });
    }
}