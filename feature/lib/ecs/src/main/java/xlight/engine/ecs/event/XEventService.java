package xlight.engine.ecs.event;

public interface XEventService {
    void sendEvent(int id);
    void sendEvent(int id, Object userData);
    void sendEvent(int id, XSendEventListener listener);
    void sendEvent(int id, Object userData, XSendEventListener listener);
    void sendEvent(int id, Object userData, XSendEventListener listener, boolean isAsync);

    /** Clear all processing events */
    void clear();
    boolean addEventListener(int eventID, XEventListener listener);
    boolean removeEventListener(int eventID, XEventListener listener);

    interface XSendEventListener {
        default void onBeginEvent(XEvent event) {}
        default void onEndEvent(XEvent event) {}
    }
}