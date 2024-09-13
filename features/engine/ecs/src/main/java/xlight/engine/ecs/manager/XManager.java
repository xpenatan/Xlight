package xlight.engine.ecs.manager;

import xlight.engine.ecs.XECSWorld;

public interface XManager {
    void onAttach(XECSWorld world);
    void onDetach(XECSWorld world);
}