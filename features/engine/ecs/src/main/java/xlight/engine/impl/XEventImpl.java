package xlight.engine.impl;


import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.pool.XPoolable;

public class XEventImpl implements XEvent, XPoolable {
    public int id;
    public Object userData;
    public XEventService.XSendEventListener listener;

    @Override
    public int getID() {
        return id;
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
    }
}