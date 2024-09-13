package xlight.engine.camera.ecs.manager;

import xlight.engine.camera.XCamera;

public interface XCameraManager {

    void clear();

    /** Set the game rendering camera that will be used to render in model batch and sprite batch.
     * This camera will be replaced temporary when your game is rendering inside an editor window.
     */
    void setGameCamera(XCamera camera);

    /** Set the gui rendering camera that will be used to render in model batch and sprite batch.
     * This camera will be replaced temporary when your game is rendering inside an editor window.
     */
    void setGuiCamera(XCamera camera);

    /**
     * Return the current rendering camera. This camera may be the game camera or other custom camera.
     * This camera is used for rendering.
     * May be null.
     */
    XCamera getRenderingGameCamera();

    /**
     * Return the current rendering camera. This camera may be the gui camera or other custom camera.
     * This camera is used for rendering.
     * May be null.
     */
    XCamera getRenderingUICamera();

    /**
     * Return the current game camera. Does not use this camera for rendering.
     * May be null.
     */
    XCamera getGameCamera();

    /**
     * Return the current gui camera. Does not use this camera for rendering.
     * May be null.
     */
    XCamera getGuiCamera();

    /** Allow editor to replace rendering camera. Used in editor windows.
     * Default: true
     */
    void setAllowToReplaceCamera(boolean flag);

    interface XEditorCamera {
        void setGameEditorCam(XCamera camera);
        void setGuiEditorCam(XCamera camera);
    }
}