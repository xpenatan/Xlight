package xlight.engine.core;

import xlight.engine.ecs.XECSWorld;
import xlight.engine.impl.XEngineImpl;

public interface XEngine {
    static XEngine newInstance() { return new XEngineImpl(); }

    XECSWorld getWorld();
    void update(float deltaTime);
    void render();
}