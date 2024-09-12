package xlight.engine.impl;


import xlight.engine.camera.XCamera;
import xlight.engine.core.ecs.service.camera.XCameraService;
import xlight.engine.core.ecs.service.camera.XEditorCamera;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.service.XService;

class XCameraServiceImpl implements XCameraService, XEditorCamera, XService {

    public XCamera gameCamera;
    public XCamera guiCamera;

    public XCamera gameEditorCamera;
    public XCamera guiEditorCamera;

    public boolean allowReplace = true;

    public XCameraServiceImpl() {
    }

    @Override
    public void clear() {
        gameEditorCamera = null;
        guiEditorCamera = null;
        gameCamera = null;
        guiCamera = null;
    }

    @Override
    public void setGameCamera(XCamera camera) {
        if(camera == gameCamera) {
            return;
        }
        XCameraImpl newCam = (XCameraImpl)camera;
        XCameraImpl oldCam = (XCameraImpl)gameCamera;
        if(oldCam != null) {
            oldCam.isActiveCamera = false;
        }
        if(newCam != null) {
            newCam.isActiveCamera = true;
        }
        gameCamera = camera;
    }

    @Override
    public void setGuiCamera(XCamera camera) {
        if(camera == guiCamera) {
            return;
        }
        XCameraImpl newCam = (XCameraImpl)camera;
        XCameraImpl oldCam = (XCameraImpl)guiCamera;
        if(oldCam != null) {
            oldCam.isActiveCamera = false;
        }
        if(newCam != null) {
            newCam.isActiveCamera = true;
        }
        guiCamera = camera;
    }

    @Override
    public XCamera getRenderingGameCamera() {
        if(gameEditorCamera != null)
            return gameEditorCamera;
        return gameCamera;
    }

    @Override
    public XCamera getRenderingUICamera() {
        if(guiEditorCamera != null)
            return guiEditorCamera;
        return guiCamera;
    }

    @Override
    public void setAllowToReplaceCamera(boolean flag) {
        allowReplace = flag;
    }

    @Override
    public void setGameEditorCam(XCamera camera) {
        this.gameEditorCamera = camera;
    }

    @Override
    public void setGuiEditorCam(XCamera camera) {
        this.guiEditorCamera = camera;
    }

    @Override
    public XCamera getGameCamera() {
        return gameCamera;
    }

    @Override
    public XCamera getGuiCamera() {
        return guiCamera;
    }

    @Override
    public void onAttach(XECSWorld world) {

    }

    @Override
    public void onDetach(XECSWorld world) {

    }
}