package xlight.engine.ecs.service;

import xlight.engine.ecs.XECSWorld;

/**
 * XService is a processing class anything related to the game or other services. It's called every frame.
 */
public interface XService {

    /**
     * Attach is used to initialize the service. It's called at the first frame step.
     */
    default void onAttach(XECSWorld world) {};
    /**
     * Detach is used to reset service state. It's called when the detach service is called.
     */
    default void onDetach(XECSWorld world) {};
    void onTick(XECSWorld world);
}