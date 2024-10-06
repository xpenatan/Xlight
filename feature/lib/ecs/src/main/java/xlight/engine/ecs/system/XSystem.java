package xlight.engine.ecs.system;

import xlight.engine.ecs.XWorld;
import xlight.engine.pool.XClassOrInterface;

/**
 * XSystem is a processing class related ECS. Its purpose is to process entity components.
 */
public interface XSystem extends XClassOrInterface {

    int DEFAULT_CONTROLLER = 941231;

    /**
     * Attach is used to initialize the system. It's called at the first frame step.
     */
    default void onAttach(XWorld world, XSystemData systemData) {}

    /**
     * Detach is used to reset system state. It's called when the detach system is called.
     */
    default void onDetach(XWorld world, XSystemData systemData) {}

    void onTick(XWorld world);

    default XSystemType getType() {
        return XSystemType.UPDATE;
    }

    default int getSystemController() {
        return DEFAULT_CONTROLLER;
    }
}