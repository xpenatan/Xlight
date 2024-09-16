package xlight.engine.scene.ecs.manager;


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

    /** Save current scene to a file */
    void saveToFile(FileHandle file);

    /**
     * Load the scene from file.
     * The current scene will be cleared before loading.
     */
    void loadFromFile(FileHandle file);

    void setSceneListener(XSceneListener listener);
    XSceneListener getSceneListener();

    /**
     * Setup a new empty scene. The current scene data will be cleared and will be updated to have the new id and name
     */
    void setScene(int id, String name);
}