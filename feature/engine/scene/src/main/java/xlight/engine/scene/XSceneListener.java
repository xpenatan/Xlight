package xlight.engine.scene;

public interface XSceneListener {

    /**
     * When a new scene is about to be loaded this method is called.
     * The default scene is always with id of 'default' and is always called.
     * If the editor launch your game, it may call other id instead of 'default'.
     */
    default void onLoadSceneBegin(int sceneId) {};

    /**
     * This method will be called after the XScene data is loaded and every entity is attached.
     * It will also be called if the scene data is empty
     */
    default void onLoadSceneEnd(int sceneId) {}
}