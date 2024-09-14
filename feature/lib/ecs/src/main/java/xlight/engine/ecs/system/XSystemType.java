package xlight.engine.ecs.system;

public enum XSystemType {
    UPDATE,
    TIMESTEP,
    RENDER,
    UI;

    public boolean isUI() {
        return this == UI;
    }

    public boolean isRender() {
        return this == RENDER;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isTimestep() {
        return this == TIMESTEP;
    }
}