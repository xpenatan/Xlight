package xlight.engine.impl;

import com.badlogic.gdx.files.FileHandle;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.scene.XScene;
import xlight.engine.scene.ecs.manager.XSceneManager;

class XSceneManagerImpl implements XSceneManager, XManager {

    XSceneImpl currentScene;
    XECSWorld world;

    @Override
    public void onAttach(XECSWorld world) {
        XPoolManager poolService = world.getManager(XPoolManager.class);
        XPoolController poolController = poolService.getPoolController();
        this.world = world;
        currentScene = new XSceneImpl(poolController);
    }

    @Override
    public XScene getScene() {
        return currentScene;
    }

    @Override
    public void save() {
        currentScene.clear();
    }

    @Override
    public void load() {

    }

    @Override
    public void saveToFile(FileHandle file) {

    }

    @Override
    public void loadFromFile(FileHandle file) {

    }
}