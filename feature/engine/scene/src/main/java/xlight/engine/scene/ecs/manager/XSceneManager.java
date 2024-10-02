package xlight.engine.scene.ecs.manager;


import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneListener;

public interface XSceneManager {
    /** Get current scene object */
    XScene getScene();

    /**
     * Save all scene objects to the current XScene.
     * The current XScene data will be cleared before saving.
     */
    void save();

    /**
     * Load all XScene from memory.
     * All entities will be cleared before loading.
     */
    void load();

    /** Save current scene a json string */
    String saveCurrentScene();

    /**
     * Load the scene from file.
     * The current scene will be cleared before loading.
     */
    boolean loadToCurrentScene(String path, Files.FileType filetype);

    /**
     * Add a scene to the current scene. All entities will have XSceneComponent.
     * The scene object will be invalid after this.
     */
    void addScene(XScene scene);

    /**
     * Load scene from file to XScene. Add it to current scene using {@link #addScene}
     */
    XScene loadScene(String path, Files.FileType filetype);

    void setSceneListener(XSceneListener listener);
    XSceneListener getSceneListener();

    /**
     * Setup a new empty scene. The current scene data will be cleared and will be updated to have the new id and name
     */
    void setScene(int id, String name);
}