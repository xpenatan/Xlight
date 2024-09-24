package xlight.editor.window.gameeditor.ecs.system.content.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.IntArray;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.glutils.XShapeRenderer;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XBoundingBoxDebugSystem extends XGameEditorSystem {

    private XEditorManager editorManager;
    private IntArray entities;

    private XShapeRenderer shapeRenderer;

    @Override
    public void onSystemAttach(XWorld world, XSystemData systemData) {
        systemData.setEnabled(false);

        editorManager = world.getManager(XEditorManager.class);

        XEngine gameEngine = editorManager.getGameEngine();

        if(gameEngine != null) {
            init(gameEngine);
        }
        world.getEventService().addEventListener(XEditorEvent.EVENT_ENGINE_CREATED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                XEngine engine = event.getUserData();
                init(engine);
                return false;
            }
        });
        world.getEventService().addEventListener(XEditorEvent.EVENT_ENGINE_DISPOSED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                entities = null;
                return false;
            }
        });
        shapeRenderer = new XShapeRenderer();
    }

    private void init(XEngine gameEngine) {
        XWorld engineWorld = gameEngine.getWorld();
        XComponentMatcherBuilder matcherBuilder = engineWorld.getComponentService().getMatcherBuilder();
        XComponentMatcher matcher = matcherBuilder.all(XGameComponent.class, XTransformComponent.class).build();
        entities = matcher.getEntities();
    }

    @Override
    public void onTick(XWorld world) {
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XCamera camera = world.getManager(XCameraManager.class).getRenderingGameCamera();
            XWorld gameEngineWorld = gameEngine.getWorld();
            if(camera != null && entities != null) {
                XEntityService es = gameEngineWorld.getEntityService();

                shapeRenderer.setProjectionMatrix(camera.getCombined());
                shapeRenderer.beginDepth(ShapeRenderer.ShapeType.Line);
                for(int i = 0; i < entities.size; i++) {
                    int entityIndex = entities.get(i);
                    XEntity e = es.getEntity(entityIndex);
                    onTickEntity(e);
                }
                shapeRenderer.endDepth();
            }
        }
    }

    private void onTickEntity(XEntity e) {
        XTransform transform = e.getComponent(XTransformComponent.class).transform;
        BoundingBox boundingBox = transform.getBoundingBox();
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.boundingBox(boundingBox);
    }

    @Override
    public XSystemType getType() {
        return XSystemType.RENDER;
    }
}