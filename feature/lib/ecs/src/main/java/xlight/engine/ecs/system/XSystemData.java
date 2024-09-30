package xlight.engine.ecs.system;

public interface XSystemData {
    boolean isEnabled();
    void setEnabled(boolean flag);
    boolean isForceUpdate();
    void setForceUpdate(boolean flag);
    XSystem getSystem();
    int getKey();
}