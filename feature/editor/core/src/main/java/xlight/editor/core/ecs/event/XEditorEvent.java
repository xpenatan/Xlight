package xlight.editor.core.ecs.event;

import xlight.engine.ecs.event.XWorldEvent;

public class XEditorEvent {
    public static final int EVENT_EDITOR_READY = XWorldEvent.getNewEventId();
    public static final int EVENT_NEW_PROJECT = XWorldEvent.getNewEventId();
    public static final int EVENT_ENGINE_CREATED = XWorldEvent.getNewEventId();
    public static final int EVENT_ENGINE_DISPOSED = XWorldEvent.getNewEventId();

    private XEditorEvent() {}
}