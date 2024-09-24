package xlight.engine.core;

import xlight.engine.ecs.event.XWorldEvent;

public class XEngineEvent {

    public static final int EVENT_CREATE = XWorldEvent.getNewEventId();
    public static final int EVENT_RESIZE = XWorldEvent.getNewEventId();
    public static final int EVENT_PAUSE = XWorldEvent.getNewEventId();
    public static final int EVENT_RESUME = XWorldEvent.getNewEventId();
    public static final int EVENT_DISPOSE = XWorldEvent.getNewEventId();

    public static final int EVENT_CLEAR_WORLD = XWorldEvent.getNewEventId();
    public static final int EVENT_SCENE_LOADED = XWorldEvent.getNewEventId();
    public static final int EVENT_SAVE_PREFERENCE = XWorldEvent.getNewEventId();
    public static final int EVENT_LOAD_PREFERENCE = XWorldEvent.getNewEventId();

    private XEngineEvent() {}
}