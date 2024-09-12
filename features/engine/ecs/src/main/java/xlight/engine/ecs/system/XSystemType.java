package xlight.engine.ecs.system;

public enum XSystemType {
    UPDATE,
    TIMESTEP,
    RENDERER,
    UI;

    public boolean isUI() {
        return this == UI;
    }

    public boolean isRenderer() {
        return this == RENDERER;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isTimestep() {
        return this == TIMESTEP;
    }
}