package xlight.editor.imgui.ecs.system.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.editor.core.XCameraType;
import xlight.editor.impl.XGameEditorManagerImpl;
import xlight.editor.window.gameeditor.ecs.manager.XGameEditorManager;
import xlight.editor.window.gameeditor.ecs.system.content.aabb.XAABBDebugSystem;
import xlight.editor.window.gameeditor.ecs.system.content.entity.XBoundingBoxDebugSystem;
import xlight.editor.window.gameeditor.ecs.system.content.selection.XSelectingSystem;
import xlight.engine.core.XEngineEvent;
import xlight.editor.core.ecs.XGameState;
import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.editor.window.gameeditor.ecs.system.content.buttons.XFloatingButtonSystem;
import xlight.editor.window.gameeditor.ecs.system.content.gizmo.XGizmoSystem;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.controller.XCameraController;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.system.XSystemController;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.init.ecs.service.XInitFeature;
import xlight.engine.init.ecs.service.XInitFeatureService;

public class XGameEditorAppListener implements ApplicationListener {

    public static final int FEATURE = XGameEditorAppListener.class.hashCode();

    private XWorld editorWorld;
    private XEditorManager editorManager;

    private XCamera editorGameCamera;
    private XCamera editorUICamera;

    public XCameraController cameraController;

    private XSystemController systemController;

    public XGameEditorAppListener(XWorld editorWorld) {
        this.editorWorld = editorWorld;

        editorGameCamera = XCamera.newInstance();
        editorGameCamera.setViewport(new ScreenViewport());
        editorGameCamera.setType(XCameraType.EDITOR_CAMERA_TYPE);
        editorGameCamera.setPosition(0, 0, 4);
        editorGameCamera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);
        editorGameCamera.setNear(0.2f);
        editorGameCamera.setFar(1000);

        editorUICamera = XCamera.newInstance();
        editorUICamera.setViewport(new ScreenViewport());
        editorUICamera.setType(XCameraType.EDITOR_CAMERA_TYPE);
        editorUICamera.setPosition(0, 0, 0);
        editorUICamera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);

        cameraController = new XCameraController();
        cameraController.setCamera(editorGameCamera);
        XSystemService systemService = editorWorld.getSystemService();
        systemController = systemService.getSystemController(XGameEditorSystem.SYSTEM_CONTROLLER);

        setupManagers(editorWorld);
        setupSystems(systemService);
    }

    private void setupSystems(XSystemService systemService) {
        systemService.attachSystem(new XGizmoSystem());
        systemService.attachSystem(new XFloatingButtonSystem());
        systemService.attachSystem(new XBoundingBoxDebugSystem());
        systemService.attachSystem(new XAABBDebugSystem());
        systemService.attachSystem(new XSelectingSystem());
    }
    private void setupManagers(XWorld world) {
        world.attachManager(XGameEditorManager.class, new XGameEditorManagerImpl());
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
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1, true);
        XEngine gameEngine = editorManager.getGameEngine();

        if(gameEngine == null) {
            return;
        }

        try {
            renderInternal(gameEngine);
        }
        catch(Throwable t) {
            editorManager.setGameEngineError();
            t.printStackTrace();
        }

        XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)editorWorld.getManager(XCameraManager.class);
        editorCameraManager.setGameEditorCam(editorGameCamera);
        editorCameraManager.setUIEditorCam(editorUICamera);

        XGameState gameState = editorManager.getGameState();
        boolean useEditorCamera = editorManager.shouldOverrideGameCamera();

        if(useEditorCamera || gameState == XGameState.STOP) {
            systemController.tickTimeStepSystem();
            systemController.tickUpdateSystem();
            systemController.tickRenderSystem();
            systemController.tickUISystem();
        }

        editorCameraManager.setGameEditorCam(null);
        editorCameraManager.setUIEditorCam(null);
    }

    private void renderInternal(XEngine gameEngine) {
        XGameState gameState = editorManager.getGameState();
        XWorld gameWorld = gameEngine.getWorld();
        float deltaTime = Gdx.graphics.getDeltaTime();
        if(gameState == XGameState.STOP) {
            cameraController.update();
            XCameraManager.XEditorCamera editorCameraManager = (XCameraManager.XEditorCamera)gameWorld.getManager(XCameraManager.class);
            editorCameraManager.setGameEditorCam(editorGameCamera);
            gameWorld.update(deltaTime);
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
                gameWorld.update(deltaTime);
                gameWorld.tickRender();
                gameWorld.tickUI();
            }
            else if(gameState == XGameState.PLAY) {
                gameWorld.update(deltaTime);
                gameWorld.tickUpdate();
                gameWorld.tickRender();
                gameWorld.tickUI();
            }
            if(useEditorCamera) {
                editorCameraManager.setGameEditorCam(null);
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