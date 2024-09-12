package xlight.engine.impl;

import xlight.engine.core.XEngine;
import xlight.engine.core.ecs.component.XCameraComponent;
import xlight.engine.core.ecs.component.XGameComponent;
import xlight.engine.core.ecs.component.XModelComponent;
import xlight.engine.core.ecs.component.XTransformComponent;
import xlight.engine.core.ecs.component.XUIComponent;
import xlight.engine.core.ecs.service.camera.XCameraService;
import xlight.engine.ecs.XECSWorld;

public class XEngineImpl implements XEngine {

    XECSWorldImpl world;

    public XEngineImpl() {
        world = new XECSWorldImpl();

        registerComponents();

        XCameraServiceImpl cameraService = new XCameraServiceImpl();
        world.attachService(XCameraService.class, cameraService);
    }

    private void registerComponents() {
        XComponentServiceImpl componentService = world.componentService;
        componentService.registerComponent(XGameComponent.class);
        componentService.registerComponent(XUIComponent.class);
        componentService.registerComponent(XTransformComponent.class);
        componentService.registerComponent(XCameraComponent.class);
        componentService.registerComponent(XModelComponent.class);
    }

    @Override
    public XECSWorld getWorld() {
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
}