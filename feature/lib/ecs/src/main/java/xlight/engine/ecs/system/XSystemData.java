package xlight.engine.ecs.system;

public interface XSystemData {
    boolean isEnabled();
    void setEnabled(boolean flag);
    XSystem getSystem();
}