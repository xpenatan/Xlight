package xlight.editor.window.gameeditor.ecs.system.content.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
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
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.g3d.XBatch3DAdapter;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.gizmo.XCursor3DRenderer;
import xlight.engine.gizmo.XGizmoRenderer;
import xlight.engine.math.XMath;
import xlight.engine.math.XRotSeq;
import xlight.engine.outline.XPicker2DFrameBuffer;
import xlight.engine.outline.XPicker3DFrameBuffer;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformType;
import xlight.engine.transform.XTransformMode;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XSelectingSystem extends XGameEditorSystem {

    private XEditorManager editorManager;
    private XSelectionRenderer selectionRenderer;
    private XEntitySelectionManager selectionManager;
    private XGizmoRenderer gizmoRenderer;
    private XCursor3DRenderer cursor3DRenderer;
    private XPicker3DFrameBuffer shader3DPicker;
    private XPicker2DFrameBuffer shader2DPicker;

    private Array<RenderableProvider> tmp3dArray = new Array<>();

    private final Array<XAABBTreeNode> hitList = new Array<>();

    private XTransformType transformType = XTransformType.POSITION;
    private boolean isGlobalTransform = true;

    private boolean targetLock = false;

    private final Class<? extends XComponent> worldType;

    public XSelectingSystem(Class<? extends XComponent> worldType) {
        this.worldType = worldType;
    }

    @Override
    public void onSystemAttach(XWorld world, XSystemData systemData) {
        selectionManager = world.getManager(XEntitySelectionManager.class);
        editorManager = world.getManager(XEditorManager.class);
        selectionRenderer = new XSelectionRenderer(worldType);
        gizmoRenderer = new XGizmoRenderer();
        shader3DPicker = new XPicker3DFrameBuffer();
        shader2DPicker = new XPicker2DFrameBuffer();

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

                    XComponentService componentService = gameEngineWorld.getWorldService().getComponentService();
                    XAABBTree gameTree = aabbService.getGameTree();
                    Camera gdxCamera = camera.asGDXCamera();
                    selectionRenderer.render(1, gdxCamera, selectionManager, componentService);

                    cursor3DRenderer.update(gdxCamera);
                    cursor3DRenderer.render(gdxCamera);

                    boolean leftClick = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
                    boolean rightClick = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
                    if(!gizmoRenderer.isHighlight() && (leftClick || rightClick)) {
                        onClickLogic(gameEngineWorld, gdxCamera, gameTree, leftClick, rightClick, selectionManager);
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
        Camera gdxCamera = camera.asGDXCamera();
        if(currentSelectedTarget != null) {
            XTransformComponent transformComponent = currentSelectedTarget.getComponent(XTransformComponent.class);
            if(transformComponent == null) {
                // Not possible to render gizmo without transform component
                return;
            }

            XTransform transform = transformComponent.transform;
            Quaternion quaternion = transform.getQuaternion();
            Vector3 position = transform.getPosition();
            XRotSeq rotationSequence = transform.getRotationSequence();
            gizmoRenderer.setPosition(position);
            gizmoRenderer.setObjectAngle(quaternion, rotationSequence);

            boolean isPerspective = camera.getProjectionMode() == PROJECTION_MODE.PERSPECTIVE;
            gizmoRenderer.render(gdxCamera, isPerspective, null, cursor3DRenderer);

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
                            gizmoRenderer.isGlobalTransform() ? XTransformMode.GLOBAL : XTransformMode.LOCAL,
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

    public void setTransformType(XTransformType transformType) {
        this.transformType = transformType;
    }

    public void setGlobalTransform(boolean flag) {
        isGlobalTransform = flag;
    }

    public boolean isGlobalTransform() {
        return isGlobalTransform;
    }

    public XTransformType getTransformType() {
        return transformType;
    }

    private void onClickLogic(XWorld gameEngineWorld, Camera camera, XAABBTree tree, boolean leftClick, boolean rightClick, XEntitySelectionManager selectionManager) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Ray ray = camera.getPickRay(x, y);
        tree.rayCast(ray, hitList, true, 0f, 10);
        if(leftClick) {
            updateClickLogic(gameEngineWorld, camera, selectionManager, hitList, x, y);
        }
        else if(rightClick) {
            if(hitList.size > 0) {
                XEntityService entityService = gameEngineWorld.getWorldService().getEntityService();
                XEntity entity = validateEntitySelected(camera, hitList, entityService, x, y, true, selectionManager);
                if(entity != null) {
                    selectionManager.changeCurrentSelectedTarget(entity);
                }
            }
        }
    }

    private void updateClickLogic(XWorld gameEngineWorld, Camera camera, XEntitySelectionManager selectionManager, Array<XAABBTreeNode> raycastList, int clickX, int clickY) {
        boolean leftCtrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
        if(raycastList.size > 0) {
            XEntityService entityService = gameEngineWorld.getWorldService().getEntityService();
            XEntity entity = validateEntitySelected(camera, raycastList, entityService, clickX, clickY, false, selectionManager);
            if(entity != null) {
                selectionManager.selectTarget(entity, leftCtrl);
            }
            else {
                selectionManager.unselectAllTargets();
            }
        }
        else {
            if(!leftCtrl) {
                selectionManager.unselectAllTargets();
            }
        }
    }

    XBatch3DAdapter batch3DWrapper = new XBatch3DAdapter() {
        @Override
        public void drawModel(RenderableProvider renderableProvider) {
            tmp3dArray.add(renderableProvider);
        }
    };

    private XEntity validateEntitySelected(Camera camera, Array<XAABBTreeNode> rayCastList, XEntityService es, int clickX, int clickY, boolean mustBeSelected, XEntitySelectionManager selectionManager) {
        boolean useAABBSelection = true;
        XEntity entity = null;
        for(int i = 0; i < rayCastList.size; i++) {
            XAABBTreeNode node = rayCastList.get(i);
            int entityId = node.getId();
            XEntity e = es.getEntity(entityId);
            XRender3DComponent render3DComponent = e.getComponent(XRender3DComponent.class);
            if(render3DComponent != null) {
                render3DComponent.onRender(batch3DWrapper);
            }
        }

        if(mustBeSelected && tmp3dArray.size > 0) {
            for(int i = 0; i < tmp3dArray.size; i++) {
                // remove objects that is not selected
                int entityId = shader3DPicker.getShaderRayPickingID(camera, tmp3dArray, clickX, clickY);
                if(entityId >= 0) {
                    XEntity e = es.getEntity(entityId);
                    if(!selectionManager.isSelected(e)) {
                        tmp3dArray.removeIndex(0);
                    }
                }
            }
        }

        if(tmp3dArray.size > 0) {
            useAABBSelection = false;
            int entityId = shader3DPicker.getShaderRayPickingID(camera, tmp3dArray, clickX, clickY);
            if(entityId >= 0) {
                entity = es.getEntity(entityId);
            }
            tmp3dArray.clear();
        }

        if(useAABBSelection) {
            // If improved selection is not implemented use bounding box
            XAABBTreeNode node = rayCastList.get(0);
            int entityId = node.getId();
            entity = es.getEntity(entityId);
        }
        return entity;
    }
}