package xlight.engine.impl;

import xlight.editor.core.XEngineEvent;
import xlight.engine.asset.ecs.manager.XAssetManager;
import xlight.engine.core.XEngine;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.init.ecs.service.XInitFeatureService;
import xlight.engine.json.ecs.manager.XJsonManager;
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
        world.tickUpdate(deltaTime);
    }

    @Override
    public void render() {
        world.tickRender();
        world.tickUI();
    }

    @Override
    public void dispose() {
        world.getEntityService().clear();
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
    }

    private void registerComponents() {
        XComponentServiceImpl componentService = world.componentService;
        componentService.registerComponent(XGameComponent.class);
        componentService.registerComponent(XUIComponent.class);
        componentService.registerComponent(XTransformComponent.class);
        componentService.registerComponent(XCameraComponent.class);
        componentService.registerComponent(XRender3DComponent.class);
    }
}