package xlight.engine.ecs.event;

import xlight.engine.ecs.XECSWorld;

public interface XEvent {
    static final int EVENT_CREATE = 1;
    static final int EVENT_RESIZE = 1;
    static final int EVENT_DISPOSE = 2;
    static final int EVENT_PAUSE = 2;
    static final int EVENT_RESUME = 2;

    int getID();
    <T> T getUserData();
    XECSWorld getWorld();
}