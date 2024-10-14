package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.core.selection.XObjectSelection;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.event.XWorldEvent;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.list.XDataArray;
import xlight.engine.list.XList;
import xlight.engine.math.XMath;
import xlight.engine.math.XMatrix4Utils;
import xlight.engine.math.XRotSeq;
import xlight.engine.math.XRotationUtils;
import xlight.engine.pool.XPool;
import xlight.engine.transform.XTransformType;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformMode;
import xlight.engine.transform.ecs.component.XTransformComponent;

class XEntitySelectionManagerImpl extends XObjectSelection<XEntity, XEntitySelectionManagerImpl.XEntitySelectionNode> implements XEntitySelectionManager, XManager {

    public XEntitySelectionManagerImpl() {
        super(new XPool<>() {
            @Override
            protected XEntitySelectionNode newObject() {
                return new XEntitySelectionNode();
            }
        });
    }

    @Override
    public void onAttach(XWorld editorWorld) {
        XWorldService worldService = editorWorld.getWorldService();
        XEventService editorEventService = worldService.getEventService();
        editorEventService.addEventListener(XEditorEvent.EVENT_ENGINE_CREATED, event -> {
            XEngine gameEngine = event.getUserData();
            XWorld gameEngineWorld = gameEngine.getWorld();
            gameEngineWorld.getWorldService().getEventService().addEventListener(XWorldEvent.EVENT_DETACH_ENTITY, new XEventListener() {
                @Override
                public boolean onEvent(XEvent event) {
                    XEntity entity = event.getUserData();
                    unselectTarget(entity);
                    return false;
                }
            });
            return false;
        });
    }

    @Override
    public void removeDragging() {
        XList<XEntitySelectionNode> selectedTargetsNode = getSelectedTargetsNode();

        for(XEntitySelectionNode node: selectedTargetsNode) {
            node.initStart = false;
            XEntity selectedTarget = node.getValue();
            XTransformComponent transformComponent = selectedTarget.getComponent(XTransformComponent.class);
            XTransform transform = transformComponent.transform;
            transform.setDragging(false);
        }
    }

    @Override
    public void moveAndStartDragging(XTransformType transformType, int transformMode, Vector3 pos, Vector3 rot) {
        XList<XEntitySelectionNode> selectedTargets = getSelectedTargetsNode();

        float targetX = pos.x;
        float targetY = pos.y;
        float targetZ = pos.z;
        float tRotX = rot.x;
        float tRotY = rot.y;
        float tRotZ = rot.z;

        boolean firstEntityFlag = true;
        float firstX = 0;
        float firstY = 0;
        float firstZ = 0;
        float firstRX = 0;
        float firstRY = 0;
        float firstRZ = 0;
        float firstRW = 0;

        boolean keyPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);

        boolean first = false;
        for(XEntitySelectionNode node: selectedTargets) {

            if(keyPressed && first) {
                break;
            }
            first = true;

            XEntity selectedTarget = node.getValue();
            boolean initStart = node.initStart;
            node.initStart = true;
            XTransformComponent transformComponent = selectedTarget.getComponent(XTransformComponent.class);
            if(transformComponent != null) {
                XTransform transform = transformComponent.transform;
                Vector3 rotation = transform.getRotation();
                transform.setDragging(true);
                XRotSeq rotationSequence = transform.getRotationSequence();
                Vector3 position = transform.getPosition();
                Vector3 scale = transform.getScale();
                Quaternion quaternion = transform.getQuaternion();

                if(!initStart) {
                    node.startOffsetPosition.setZero();
                    node.startPosition.set(position);
                    node.startRotation.set(rotation);
                    node.startQuaternion.set(quaternion);
                    node.startScale.set(scale);
                }

                if(firstEntityFlag) {
                    firstEntityFlag = false;

                    firstX = position.x;
                    firstY = position.y;
                    firstZ = position.z;

                    transform.forceUpdate();

                    XMath.MAT4_3.idt();
                    XMath.MAT4_2.idt();
                    XMath.MAT4_1.idt();
                    XMath.QUAT_1.idt();
                    XMath.QUAT_2.idt();

                    {
                        if(transformType == XTransformType.POSITION) {
                            transform.forceUpdate();
                            transform.setPosition(targetX, targetY, targetZ);
                            firstX = targetX;
                            firstY = targetY;
                            firstZ = targetZ;
                        }
                        else if(transformType == XTransformType.ROTATE) {
                            if(transformMode == XTransformMode.GLOBAL) {
                                XRotationUtils.convertEulerToQuat(rotationSequence, node.startRotation, XMath.QUAT_1);
                                XMath.QUAT_1.set(node.startQuaternion);
                                XMath.QUAT_2.setEulerAngles(tRotY, tRotX, tRotZ);
                                XMath.QUAT_1.mulLeft(XMath.QUAT_2);
                                transform.setRotation(XMath.QUAT_1);
                            }
                            else if(transformMode == XTransformMode.LOCAL) {
                                XMath.QUAT_1.set(node.startQuaternion);
                                XMath.QUAT_2.setEulerAngles(tRotY, tRotX, tRotZ);
                                XMath.QUAT_1.mul(XMath.QUAT_2);
                                transform.setRotation(XMath.QUAT_1);
                            }
                            else {
                                transform.forceUpdate();
                                transform.setRotation(tRotX, tRotY, tRotZ);
                            }
                        }
                        Quaternion quat = transform.getQuaternion();
                        firstRX = quat.x;
                        firstRY = quat.y;
                        firstRZ = quat.z;
                        firstRW = quat.w;
                    }
                }
                else {
                    // Second object and so on
                    XEntitySelectionNode firstNode = getSelectedIndex(0);

                    float curOffsetX = XMath.getAxisDistance(firstNode.startPosition.x, position.x);
                    float curOffsetY = XMath.getAxisDistance(firstNode.startPosition.y, position.y);
                    float curOffsetZ = XMath.getAxisDistance(firstNode.startPosition.z, position.z);

                    if(!initStart) {
                        node.startOffsetPosition.set(curOffsetX, curOffsetY, curOffsetZ);
                    }

                    Matrix4 mulMatrix = XMath.getRotationOffset(firstNode.startQuaternion, XMath.QUAT_1.set(firstRX, firstRY, firstRZ, firstRW));
                    Quaternion rotationOffset = XMath.QUAT_1;
                    mulMatrix.getRotation(rotationOffset);
                    float rotOffsetX = rotationOffset.x;
                    float rotOffsetY = rotationOffset.y;
                    float rotOffsetZ = rotationOffset.z;
                    float rotOffsetW = rotationOffset.w;

                    XMath.MAT4_2.idt();
                    XMath.MAT4_3.idt();

                    XMath.MAT4_3.rotate(rotationOffset);
                    XMath.MAT4_2.translate(node.startOffsetPosition.x, node.startOffsetPosition.y, node.startOffsetPosition.z);
                    XMatrix4Utils.rotateAroundOrigin(XMath.MAT4_2, XMath.MAT4_3, XMath.MAT4_1);

                    XMath.MAT4_1.getTranslation(XMath.VEC3_1);
                    float xx = XMath.VEC3_1.x;
                    float yy = XMath.VEC3_1.y;
                    float zz = XMath.VEC3_1.z;

                    float newX = firstX + xx;
                    float newY = firstY + yy;
                    float newZ = firstZ + zz;

                    System.out.println("X: " + newX + " Y: " + newY + " Z: " + newZ);
                    transform.setPosition(newX, newY, newZ);

                    rotationOffset.set(rotOffsetX, rotOffsetY, rotOffsetZ, rotOffsetW);
                    XMath.MAT4_2.idt();
                    XMath.MAT4_3.idt();
                    XMath.MAT4_3.rotate(rotationOffset.mul(node.startQuaternion));
                    XMatrix4Utils.rotateAtOrigin(XMath.MAT4_2, XMath.MAT4_3, XMath.MAT4_1);

                    XMath.MAT4_1.getRotation(XMath.QUAT_1);
                    transform.setRotation(XMath.QUAT_1);
                }
            }
        }
    }

    public static class XEntitySelectionNode extends XDataArray.XDataArrayNode<XEntity> {
        public boolean initStart = false;
        final public Vector3 startPosition = new Vector3();
        final public Vector3 startOffsetPosition = new Vector3();
        final public Vector3 startRotation = new Vector3();
        final public Quaternion startQuaternion = new Quaternion();
        final public Vector3 startScale = new Vector3();

    }
}