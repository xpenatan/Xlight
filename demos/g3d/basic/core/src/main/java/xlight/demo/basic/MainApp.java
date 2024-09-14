package xlight.demo.basic;

import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.g3d.ecs.component.XBox3DComponent;
import xlight.engine.transform.ecs.component.XTransformComponent;
import xlight.engine.core.ecs.system.XCameraSystem;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.g3d.ecs.system.XRender3DSystem;
import xlight.engine.ecs.system.XSystemType;

public class MainApp implements XApplication {

    @Override
    public void onSetup(XEngine engine) {

        XECSWorld world = engine.getWorld();
        XSystemService systemService = world.getSystemService();

        systemService.attachSystem(new XCameraSystem(XSystemType.GAME));
        systemService.attachSystem(new XRender3DSystem(XSystemType.GAME));

        XEntityService es = world.getEntityService();
        XComponentService cs = world.getComponentService();

        createCameraEntity(es, cs);
        createModelEntity(es, cs);
    }

    public void createCameraEntity(XEntityService es, XComponentService cs) {
        XEntity e = es.obtain();
        cs.attachComponent(e, new XTransformComponent().position(0, 0, -10));
        cs.attachComponent(e, new XCameraComponent());
        cs.attachComponent(e, new XGameComponent());
        es.attachEntity(e);
    }

    public void createModelEntity(XEntityService es, XComponentService cs) {
        XEntity e = es.obtain();
        cs.attachComponent(e, new XBox3DComponent());
        cs.attachComponent(e, new XTransformComponent().position(0, 0, 0));
        cs.attachComponent(e, new XGameComponent());
        es.attachEntity(e);
    }
}