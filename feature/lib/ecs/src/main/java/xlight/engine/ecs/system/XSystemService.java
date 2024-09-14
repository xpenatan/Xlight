package xlight.engine.ecs.system;

public interface XSystemService {
    void attachSystem(XSystem system);
    <T extends XSystem> T detachSystem(Class<?> type);
    <T extends XSystem> T getSystem(Class<?> type);
    <T extends XSystem> XSystemData getSystemData(Class<T> type);
    void addTickListener(XSystemType type, XSystemBeginEndListener listener);
    void removeTickListener(XSystemType type, XSystemBeginEndListener listener);
}