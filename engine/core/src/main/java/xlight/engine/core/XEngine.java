package xlight.engine.core;

import xlight.engine.ecs.XWorld;
import xlight.engine.impl.XEngineImpl;

public interface XEngine {
    static XEngine newInstance() { return new XEngineImpl(); }

    XWorld getWorld();
    void update(float deltaTime);
    void render();
    void dispose();
}