package xlight.engine.impl;

import xlight.engine.core.XEngine;
import xlight.engine.ecs.XECSWorld;

public class XEngineImpl implements XEngine {

    XECSWorldImpl world;

    public XEngineImpl() {
        world = new XECSWorldImpl();
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