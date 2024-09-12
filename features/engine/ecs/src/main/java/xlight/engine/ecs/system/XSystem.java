package xlight.engine.ecs.system;

import xlight.engine.ecs.XECSWorld;

public abstract class XSystem {

    boolean enabled = true;

    public void onAttach(XECSWorld world) {}
    public void onDetach(XECSWorld world) {}
    public abstract void onTick();

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