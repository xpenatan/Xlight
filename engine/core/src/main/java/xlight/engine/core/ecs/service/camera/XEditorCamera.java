package xlight.engine.core.ecs.service.camera;

import xlight.engine.camera.XCamera;

public interface XEditorCamera {
    void setGameEditorCam(XCamera camera);
    void setGuiEditorCam(XCamera camera);
}