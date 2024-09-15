package xlight.engine.impl;

import com.badlogic.gdx.files.FileHandle;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneListener;
import xlight.engine.scene.ecs.manager.XSceneManager;

class XSceneManagerImpl implements XSceneManager, XManager {

    XSceneImpl currentScene;
    XWorld world;

    @Override
    public void onAttach(XWorld world) {
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

    @Override
    public void setSceneListener(XSceneListener listener) {

    }

    @Override
    public void setScene(int id, String name) {
        currentScene.clear();
        currentScene.setId(id);
        currentScene.setName(name);

        XEntityService entityService = world.getEntityService();
        entityService.clear();
    }
}