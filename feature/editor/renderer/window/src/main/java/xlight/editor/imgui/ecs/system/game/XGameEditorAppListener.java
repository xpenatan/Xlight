package xlight.editor.imgui.ecs.system.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.editor.core.ecs.XGameState;
import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.init.ecs.service.XInitFeature;
import xlight.engine.init.ecs.service.XInitFeatureService;

public class XGameEditorAppListener implements ApplicationListener {

    public static final int FEATURE = XGameEditorAppListener.class.hashCode();

    private XECSWorld editorWorld;
    private XEditorManager editorManager;

    private XCamera editorGameCamera;

    public XGameEditorAppListener(XECSWorld editorWorld) {
        this.editorWorld = editorWorld;

        editorGameCamera = XCamera.newInstance();
        editorGameCamera.setViewport(new ScreenViewport());
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
    public void resize(int width, int height) {
        System.out.println("LOG RESIZE " + width + " " + height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1, true);
        editorGameCamera.updateCamera();

        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XGameState gameState = editorManager.getGameState();
            XECSWorld gameWorld = gameEngine.getWorld();
            float deltaTime = Gdx.graphics.getDeltaTime();
            if(gameState == XGameState.STOP) {
                XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)gameWorld.getManager(XCameraManager.class);
                editorCameraManager.setGameEditorCam(editorGameCamera);
                gameWorld.tickRender();
                editorCameraManager.setGameEditorCam(null);
            }
            else {
                boolean useEditorCamera = editorManager.shouldOverrideGameCamera();
                XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)gameWorld.getManager(XCameraManager.class);
                if(useEditorCamera) {
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
    public void pause() {
        System.out.println("LOG PAUSE");
    }

    @Override
    public void resume() {
        System.out.println("LOG RESUME");
    }

    @Override
    public void dispose() {
        System.out.println("LOG DISPOSE");
    }
}