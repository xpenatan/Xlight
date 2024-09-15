package xlight.engine.ecs.event;

import xlight.engine.ecs.XWorld;

public interface XEvent {
    static final int EVENT_CREATE = 1;
    static final int EVENT_RESIZE = 2;
    static final int EVENT_PAUSE = 3;
    static final int EVENT_RESUME = 4;
    static final int EVENT_DISPOSE = 5;

    int getID();
    <T> T getUserData();
    XWorld getWorld();
}