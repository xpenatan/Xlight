package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.gdx.multiview.EmuFiles;
import xlight.editor.core.XEngineEvent;
import xlight.editor.core.ecs.XGameState;
import xlight.editor.core.ecs.event.XEditorEvents;
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

    boolean overrideGameCamera = true;
    boolean overrideUICamera = true;

    public XGameState gameState = XGameState.PLAY;

    @Override
    public void onAttach(XWorld world) {
        rootEmuFiles = new EmuFiles(Gdx.files);
        Gdx.files = rootEmuFiles;

        defaultInput = Gdx.input; // cache the default input
        defaultMultiplexer = new InputMultiplexer();
        defaultInput.setInputProcessor(defaultMultiplexer);

        XEventService eventService = world.getEventService();
        eventService.addEventListener(XEditorEvents.EVENT_NEW_PROJECT, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                XProjectOptions projectOptions = event.getUserData();
                if(projectOptions != null) {

                    XEngine oldEngine = gameEngine;

                    if(oldEngine != null) {
                        gameEngine = null;
                        eventService.sendEvent(XEditorEvents.EVENT_ENGINE_DISPOSED, oldEngine, new XEventService.XSendEventListener() {
                            @Override
                            public void onBegin(XEvent event) {
                                oldEngine.dispose();
                                rootEmuFiles.setInternalPrefix("");
                                if(projectOptions != null) {
                                    initEngine(projectOptions, eventService, XEditorManagerImpl.this);
                                }
                            }
                        });
                    }
                    else {
                        if(projectOptions != null) {
                            initEngine(projectOptions, eventService, XEditorManagerImpl.this);
                        }
                    }
                }
                return false;
            }
        });
    }

    private void initEngine(XProjectOptions projectOptions, XEventService eventService, XEditorManagerImpl editorManager) {
        try {
            XAppLoader appLoader = new XAppLoader(projectOptions.buildPath);
            XApplication applicationListener = appLoader.create(projectOptions.mainApplication);
            XEngine gameEngine = XEngine.newInstance();
            gameEngine.update(1);
            boolean error = false;
            try {
                applicationListener.onSetup(gameEngine);
            }catch(Throwable t) {
                error = true;
                t.printStackTrace();
            }

            if(!error) {
                editorManager.rootEmuFiles.setInternalPrefix(projectOptions.getProjectAssetPath());
                editorManager.rootEmuFiles.setLocalPrefix(projectOptions.getProjectAssetPath());

                // Update 1 time so all systems are created
                gameEngine.update(1);

                eventService.sendEvent(XEditorEvents.EVENT_ENGINE_CREATED, gameEngine, new XEventService.XSendEventListener() {
                    @Override
                    public void onBegin(XEvent event) {
                        editorManager.gameEngine = gameEngine;
                    }

                    @Override
                    public void onEnd(XEvent event) {
                        gameEngine.getWorld().getEventService().sendEvent(XEngineEvent.EVENT_CREATE, null, new XEventService.XSendEventListener() {
                            @Override
                            public void onEnd(XEvent event) {
                                XSceneManager sceneManager = gameEngine.getWorld().getManager(XSceneManager.class);
                                sceneManager.setScene(0, "default");
                            }
                        });
                    }
                });
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
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
}