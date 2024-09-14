package xlight.engine.ecs.event;

public interface XEventListener {
    /** Returning true will auto remove listener */
    boolean onEvent(XEvent event);
}