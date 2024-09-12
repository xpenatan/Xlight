package xlight.demo.basic;

import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.engine.core.ecs.system.XCameraSystem;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.core.ecs.system.XModelSystem;
import xlight.engine.ecs.system.XSystemType;

public class MainApp implements XApplication {

    @Override
    public void onSetup(XEngine engine) {

        XECSWorld world = engine.getWorld();
        XSystemService systemService = world.getSystemService();

        systemService.attachSystem(new XCameraSystem(XSystemType.RENDERER));
        systemService.attachSystem(new XModelSystem(XSystemType.RENDERER));
    }
}