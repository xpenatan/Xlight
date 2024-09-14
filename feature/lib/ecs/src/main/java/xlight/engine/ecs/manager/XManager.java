package xlight.engine.ecs.manager;

import xlight.engine.ecs.XECSWorld;

public interface XManager {
    default void onAttach(XECSWorld world) {}
    default void onDetach(XECSWorld world) {}
}