package xlight.engine.impl;

import xlight.engine.core.XEngineEvent;
import xlight.engine.core.ecs.XPreferencesManager;
import xlight.engine.asset.ecs.manager.XAssetManager;
import xlight.engine.core.XEngine;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.g3d.ecs.component.XGLTFComponent;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.init.ecs.service.XInitFeatureService;
import xlight.engine.json.ecs.manager.XJsonManager;
import xlight.engine.pool.XPool;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.scene.ecs.manager.XSceneManager;
import xlight.engine.transform.ecs.component.XTransformComponent;
import xlight.engine.ecs.component.XUIComponent;
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
        world.getEntityService().clear();
        world.eventService.clear();
        update(2);
        world.getEventService().sendEvent(XEngineEvent.EVENT_DISPOSE, null, null, false);
        world = null;
    }

    private void setupEngine() {
        registerComponents();

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
    }

    private void registerComponents() {
        XComponentServiceImpl componentService = world.componentService;
        componentService.registerComponent(XRender3DComponent.class); // Abstract component so there is no pool
        componentService.registerComponent(XGameComponent.class, new XPool<>() { protected XComponent newObject() { return new XGameComponent(); } });
        componentService.registerComponent(XUIComponent.class, new XPool<>() { protected XComponent newObject() { return new XUIComponent(); } });
        componentService.registerComponent(XTransformComponent.class, new XPool<>() { protected XComponent newObject() { return new XTransformComponent(); } });
        componentService.registerComponent(XCameraComponent.class, new XPool<>() { protected XComponent newObject() { return new XCameraComponent(); } });
        componentService.registerComponent(XGLTFComponent.class, new XPool<>() { protected XComponent newObject() { return new XGLTFComponent(); } });
    }
}