package xlight.engine.ecs.service;

import xlight.engine.ecs.XECSWorld;

public interface XService {
    void onAttach(XECSWorld world);
    void onDetach(XECSWorld world);
}