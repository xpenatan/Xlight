package xlight.engine.ecs.system;

import xlight.engine.ecs.XECSWorld;

/**
 * XSystem is a processing class related ECS. Its purpose is to process entity components.
 */
public abstract class XSystem {
    // TODO change to interface

    boolean enabled = true;

    /**
     * Attach is used to initialize the system. It's called at the first frame step.
     */
    public void onAttach(XECSWorld world) {}

    /**
     * Detach is used to reset system state. It's called when the detach system is called.
     */
    public void onDetach(XECSWorld world) {}

    public abstract void onTick(XECSWorld world);

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public XSystemType getType() {
        return XSystemType.UPDATE;
    }
}