package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.core.selection.XObjectSelection;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.event.XWorldEvent;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.list.XDataArray;
import xlight.engine.list.XList;
import xlight.engine.math.XMath;
import xlight.engine.math.XMatrix4Utils;
import xlight.engine.math.XRotSeq;
import xlight.engine.math.XRotationUtils;
import xlight.engine.pool.XPool;
import xlight.engine.transform.XGizmoType;
import xlight.engine.transform.XTransform;
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
    public void onAttach(XWorld world) {
        world.getEventService().addEventListener(XEditorEvent.EVENT_ENGINE_CREATED, event -> {
            XEngine gameEngine = event.getUserData();
            XWorld gameEngineWorld = gameEngine.getWorld();
            gameEngineWorld.getEventService().addEventListener(XWorldEvent.EVENT_DETACH_ENTITY, new XEventListener() {
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
    public void moveAndStartDragging(XGizmoType transformType, int transformMode, Vector3 pos, Vector3 rot) {
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
                        if(transformType == XGizmoType.POSITION) {
                            transform.forceUpdate();
                            transform.setPosition(targetX, targetY, targetZ);
                            firstX = targetX;
                            firstY = targetY;
                            firstZ = targetZ;
                        }
                        else if(transformType == XGizmoType.ROTATE) {
                            if(transformMode == XTransform.XTransformMode.GLOBAL) {
                                if(!fixRotation(rotationSequence, transform, node, tRotX, tRotY, tRotZ)) {
                                    XRotationUtils.convertEulerToQuat(rotationSequence, node.startRotation, XMath.QUAT_1, true);
                                    XMath.QUAT_1.set(node.startQuaternion);
                                    XMath.QUAT_2.setEulerAngles(tRotY, tRotX, tRotZ);
                                    XMath.QUAT_1.mulLeft(XMath.QUAT_2);
                                    transform.setQuaternion(XMath.QUAT_1);
                                }
                            }
                            else if(transformMode == XTransform.XTransformMode.LOCAL) {
                                if(!fixRotation(rotationSequence, transform, node, tRotX, tRotY, tRotZ)) {
//                                XpeRotation.convertEulerToQuat(rotationSequence, node.startRotation, XpeMath.QUAT_1, true);
                                    XMath.QUAT_1.set(node.startQuaternion);
                                    XMath.QUAT_2.setEulerAngles(tRotY, tRotX, tRotZ);
                                    XMath.QUAT_1.mul(XMath.QUAT_2);
                                    transform.setQuaternion(XMath.QUAT_1);
                                }
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

                    XMath.MAT4_2.idt();
                    XMath.MAT4_3.idt();
                    XMath.MAT4_1.idt();

                    XMath.MAT4_2.rotate(firstNode.startQuaternion);
                    XMath.MAT4_3.rotate(XMath.QUAT_1.set(firstRX, firstRY, firstRZ, firstRW));
                    XMath.MAT4_2.inv();
                    XMath.MAT4_1.mul(XMath.MAT4_3);  // Multiplication order is important here
                    XMath.MAT4_1.mul(XMath.MAT4_2);

                    Quaternion rotationOffset = XMath.QUAT_1;
                    XMath.MAT4_1.getRotation(rotationOffset);
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

                    transform.setPosition(newX, newY, newZ);

                    rotationOffset.set(rotOffsetX, rotOffsetY, rotOffsetZ, rotOffsetW);
                    XMath.MAT4_2.idt();
                    XMath.MAT4_3.idt();
                    XMath.MAT4_3.rotate(rotationOffset.mul(node.startQuaternion));
                    XMatrix4Utils.rotateAtOrigin(XMath.MAT4_2, XMath.MAT4_3, XMath.MAT4_1);

                    XMath.MAT4_1.getRotation(XMath.QUAT_1);
                    transform.setQuaternion(XMath.QUAT_1);
                }
            }
        }
    }

    private boolean fixRotation(XRotSeq rotationSequence, XTransform transform, XEntitySelectionNode node, float tRotX, float tRotY, float tRotZ) {
        // Prevent when increment the middle axis the values switch side and decrements the value when having a certain degree
        // Fix is only when one of the two remaining axis angle is 0

        if((rotationSequence == XRotSeq.yxz || rotationSequence == XRotSeq.zxy) && tRotX != 0 && (node.startQuaternion.y == 0 || node.startQuaternion.z == 0)) {
            transform.setRX(node.startRotation.x + tRotX);
            return true;
        }
        else if((rotationSequence == XRotSeq.xzy || rotationSequence == XRotSeq.yzx) && tRotZ != 0 && (node.startQuaternion.x == 0 || node.startQuaternion.y == 0)) {
            transform.setRZ(node.startRotation.z - tRotZ);
            return true;
        }
        else if((rotationSequence == XRotSeq.xyz || rotationSequence == XRotSeq.zyx) && tRotY != 0 && (node.startQuaternion.x == 0 || node.startQuaternion.z == 0)) {
            transform.setRY(node.startRotation.y - tRotY);
            return true;
        }
        return false;
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