package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.XEngineEvent;
import xlight.engine.datamap.XDataMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneListener;
import xlight.engine.scene.ecs.manager.XSceneManager;

class XSceneManagerImpl implements XSceneManager, XManager {

    XSceneImpl currentScene;
    XWorld world;
    private XSceneListener sceneListener;
    private XPoolController poolController;

    private XLoadEntity loadEntity;

    @Override
    public void onAttach(XWorld world) {
        poolController = world.getGlobalData(XPoolController.class);
        this.world = world;
        currentScene = new XSceneImpl(poolController);
        loadEntity = new XLoadEntity();
    }

    @Override
    public XScene getScene() {
        return currentScene;
    }

    @Override
    public void save() {
        saveInternal(currentScene);
    }

    @Override
    public void load() {
        loadInternal(currentScene);
    }

    @Override
    public String saveCurrentScene() {
        saveInternal(currentScene);
        // Parse to json
        return currentScene.getJson();
    }

    @Override
    public boolean loadToCurrentScene(String path, Files.FileType filetype) {
        currentScene.onReset();
        if(currentScene.loadJson(path, filetype)) {
            loadInternal(currentScene);
            return true;
        }
        return false;
    }

    @Override
    public void addScene(XScene scene) {
        if(scene != null) {
            loadEntity.load(world, scene, true);
        }
    }

    @Override
    public XScene loadScene(String path, Files.FileType filetype) {
        XSceneImpl scene = null;
        scene = poolController.obtainObject(XScene.class);
        if(scene.loadJson(path, filetype)) {

            return scene;
        }
        poolController.releaseObject(XScene.class, scene);
        return null;
    }

    @Override
    public void setSceneListener(XSceneListener listener) {
        this.sceneListener = listener;
    }

    @Override
    public XSceneListener getSceneListener() {
        return sceneListener;
    }

    @Override
    public void setScene(int id, String name) {
        currentScene.clear();
        XEntityService entityService = world.getWorldService().getEntityService();
        entityService.clear();
        currentScene.setId(id);
        currentScene.setName(name);
        XEventService eventService = world.getWorldService().getEventService();
        eventService.sendEvent(XEngineEvent.EVENT_CLEAR_WORLD, null, new XEventService.XSendEventListener() {
            @Override
            public void onEndEvent(XEvent event) {
                if(sceneListener != null) {
                    // It's a new scene so just call both listeners
                    sceneListener.onLoadSceneBegin(id);
                    sceneListener.onLoadSceneEnd(id);
                }
            }
        }, true);
    }

    public void loadInternal(XScene scene) {
        XWorldService worldService = world.getWorldService();
        XEntityService entityService = worldService.getEntityService();
        entityService.clear();
        XEventService eventService = worldService.getEventService();
        eventService.sendEvent(XEngineEvent.EVENT_CLEAR_WORLD, null, new XEventService.XSendEventListener() {
            @Override
            public void onEndEvent(XEvent event) {
                int id = scene.getId();
                if(sceneListener != null) {
                    sceneListener.onLoadSceneBegin(id);
                }
                loadSceneInternal(scene);
                if(sceneListener != null) {
                    sceneListener.onLoadSceneEnd(id);
                }
                eventService.sendEvent(XEngineEvent.EVENT_SCENE_LOADED);
            }
        }, true);
    }

    private void loadSceneInternal(XScene scene) {
        XLoadSystem.load(world, scene);
        XLoadManager.load(world, scene);
        loadEntity.load(world, scene, false);
    }

    public void saveInternal(XScene scene) {
        scene.clear();

        XDataMap sceneDataMap = currentScene.sceneDataMap;
        sceneDataMap.put(XSceneKeys.SCENE_TYPE.getKey(), currentScene.type.getValue());

        XSaveSystem.save(world, currentScene);
        XSaveManager.save(world, currentScene);
        XSaveEntity.save(world, currentScene);
    }
}