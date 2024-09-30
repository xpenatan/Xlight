package xlight.editor.impl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.gdx.multiview.EmuFiles;
import xlight.engine.aabb.ecs.service.XAABBService;
import xlight.engine.aabb.ecs.service.XAABBServiceDefault;
import xlight.engine.core.XEngineEvent;
import xlight.editor.core.ecs.XGameState;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.project.XAppLoader;
import xlight.editor.core.project.XProjectOptions;
import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.scene.ecs.manager.XSceneManager;

class XEditorManagerImpl implements XEditorManager, XManager {

    public XEngine gameEngine;
    public Input defaultInput;
    public InputMultiplexer defaultMultiplexer;
    public EmuFiles rootEmuFiles;

    boolean overrideGameCamera = false;
    boolean overrideUICamera = false;

    public XGameState gameState = XGameState.STOP;

    @Override
    public void onAttach(XWorld world) {
        rootEmuFiles = new EmuFiles(Gdx.files);
        rootEmuFiles.fileHandleOverride = new FileHandleOverride();
        Gdx.files = rootEmuFiles;

        defaultInput = Gdx.input; // cache the default input
        defaultMultiplexer = new InputMultiplexer();
        defaultInput.setInputProcessor(defaultMultiplexer);

        XEventService eventService = world.getEventService();
        eventService.addEventListener(XEditorEvent.EVENT_NEW_PROJECT, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                XProjectOptions projectOptions = event.getUserData();
                if(projectOptions != null) {
                    XEngine oldEngine = gameEngine;

                    if(oldEngine != null) {
                        gameEngine = null;
                        eventService.sendEvent(XEditorEvent.EVENT_ENGINE_DISPOSED, oldEngine, new XEventService.XSendEventListener() {
                            @Override
                            public void onBeginEvent(XEvent event) {
                                oldEngine.dispose();
                                rootEmuFiles.setInternalPrefix("");
                                initEngine(projectOptions, eventService, XEditorManagerImpl.this);
                            }
                        });
                    }
                    else {
                        initEngine(projectOptions, eventService, XEditorManagerImpl.this);
                    }
                }
                return false;
            }
        });
    }

    private void initEngine(XProjectOptions projectOptions, XEventService eventService, XEditorManagerImpl editorManager) {
        try {
            if(Gdx.app.getType() != Application.ApplicationType.WebGL && projectOptions.buildPath.isEmpty()) {
                throw new RuntimeException("XProjectOptions build path is empty");
            }
            XAppLoader appLoader = new XAppLoader(projectOptions.buildPath);
            XApplication applicationListener = appLoader.create(projectOptions.mainApplication);
            if(applicationListener != null) {
                XEngine gameEngine = XEngine.newInstance();
                gameEngine.update(1);
                boolean error = false;
                try {
                    applicationListener.onSetup(gameEngine);
                }catch(Throwable t) {
                    error = true;
                    System.err.println("Error setting up engine");
                    t.printStackTrace();
                }

                if(!error) {
                    editorManager.rootEmuFiles.setInternalPrefix(projectOptions.getProjectAssetPath());
                    editorManager.rootEmuFiles.setLocalPrefix(projectOptions.getProjectAssetPath());

                    // Update 1 time so all systems are created
                    gameEngine.update(1);

                    setupGameWorld(gameEngine.getWorld());

                    eventService.sendEvent(XEditorEvent.EVENT_ENGINE_CREATED, gameEngine, new XEventService.XSendEventListener() {
                        @Override
                        public void onBeginEvent(XEvent event) {
                            editorManager.gameEngine = gameEngine;
                        }

                        @Override
                        public void onEndEvent(XEvent event) {
                            gameEngine.getWorld().getEventService().sendEvent(XEngineEvent.EVENT_CREATE, null, new XEventService.XSendEventListener() {
                                @Override
                                public void onEndEvent(XEvent event) {
                                    XSceneManager sceneManager = gameEngine.getWorld().getManager(XSceneManager.class);
                                    sceneManager.setScene(0, "default");
                                }
                            });
                        }
                    });
                }
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private void setupGameWorld(XWorld gameWorld) {
        // This setup game engine world to be used by the editor
        if(gameWorld.getService(XAABBService.class) == null) {
            // Add AABB Service if its not set
            gameWorld.attachService(XAABBService.class, new XAABBServiceDefault());
        }
    }

    @Override
    public void onDetach(XWorld world) {

    }

    @Override
    public XEngine getGameEngine() {
        return gameEngine;
    }

    @Override
    public Input getDefaultInput() {
        return defaultInput;
    }

    @Override
    public InputMultiplexer getDefaultMultiplexer() {
        return defaultMultiplexer;
    }

    @Override
    public XGameState getGameState() {
        return gameState;
    }

    @Override
    public boolean shouldOverrideGameCamera() {
        return overrideGameCamera;
    }

    @Override
    public boolean shouldOverrideUICamera() {
        return overrideUICamera;
    }

    @Override
    public void setGameEngineError() {
        gameEngine.dispose();
        gameEngine = null;
    }
}