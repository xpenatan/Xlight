package xlight.engine.impl;


import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.manager.XManager;

class XCameraManagerImpl implements XCameraManager, XCameraManager.XEditorCamera, XManager {

    public XCamera gameCamera;
    public XCamera guiCamera;

    public XCamera gameEditorCamera;
    public XCamera guiEditorCamera;

    public boolean allowReplace = true;

    public XCameraManagerImpl() {
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
    public void setUICamera(XCamera camera) {
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
    public void setUIEditorCam(XCamera camera) {
        this.guiEditorCamera = camera;
    }

    @Override
    public XCamera getGameCamera() {
        if(gameCamera != null) {
            if(!gameCamera.isActiveCamera()) {
                gameCamera.setActiveCamera(false);
                gameCamera = null;
            }
        }
        return gameCamera;
    }

    @Override
    public XCamera getUICamera() {
        if(guiCamera != null) {
            if(!guiCamera.isActiveCamera()) {
                guiCamera.setActiveCamera(false);
                guiCamera = null;
            }
        }
        return guiCamera;
    }

    @Override
    public void onAttach(XWorld world) {

    }

    @Override
    public void onDetach(XWorld world) {

    }
}