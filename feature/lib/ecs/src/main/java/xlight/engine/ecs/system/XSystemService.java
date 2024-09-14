package xlight.engine.ecs.system;

public interface XSystemService {
    void attachSystem(XSystem system);
    <T extends XSystem> T detachSystem(Class<T> type);
    <T extends XSystem> T getSystem(Class<T> type);
    <T extends XSystem> XSystemData getSystemData(Class<T> type);
}