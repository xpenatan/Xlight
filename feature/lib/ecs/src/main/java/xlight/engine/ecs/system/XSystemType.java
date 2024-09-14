package xlight.engine.ecs.system;

public enum XSystemType {
    UPDATE,
    TIMESTEP,
    GAME,
    UI;

    public boolean isUI() {
        return this == UI;
    }

    public boolean isGame() {
        return this == GAME;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isTimestep() {
        return this == TIMESTEP;
    }
}