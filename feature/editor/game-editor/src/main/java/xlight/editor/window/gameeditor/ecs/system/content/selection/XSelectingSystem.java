package xlight.editor.window.gameeditor.ecs.system.content.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.aabb.XAABBTree;
import xlight.engine.aabb.XAABBTreeNode;
import xlight.engine.aabb.ecs.service.XAABBService;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemType;

public class XSelectingSystem extends XGameEditorSystem {

    XEditorManager editorManager;
    private XSelectionRenderer selectionRenderer;
    private XEntitySelectionManager selectionManager;

    private boolean hit;
    private final Array<XAABBTreeNode> hitList = new Array<>();

    @Override
    public void onSystemAttach(XWorld world) {
        selectionManager = world.getManager(XEntitySelectionManager.class);
        editorManager = world.getManager(XEditorManager.class);
        selectionRenderer = new XSelectionRenderer();

    }

    @Override
    public void onTick(XWorld world) {
        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XCamera camera = world.getManager(XCameraManager.class).getRenderingGameCamera();

            XWorld gameEngineWorld = gameEngine.getWorld();
            if(camera != null) {
                XAABBService aabbService = gameEngineWorld.getService(XAABBService.class);
                if(aabbService != null) {
                    XComponentService componentService = gameEngineWorld.getComponentService();
                    XAABBTree gameTree = aabbService.getGameTree();
                    Camera gdxCamera = camera.asGDXCamera();
                    selectionRenderer.render(1, gdxCamera, selectionManager, componentService);

                    boolean leftClick = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
                    boolean rightClick = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
                    if(leftClick || rightClick) {
                        onClickLogic(gameEngineWorld, gdxCamera, gameTree, leftClick, selectionManager);
                    }
                }
            }
        }
    }

    @Override
    public XSystemType getType() {
        return XSystemType.RENDER;
    }

    private void onClickLogic(XWorld gameEngineWorld, Camera camera, XAABBTree tree, boolean leftClick, XEntitySelectionManager selectionManager) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        hit = false;
        Ray ray = camera.getPickRay(x, y);
        tree.rayCast(ray, hitList, true, 0f, 1, leftClick);
        if(hitList.size > 0) {
            hit = true;
        }
        if(leftClick) {
            updateClickLogic(gameEngineWorld, hitList, selectionManager);
        }
    }

    private void updateClickLogic(XWorld gameEngineWorld, Array<XAABBTreeNode> raycastList, XEntitySelectionManager selectionManager) {
        XEntityService entityService = gameEngineWorld.getEntityService();
        boolean leftCtrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
        if(raycastList.size > 0) {
            XAABBTreeNode node = raycastList.get(0);
            XEntity entity = entityService.getEntity(node.getId());
            if(entity != null) {
                selectionManager.selectTarget(entity, leftCtrl);
            }
        }
        else {
            if(!leftCtrl) {
                selectionManager.unselectAllTargets();
            }
        }
    }
}