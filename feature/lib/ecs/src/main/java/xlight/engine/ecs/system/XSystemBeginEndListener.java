package xlight.engine.ecs.system;

import xlight.engine.ecs.XECSWorld;

public interface XSystemBeginEndListener {
    void onBegin(XECSWorld world);
    void onEnd(XECSWorld world);
}