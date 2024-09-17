package xlight.editor.imgui.ecs.system.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.editor.core.XEngineEvent;
import xlight.editor.core.ecs.XGameState;
import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.controller.XCameraController;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.init.ecs.service.XInitFeature;
import xlight.engine.init.ecs.service.XInitFeatureService;

public class XGameEditorAppListener implements ApplicationListener {

    public static final int FEATURE = XGameEditorAppListener.class.hashCode();

    private XWorld editorWorld;
    private XEditorManager editorManager;

    private XCamera editorGameCamera;

    public XCameraController cameraController;

    public XGameEditorAppListener(XWorld editorWorld) {
        this.editorWorld = editorWorld;

        editorGameCamera = XCamera.newInstance();
        editorGameCamera.setViewport(new ScreenViewport());
        editorGameCamera.setType(1);
        editorGameCamera.setPosition(0, 0, 4);
        editorGameCamera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);

        cameraController = new XCameraController();
        cameraController.setCamera(editorGameCamera);
    }

    @Override
    public void create() {

        editorManager = editorWorld.getManager(XEditorManager.class);

        editorWorld.getEventService().addEventListener(XEditorEvents.EVENT_ENGINE_CREATED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                return false;
            }
        });

        editorWorld.getEventService().addEventListener(XEditorEvents.EVENT_ENGINE_DISPOSED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                return false;
            }
        });


        XInitFeatureService featureService = editorWorld.getService(XInitFeatureService.class);
        featureService.addFeature(XGameEditorAppListener.FEATURE, XInitFeature::initFeature);

        System.out.println("LOG CREATE");
    }

    @Override
    public void render() {
        try {
            renderInternal();
        }
        catch(Throwable t) {
            editorManager.setGameEngineError();
            t.printStackTrace();
        }
    }

    private void renderInternal() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1, true);

        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XGameState gameState = editorManager.getGameState();
            XWorld gameWorld = gameEngine.getWorld();
            float deltaTime = Gdx.graphics.getDeltaTime();
            if(gameState == XGameState.STOP) {
                cameraController.update();
                XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)gameWorld.getManager(XCameraManager.class);
                editorCameraManager.setGameEditorCam(editorGameCamera);
                gameWorld.tickRender();
                editorCameraManager.setGameEditorCam(null);
            }
            else {
                boolean useEditorCamera = editorManager.shouldOverrideGameCamera();
                XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)gameWorld.getManager(XCameraManager.class);
                if(useEditorCamera) {
                    cameraController.update();
                    editorCameraManager.setGameEditorCam(editorGameCamera);
                }
                if(gameState == XGameState.PAUSE) {
                    gameWorld.tickRender();
                    gameWorld.tickUI();
                }
                else if(gameState == XGameState.PLAY) {
                    gameWorld.tickUpdate(deltaTime);
                    gameWorld.tickRender();
                    gameWorld.tickUI();
                }
                if(useEditorCamera) {
                    editorCameraManager.setGameEditorCam(null);
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("LOG RESIZE " + width + " " + height);
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            gameEngine.getWorld().getEventService().sendEvent(XEngineEvent.EVENT_RESIZE);
        }
    }

    @Override
    public void pause() {
        System.out.println("LOG PAUSE");
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            gameEngine.getWorld().getEventService().sendEvent(XEngineEvent.EVENT_PAUSE);
        }
    }

    @Override
    public void resume() {
        System.out.println("LOG RESUME");
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            gameEngine.getWorld().getEventService().sendEvent(XEngineEvent.EVENT_RESUME);
        }
    }

    @Override
    public void dispose() {
        System.out.println("LOG DISPOSE");
    }
}