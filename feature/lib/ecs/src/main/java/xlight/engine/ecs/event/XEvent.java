package xlight.engine.ecs.event;

import xlight.engine.ecs.XWorld;

public interface XEvent {
    int getID();
    <T> T getUserData();
    XWorld getWorld();
}