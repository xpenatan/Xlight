package xlight.engine.ecs.system;

public interface XSystemService {
    boolean attachSystem(XSystem system);
    <T extends XSystem> T detachSystem(Class<?> type);
    <T extends XSystem> T getSystem(Class<?> type);
    XSystemData getSystemData(Class<?> type);
    void addTickListener(XSystemType type, XSystemBeginEndListener listener);
    void removeTickListener(XSystemType type, XSystemBeginEndListener listener);

    /**
     * LOW LEVEL USAGE.
     * Return the system controller used to process the systems.
     * Controller will be created if not found.
     */
    XSystemController getSystemController(int key);

    /**
     * LOW LEVEL USAGE.
     * Return the system controller used to process the systems.
     * Controller will be created if not found.
     */
    XSystemController getSystemController(XSystem system);
}