package xlight.engine.ecs.event;

import xlight.engine.ecs.XWorld;

public interface XEvent {
    int getId();
    <T> T getUserData();
    XWorld getWorld();
}