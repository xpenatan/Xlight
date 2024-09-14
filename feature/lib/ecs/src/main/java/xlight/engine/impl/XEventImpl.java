package xlight.engine.impl;


import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.pool.XPoolable;

public class XEventImpl implements XEvent, XPoolable {
    public int id;
    public Object userData;
    public XEventService.XSendEventListener listener;
    public XECSWorld world;

    @Override
    public int getID() {
        return id;
    }

    @Override
    public XECSWorld getWorld() {
        return world;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void onReset() {
        id = 0;
        userData = null;
        listener = null;
        world = null;
    }
}