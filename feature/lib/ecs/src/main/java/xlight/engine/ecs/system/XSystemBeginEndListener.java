package xlight.engine.ecs.system;

import xlight.engine.ecs.XWorld;

public interface XSystemBeginEndListener {
    void onBegin(XWorld world);
    void onEnd(XWorld world);
}