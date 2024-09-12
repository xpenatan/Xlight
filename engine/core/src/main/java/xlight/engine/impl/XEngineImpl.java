package xlight.engine.impl;

import xlight.engine.core.XEngine;
import xlight.engine.core.ecs.component.XCameraComponent;
import xlight.engine.core.ecs.service.camera.XCameraService;
import xlight.engine.ecs.XECSWorld;

public class XEngineImpl implements XEngine {

    XECSWorldImpl world;

    public XEngineImpl() {
        world = new XECSWorldImpl();

        XComponentServiceImpl componentService = world.componentService;
        componentService.registerComponent(XCameraComponent.class);

        XCameraServiceImpl cameraService = new XCameraServiceImpl();
        world.attachService(XCameraService.class, cameraService);
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