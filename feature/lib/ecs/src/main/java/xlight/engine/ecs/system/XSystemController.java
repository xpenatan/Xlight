package xlight.engine.ecs.system;

public interface XSystemController {
    void tickTimeStepSystem();
    void tickUpdateSystem();
    void tickRenderSystem();
    void tickUISystem();

    void addTickListener(XSystemType type, XSystemBeginEndListener listener);
    void removeTickListener(XSystemType type, XSystemBeginEndListener listener);
}