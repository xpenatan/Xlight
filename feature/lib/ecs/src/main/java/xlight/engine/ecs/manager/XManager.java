package xlight.engine.ecs.manager;

import xlight.engine.ecs.XWorld;

public interface XManager {
    /**
     * Attach is used to initialize the manager. It's called at the first frame step.
     */
    default void onAttach(XWorld world) {}

    /**
     * Detach is used to reset manager state. It's called when the detach manager is called.
     */
    default void onDetach(XWorld world) {}
}