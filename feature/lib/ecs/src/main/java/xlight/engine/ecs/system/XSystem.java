package xlight.engine.ecs.system;

import xlight.engine.ecs.XECSWorld;

/**
 * XSystem is a processing class related ECS. Its purpose is to process entity components.
 */
public interface XSystem {
    // TODO change to interface

    boolean enabled = true;

    /**
     * Attach is used to initialize the system. It's called at the first frame step.
     */
    default void onAttach(XECSWorld world) {}

    /**
     * Detach is used to reset system state. It's called when the detach system is called.
     */
    default void onDetach(XECSWorld world) {}

    void onTick(XECSWorld world);

    default XSystemType getType() {
        return XSystemType.UPDATE;
    }
}