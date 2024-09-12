package xlight.demo.basic;

import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.g3d.ecs.system.X3DRendererSystem;

public class MainApp implements XApplication {

    @Override
    public void onSetup(XEngine engine) {

        XECSWorld world = engine.getWorld();
        XSystemService systemService = world.getSystemService();

        systemService.attachSystem(new X3DRendererSystem());
    }
}