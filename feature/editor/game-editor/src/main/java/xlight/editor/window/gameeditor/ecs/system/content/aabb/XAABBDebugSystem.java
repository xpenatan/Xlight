package xlight.editor.window.gameeditor.ecs.system.content.aabb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.aabb.XAABBTree;
import xlight.engine.aabb.XAABBTreeNode;
import xlight.engine.aabb.ecs.service.XAABBService;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.glutils.XShapeRenderer;
import xlight.engine.math.XMath;

public class XAABBDebugSystem extends XGameEditorSystem {

    private XEditorManager editorManager;
    private XShapeRenderer shapeRenderer;

    @Override
    public void onSystemAttach(XWorld world, XSystemData systemData) {
        systemData.setEnabled(false);
        editorManager = world.getManager(XEditorManager.class);
        shapeRenderer = new XShapeRenderer();
    }

    @Override
    public void onTick(XWorld world) {
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XCamera camera = world.getManager(XCameraManager.class).getRenderingGameCamera();
            XWorld gameEngineWorld = gameEngine.getWorld();
            XAABBService aabbService = gameEngineWorld.getService(XAABBService.class);
            if(camera != null && aabbService != null) {
                XAABBTree gameTree = aabbService.getGameTree();
                shapeRenderer.setProjectionMatrix(camera.getCombined());
                shapeRenderer.beginDepth(ShapeRenderer.ShapeType.Line);

                int size = gameTree.getSize();

                for(int i = 0; i < size; i++) {
                    XAABBTreeNode nodeIndex = gameTree.getNodeIndex(i);
                    Vector3 fatMax = nodeIndex.getFatMax();
                    Vector3 fatMin = nodeIndex.getFatMin();
                    XMath.BOUNDING_BOX_1.set(fatMin, fatMax);
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.boundingBox(XMath.BOUNDING_BOX_1);
                }
                shapeRenderer.endDepth();
            }
        }
    }

    @Override
    public XSystemType getType() {
        return XSystemType.RENDER;
    }
}