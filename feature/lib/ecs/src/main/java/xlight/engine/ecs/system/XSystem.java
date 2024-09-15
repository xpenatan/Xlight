package xlight.engine.ecs.system;

import xlight.engine.ecs.XWorld;

/**
 * XSystem is a processing class related ECS. Its purpose is to process entity components.
 */
public interface XSystem {

    /**
     * Attach is used to initialize the system. It's called at the first frame step.
     */
    default void onAttach(XWorld world) {}

    /**
     * Detach is used to reset system state. It's called when the detach system is called.
     */
    default void onDetach(XWorld world) {}

    void onTick(XWorld world);

    default XSystemType getType() {
        return XSystemType.UPDATE;
    }

    default Class<?> getClassType() {
        return getClass();
    }
}