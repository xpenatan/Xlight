package xlight.engine.core;

import xlight.engine.ecs.XECSWorld;

public interface XEngine {
    XECSWorld getWorld();
    void update(float deltaTime);
    void render();
}