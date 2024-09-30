package xlight.engine.ecs.system;

import xlight.engine.list.XList;

public interface XSystemService {
    // TODO check if type hash will be the same with proguard
    boolean attachSystem(XSystem system);
    <T extends XSystem> T detachSystem(Class<?> type);
    <T extends XSystem> T getSystem(Class<?> type);
    XList<XSystemData> getSystems();
    XSystemData getSystemData(Class<?> type);
    XSystemData getSystemData(int key);
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