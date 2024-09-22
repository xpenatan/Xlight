package xlight.editor.window.gameeditor.ecs.system.content.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.aabb.XAABBTree;
import xlight.engine.aabb.XAABBTreeNode;
import xlight.engine.aabb.ecs.service.XAABBService;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.gizmo.XCursor3DRenderer;
import xlight.engine.gizmo.XGizmoRenderer;
import xlight.engine.math.XMath;
import xlight.engine.math.XRotSeq;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XSelectingSystem extends XGameEditorSystem {

    private XEditorManager editorManager;
    private XSelectionRenderer selectionRenderer;
    private XEntitySelectionManager selectionManager;
    private XGizmoRenderer gizmoRenderer;
    private XCursor3DRenderer cursor3DRenderer;

    private boolean hit;
    private final Array<XAABBTreeNode> hitList = new Array<>();

    private XGizmoRenderer.TRANSFORM_TYPE transformType = XGizmoRenderer.TRANSFORM_TYPE.POSITION;
    private boolean isGlobalTransform = true;

    private boolean targetLock = false;

    @Override
    public void onSystemAttach(XWorld world, XSystemData systemData) {
        selectionManager = world.getManager(XEntitySelectionManager.class);
        editorManager = world.getManager(XEditorManager.class);
        selectionRenderer = new XSelectionRenderer();
        gizmoRenderer = new XGizmoRenderer();

        cursor3DRenderer = new XCursor3DRenderer();
        XMath.QUAT_1.idt();
        cursor3DRenderer.setGlobalAngle(XMath.QUAT_1);
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

                    gizmoRenderer.setGlobalTransform(isGlobalTransform());
                    gizmoRenderer.setTransformType(getTransformType());

                    XComponentService componentService = gameEngineWorld.getComponentService();
                    XAABBTree gameTree = aabbService.getGameTree();
                    Camera gdxCamera = camera.asGDXCamera();
                    selectionRenderer.render(1, gdxCamera, selectionManager, componentService);

                    boolean leftClick = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
                    boolean rightClick = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
                    if(!gizmoRenderer.isHighlight() && (leftClick || rightClick)) {
                        onClickLogic(gameEngineWorld, gdxCamera, gameTree, leftClick, selectionManager);
                    }
                    else {
                        renderGizmo(camera);
                    }
                }
            }
        }
    }

    private void renderGizmo(XCamera camera) {
        XEntity currentSelectedTarget = selectionManager.getCurrentSelectedTarget();
        if(currentSelectedTarget != null) {
            XTransform transform = currentSelectedTarget.getComponent(XTransformComponent.class).transform;
            Quaternion quaternion = transform.getQuaternion();
            Vector3 position = transform.getPosition();
            XRotSeq rotationSequence = transform.getRotationSequence();
            gizmoRenderer.setPosition(position);
            gizmoRenderer.setObjectAngle(quaternion, rotationSequence);

            boolean isPerspective = camera.getProjectionMode() == PROJECTION_MODE.PERSPECTIVE;
            gizmoRenderer.render(camera.asGDXCamera(), isPerspective, null, cursor3DRenderer);

            XCursor3DRenderer.AXIS_TYPE targetType = gizmoRenderer.getAxisType();
            if(targetType != null) {
                if(!targetLock) {
                    targetLock = true;
                    Vector3 gizmoPosition = gizmoRenderer.getPosition();
                    cursor3DRenderer.set3DCursorPosition(gizmoPosition);
                    cursor3DRenderer.setMoveState(targetType);
                    if(gizmoRenderer.isGlobalTransform()) {
                        XMath.QUAT_1.idt();
                        cursor3DRenderer.setGlobalAngle(XMath.QUAT_1);
                    }
                    else {
                        cursor3DRenderer.setGlobalAngle(quaternion);
                    }
                }

                if(gizmoRenderer.isDragging()) {
                    selectionManager.moveAndStartDragging(
                            gizmoRenderer.getTransformType(),
                            gizmoRenderer.isGlobalTransform() ? XTransform.XTransformMode.GLOBAL : XTransform.XTransformMode.LOCAL,
                            gizmoRenderer.getObjectVirtualPosition(),
                            gizmoRenderer.getObjectVirtualTotalVectorTargetAngle()
                    );
                }
            }
            else {
                if(targetLock) {
                    targetLock = false;
                    selectionManager.removeDragging();
                }
            }
        }
    }

    @Override
    public XSystemType getType() {
        return XSystemType.RENDER;
    }

    public void setTransformType(XGizmoRenderer.TRANSFORM_TYPE transformType) {
        this.transformType = transformType;
    }

    public void setGlobalTransform(boolean flag) {
        isGlobalTransform = flag;
    }

    public boolean isGlobalTransform() {
        return isGlobalTransform;
    }

    public XGizmoRenderer.TRANSFORM_TYPE getTransformType() {
        return transformType;
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