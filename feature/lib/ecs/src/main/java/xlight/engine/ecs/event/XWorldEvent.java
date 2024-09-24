package xlight.engine.ecs.event;

public class XWorldEvent {
    private static int i;
    public static final int EVENT_ATTACH_ENTITY = getNewEventId();
    public static final int EVENT_DETACH_ENTITY = getNewEventId();

    public static int getNewEventId() {
        i++;
        return i;
    }

    private XWorldEvent() {};
}