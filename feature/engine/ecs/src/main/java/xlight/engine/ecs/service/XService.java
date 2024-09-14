package xlight.engine.ecs.service;

import xlight.engine.ecs.XECSWorld;

/**
 * XService is a processing class anything related to the game or other services. It's called every frame.
 */
public interface XService {
    void onAttach(XECSWorld world);
    void onDetach(XECSWorld world);
    void onTick(XECSWorld world);
}