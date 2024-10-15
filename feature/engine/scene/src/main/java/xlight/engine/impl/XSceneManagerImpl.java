package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.XEngineEvent;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneListener;
import xlight.engine.scene.ecs.manager.XSceneManager;

class XSceneManagerImpl implements XSceneManager, XManager {

    private boolean loadSceneCalled;

    XSceneImpl currentScene;
    XWorld world;
    private XSceneListener sceneListener;
    private XPoolController poolController;

    private XLoadEntity2 loadEntity;

    @Override
    public void onAttach(XWorld world) {
        poolController = world.getGlobalData(XPoolController.class);
        this.world = world;
        currentScene = new XSceneImpl(poolController);
        loadEntity = new XLoadEntity2();
    }

    @Override
    public XScene getScene() {
        return currentScene;
    }

    @Override
    public void save() {
        if(currentScene.doFileExists()) {
            saveToFile(currentScene.getPath(), currentScene.getFileType());
        }
        else {
            saveInternal(currentScene);
        }
    }

    @Override
    public void load() {
        if(currentScene.doFileExists()) {
            loadFromFile(currentScene.getPath(), currentScene.getFileType());
        }
        else {
            loadInternal(currentScene);
        }
    }

    @Override
    public boolean saveToFile(String path, Files.FileType filetype) {
        try {
            saveInternal(currentScene);
            // Parse to json
            String json = currentScene.getJson();
            FileHandle fileHandle = Gdx.files.getFileHandle(path, filetype);
            fileHandle.writeString(json, false);
            return true;
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean loadFromFile(String path, Files.FileType filetype) {
        try {
            currentScene.clear();
            if(currentScene.loadJson(path, filetype)) {
                loadInternal(currentScene);
                return true;
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addScene(String path, Files.FileType filetype) {
        XSceneImpl scene = null;
        scene = poolController.obtainObject(XScene.class);
        try {
            if(scene.loadJson(path, filetype)) {
                loadEntity.load(world, scene, true);
                poolController.releaseObject(XScene.class, scene);
                return true;
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        poolController.releaseObject(XScene.class, scene);
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
    public void newScene(int id, String name) {
        if(name == null) {
            return;
        }
        name = name.trim();
        if(name.isEmpty()) {
            return;
        }
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

    @Override
    public XDataMap saveEntity(XEntity entity) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XDataMap entityDataMap = XSaveEntity.saveEntity(entityService, poolController, registerManager, entity);
        return entityDataMap;
    }

    @Override
    public XEntity loadEntity(XDataMap entityMap) {
        XEntity entity = loadEntity.loadEntityAndAttach(world, entityMap);
        return entity;
    }

    public void loadInternal(XScene scene) {
        loadSceneCalled = true;
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

    public void saveInternal(XSceneImpl scene) {
        scene.getSceneDataMap().clear();
        XSaveSystem.save(world, currentScene);
        XSaveManager.save(world, currentScene);
        XSaveEntity.save(world, currentScene);
    }
}