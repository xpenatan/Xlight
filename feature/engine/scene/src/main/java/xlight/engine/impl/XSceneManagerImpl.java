package xlight.engine.impl;

import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.XEngineEvent;
import xlight.engine.datamap.XDataMap;
import xlight.engine.ecs.XWorld;
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
        saveInternal(currentScene);
    }

    @Override
    public void load() {
        loadInternal(currentScene);
    }

    @Override
    public void saveToFile(FileHandle file) {
        saveInternal(currentScene);
        // Parse to json
        String json = currentScene.getJson();
        if(json != null) {
            file.writeString(json, false);
        }
    }

    @Override
    public boolean loadFromFile(FileHandle file) {
        if(file.exists() && !file.isDirectory()) {
            currentScene.onReset();
            String jsonStr = file.readString();
            currentScene.loadJson(jsonStr);
            loadInternal(currentScene);
            return true;
        }
        return false;
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
        XEntityService entityService = world.getEntityService();
        entityService.clear();
        currentScene.setId(id);
        currentScene.setName(name);
        XEventService eventService = world.getEventService();
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
        XEntityService entityService = world.getEntityService();
        entityService.clear();
        XEventService eventService = world.getEventService();
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
        XLoadEntity.loadEntities(world, scene);
    }

    public void saveInternal(XScene scene) {
        scene.clear();

        XDataMap sceneDataMap = currentScene.sceneDataMap;
        sceneDataMap.put(XSceneKeys.SCENE_TYPE.getKey(), currentScene.type.getValue());

        XSaveEntity.saveEntities(world, currentScene);
    }
}