package xlight.demo.basic;

import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.engine.core.ecs.component.XCameraComponent;
import xlight.engine.core.ecs.component.XGameComponent;
import xlight.engine.core.ecs.component.XModelComponent;
import xlight.engine.core.ecs.component.XTransformComponent;
import xlight.engine.core.ecs.system.XCameraSystem;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.core.ecs.system.XModelSystem;
import xlight.engine.ecs.system.XSystemType;

public class MainApp implements XApplication {

    @Override
    public void onSetup(XEngine engine) {

        XECSWorld world = engine.getWorld();
        XSystemService systemService = world.getSystemService();

        systemService.attachSystem(new XCameraSystem(XSystemType.GAME));
        systemService.attachSystem(new XModelSystem(XSystemType.GAME));

        XEntityService es = world.getEntityService();
        XComponentService cs = world.getComponentService();

        createCameraEntity(es, cs);
        createModelEntity(es, cs);
    }

    public void createCameraEntity(XEntityService es, XComponentService cs) {
        XEntity cameraEntity = es.obtain();
        cs.attachComponent(cameraEntity, new XTransformComponent().position(0, 0, -10));
        cs.attachComponent(cameraEntity, new XCameraComponent());
        cs.attachComponent(cameraEntity, new XGameComponent());
    }

    public void createModelEntity(XEntityService es, XComponentService cs) {
        XEntity cameraEntity = es.obtain();
        cs.attachComponent(cameraEntity, new XModelComponent());
        cs.attachComponent(cameraEntity, new XTransformComponent().position(0, 0, 0));
        cs.attachComponent(cameraEntity, new XGameComponent());
    }
}