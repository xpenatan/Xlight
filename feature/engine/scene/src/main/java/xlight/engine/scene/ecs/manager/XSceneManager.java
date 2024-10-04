package xlight.engine.scene.ecs.manager;


import com.badlogic.gdx.Files;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneListener;

public interface XSceneManager {
    /** Get current scene object */
    XScene getScene();

    /**
     * Save all scene objects to the current XScene.
     * The current XScene data will be cleared before saving.
     * If the loaded scene is a file, it will overwrite the file.
     */
    void save();

    /**
     * Load all XScene from memory.
     * All entities will be cleared before loading.
     */
    void load();

    /** Save current scene a json string */
    boolean saveToFile(String path, Files.FileType filetype);

    /**
     * Load the scene from file.
     * The current scene will be cleared before loading.
     */
    boolean loadFromFile(String path, Files.FileType filetype);


    /**
     * Add a scene to the current scene. All entities will have XSceneComponent.
     * The scene object will be invalid after this.
     */
    boolean addScene(String path, Files.FileType filetype);

    void setSceneListener(XSceneListener listener);
    XSceneListener getSceneListener();

    /**
     * Setup a new empty scene. The current scene data will be cleared and will be updated to have the new id and name
     */
    void newScene(int id, String name);
}