package xlight.editor.core.ecs.event;

import xlight.engine.ecs.event.XWorldEvent;

public class XEditorEvent {
    public static final int EVENT_EDITOR_READY = XWorldEvent.getNewEventId();
    public static final int EVENT_NEW_PROJECT = XWorldEvent.getNewEventId();
    public static final int EVENT_ENGINE_CREATED = XWorldEvent.getNewEventId();
    public static final int EVENT_ENGINE_DISPOSED = XWorldEvent.getNewEventId();
    public static final int EVENT_EDITOR_COPY_ENTITY = XWorldEvent.getNewEventId();
    public static final int EVENT_EDITOR_PASTE_ENTITY = XWorldEvent.getNewEventId();

    private XEditorEvent() {}
}