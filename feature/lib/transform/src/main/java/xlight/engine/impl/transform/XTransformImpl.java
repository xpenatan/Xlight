package xlight.engine.impl.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import xlight.engine.math.XMath;
import xlight.engine.math.XMatrix4Utils;
import xlight.engine.math.XRotSeq;
import xlight.engine.math.XRotationUtils;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformListener;

public class XTransformImpl implements XTransform {
    final private Vector3 previousPosition;
    final private Vector3 position;
    final private Vector3 rotation;
    final private Vector3 scale;
    final private Vector3 size;
    final private Vector3 offset;
    final private BoundingBox boundingBox;
    final private Matrix4 matrix;
    final private Matrix4 matrixNotScaled;
    final private Quaternion quaternion;
    // Temp values are used for read only return values
    final private Vector3 tmpPosition;
    final private Vector3 tmpRotation;
    final private Vector3 tmpScale;
    final private Vector3 tmpSize;
    final private Vector3 tmpOffset;
    final private Quaternion tmpQuaternion;
    final private Matrix4 tmpMatrix;
    private XRotSeq rotationSequence;
    private boolean forceUpdate;

    private boolean enableX = true, enableY = true, enableZ = true;
    private boolean enableRotateX = true, enableRotateY = true, enableRotateZ = true;
    private int scalenRoundScale = 4;

    private boolean isDragging;
    private boolean initPreviousPosition;

    private Array<XTransformListener> transformListeners;

    public static final int LISTENER_CODE_TRANSFORM = 0;
    public static final int LISTENER_CODE_ROTATE = 1;
    public static final int LISTENER_CODE_SCALE = 2;

    public XTransformImpl() {
        previousPosition = new Vector3();
        position = new Vector3();
        rotation = new Vector3();
        scale = new Vector3();
        size = new Vector3();
        offset = new Vector3();
        boundingBox = new BoundingBox();
        matrix = new Matrix4();
        matrixNotScaled = new Matrix4();
        quaternion = new Quaternion();
        tmpPosition = new Vector3();
        tmpRotation = new Vector3();
        tmpScale = new Vector3();
        tmpSize = new Vector3();
        tmpOffset = new Vector3();
        tmpQuaternion = new Quaternion();
        tmpMatrix = new Matrix4();
        transformListeners = new Array<>();
        reset();
    }

    @Override
    public void reset() {
        initPreviousPosition = true;
        position.setZero();
        previousPosition.setZero();
        rotation.setZero();
        scale.set(1, 1, 1);
        size.set(1f, 1f, 1f);
        quaternion.idt();
        offset.set(0, 0, 0);

        forceUpdate = false;
        enableX = true;
        enableY = true;
        enableZ = true;
        enableRotateX = true;
        enableRotateY = true;
        enableRotateZ = true;
        matrix.idt();
        matrixNotScaled.idt();
        boundingBox.clr();
        scalenRoundScale = 4;
        rotationSequence = XRotSeq.yxz;

        isDragging = false;

        transformListeners.clear();
    }

    @Override
    public void setX(float x) {
        setPosition(x, position.y, position.z);
    }

    @Override
    public void setY(float y) {
        setPosition(position.x, y, position.z);
    }

    @Override
    public void setZ(float z) {
        setPosition(position.x, position.y, z);
    }

    /**
     * Return read only position.
     */
    @Override
    public Vector3 getPosition() {
        return tmpPosition.set(position);
    }

    @Override
    public void setPosition(Vector3 value) {
        setPosition(value.x, value.y, value.z);
    }

    @Override
    public void setPosition(Vector3 value, boolean callListener) {
        setPositionInternal(value.x, value.y, value.z, callListener);
    }

    @Override
    public void setRX(float rx) {
        setRotation(rx, rotation.y, rotation.z);
    }

    @Override
    public void setRY(float ry) {
        setRotation(rotation.x, ry, rotation.z);
    }

    @Override
    public void setRZ(float rz) {
        setRotation(rotation.x, rotation.y, rz);
    }

    @Override
    public Vector3 getRotation() {
        return tmpRotation.set(rotation);
    }

    @Override
    public void setRotation(Vector3 value) {
        setRotation(value.x, value.y, value.z);
    }

    @Override
    public void setRotation(Vector3 value, boolean callListener) {
        setRotation(value.x, value.y, value.z, callListener);
    }

    @Override
    public void setSX(float sx) {
        setScale(sx, scale.y, scale.z);
    }

    @Override
    public void setSY(float sy) {
        setScale(scale.x, sy, scale.z);
    }

    @Override
    public void setSZ(float sz) {
        setScale(scale.x, scale.y, sz);
    }

    @Override
    public Vector3 getScale() {
        return tmpScale.set(scale);
    }

    @Override
    public void setScale(Vector3 value) {
        setScale(value.x, value.y, value.z);
    }

    @Override
    public void setScale(Vector3 value, boolean callListener) {
        setScaleInternal(value.x, value.y, value.z, callListener);
    }

    @Override
    public Vector3 getSize() {
        return tmpSize.set(size);
    }

    @Override
    public void setSizeX(float value) {
        size.x = value;
    }

    @Override
    public void setSizeY(float value) {
        size.y = value;
    }

    @Override
    public void setSizeZ(float value) {
        size.z = value;
    }

    @Override
    public void setSize(Vector3 value) {
        setSize(value.x, value.y, value.z);
    }

    @Override
    public void setOffset(float x, float y, float z) {
        offset.set(x, y, z);
    }

    @Override
    public Vector3 getOffset() {
        return tmpOffset.set(offset);
    }

    @Override
    public void setOffsetX(float value) {
        offset.x = value;
    }

    @Override
    public void setOffsetY(float value) {
        offset.y = value;
    }

    @Override
    public void setOffsetZ(float value) {
        offset.z = value;
    }

    @Override
    public void setOffset(Vector3 value) {
        offset.set(value.x, value.y, value.z);
    }

    @Override
    public void setLocalBoundingBox(BoundingBox boundingBox) {
        float width = boundingBox.getWidth();
        float height = boundingBox.getHeight();
        float depth = boundingBox.getDepth();
        float centerX = boundingBox.getCenterX();
        float centerY = boundingBox.getCenterY();
        float centerZ = boundingBox.getCenterZ();
        float offsetX = centerX / width;
        float offsetY = centerY / height;
        float offsetZ = centerZ / depth;
        offset.set(offsetX, offsetY, offsetZ);
        setSize(width, height, depth);
    }

    @Override
    public BoundingBox getLocalBoundingBox() {
        float halfX = size.x / 2f;
        float halfY = size.y / 2f;
        float halfZ = size.z / 2f;
        float x1 = -halfX;
        float y1 = -halfY;
        float z1 = -halfZ;
        float x2 = halfX;
        float y2 = halfY;
        float z2 = halfZ;
        boundingBox.min.set(x1, y1, z1);
        boundingBox.max.set(x2, y2, z2);
        return boundingBox;
    }

    @Override
    public BoundingBox getBoundingBox() {
        XMath.MAT4_1.idt();
        XMath.MAT4_1.set(getMatrix4());

        float xx = offset.x * size.x;
        float yy = offset.y * size.y;
        float zz = offset.z * size.z;

        XMath.MAT4_1.translate(xx, yy, zz);

        BoundingBox localBoundingBox = getLocalBoundingBox();
        localBoundingBox.set(localBoundingBox);
        localBoundingBox.mul(XMath.MAT4_1);

        return localBoundingBox;
    }

    @Override
    public XRotSeq getRotationSequence() {
        return rotationSequence;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setDragging(boolean flag) {
        // TODO fix this
        isDragging = false;
    }

    @Override
    public void forceUpdate() {
        forceUpdate = true;
    }

    @Override
    public void addTransformListener(XTransformListener listener) {
        if(!transformListeners.contains(listener, true)) {
            transformListeners.add(listener);
        }
    }

    @Override
    public void removeTransformListener(XTransformListener listener) {
        transformListeners.removeValue(listener, true);
    }

    @Override
    public void callOnChangeListeners(int code) {
        for(int i = 0; i < transformListeners.size; i++) {
            transformListeners.get(i).onChange(this, code);
        }
    }

    @Override
    public Quaternion getQuaternion() {
        tmpQuaternion.set(quaternion);
        return tmpQuaternion;
    }

    @Override
    public void setRotation(Quaternion quat) {
        setRotation(quat.x, quat.y, quat.z, quat.w);
    }

    @Override
    public void setRotation(Quaternion quat, boolean callListener) {
        setRotation(quat.x, quat.y, quat.z, quat.w, callListener);
    }

    @Override
    public void setRotation(float x, float y, float z, float w) {
        setRotation(x, y, z, w, true);
    }

    @Override
    public void setRotation(float x, float y, float z, float w, boolean callListener) {
// We round it to skip small numbers
        float x1 = XMath.round(x, scalenRoundScale);
        float y1 = XMath.round(y, scalenRoundScale);
        float z1 = XMath.round(z, scalenRoundScale);
        float w1 = XMath.round(w, scalenRoundScale);
        float x2 = XMath.round(quaternion.x, scalenRoundScale);
        float y2 = XMath.round(quaternion.y, scalenRoundScale);
        float z2 = XMath.round(quaternion.z, scalenRoundScale);
        float w2 = XMath.round(quaternion.w, scalenRoundScale);
        if(!(x1 == x2 && y1 == y2 && z1 == z2 && w1 == w2)) {
            tmpQuaternion.set(x1, y1, z1, w1);
            tmpQuaternion.nor();
            XRotationUtils.convertQuatToEuler(rotationSequence, tmpQuaternion, XMath.VEC3_1, true);
            quaternion.set(tmpQuaternion);
            forceUpdate = true;
            setRotationInternal(XMath.VEC3_1.x, XMath.VEC3_1.y, XMath.VEC3_1.z, false, callListener);
        }
    }

    @Override
    public Matrix4 getMatrix4() {
        return getMatrix4(true);
    }

    @Override
    public Matrix4 getMatrix4(boolean applyScale) {
        if(!applyScale) {
            return tmpMatrix.set(matrixNotScaled);
        }
        return tmpMatrix.set(matrix);
    }

    @Override
    public void setSize(float sizeX, float sizeY, float sizeZ) {
        size.set(sizeX, sizeY, sizeZ);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        setPositionInternal(x, y, z, true);
    }

    @Override
    public void setPosition(float x, float y, float z, boolean callListener) {
        setPositionInternal(x, y, z, callListener);
    }

    @Override
    public void setRotation(float x, float y, float z) {
        setRotationInternal(x, y,z, true, true);
    }

    @Override
    public void setRotation(float x, float y, float z, boolean callListener) {
        setRotationInternal(x, y, z, true, callListener);
    }

    @Override
    public void setScale(float x, float y, float z) {
        setScaleInternal(x, y, z, true);
    }

    @Override
    public void setScale(float x, float y, float z, boolean callListener) {
        setScaleInternal(x, y, z, callListener);
    }

    private void setPositionInternal(float x, float y, float z, boolean shouldCallListener) {
        boolean forceChange = forceUpdate;
        forceUpdate = false;

        boolean toUpdate = false;
        if(forceChange) {
            toUpdate = true;
        }
        else {
            if(!isDragging && (position.x != x || position.y != y || position.z != z)) {
                toUpdate = true;
            }
        }

        if(toUpdate) {
            boolean flag = false;
            if(enableX || forceChange) {
                flag = true;
                previousPosition.x = position.x;
                position.x = x;
            }
            if(enableY || forceChange) {
                flag = true;
                previousPosition.y = position.y;
                position.y = y;
            }
            if(enableZ || forceChange) {
                flag = true;
                previousPosition.z = position.z;
                position.z = z;
            }

            if(flag) {
                if(initPreviousPosition) {
                    initPreviousPosition = false;
                    previousPosition.set(position);
                }
                updateValuesToMatrix();
                if(shouldCallListener) {
                    callOnChangeListeners(LISTENER_CODE_TRANSFORM);
                }
            }
        }
    }

    private void setRotationInternal(float rotationX, float rotationY, float rotationZ, boolean convertToQuat, boolean shouldCallListener) {
        boolean forceChange = forceUpdate;
        forceUpdate = false;

        boolean toUpdate = false;
        if(forceChange) {
            toUpdate = true;
        }
        else {
            if(!isDragging && (rotation.x != rotationX || rotation.y != rotationY || rotation.z != rotationZ)) {
                toUpdate = true;
            }
        }

        if(toUpdate) {
            boolean flag = false;
            if(enableX || forceChange) {
                flag = true;
                rotation.x = rotationX;
            }
            if(enableY || forceChange) {
                flag = true;
                previousPosition.y = position.y;
                rotation.y = rotationY;
            }
            if(enableZ || forceChange) {
                flag = true;
                previousPosition.z = position.z;
                rotation.z = rotationZ;
            }

            if(flag) {
                if(convertToQuat) {
                    XRotationUtils.convertEulerToQuat(rotationSequence, rotation, quaternion);
                }
                updateValuesToMatrix();
                if(shouldCallListener) {
                    callOnChangeListeners(LISTENER_CODE_ROTATE);
                }
            }
        }
    }

    private void setScaleInternal(float x, float y, float z, boolean shouldCallListener) {
        boolean forceChange = forceUpdate;
        forceUpdate = false;

        boolean toUpdate = false;
        if(forceChange) {
            toUpdate = true;
        }
        else {
            if(x > 0f && y > 0f && z > 0f) {
                if(!isDragging && (scale.x != x || scale.y != y || scale.z != z)) {
                    toUpdate = true;
                }
            }
        }

        if(toUpdate) {
            scale.x = x;
            scale.y = y;
            scale.z = z;
            updateValuesToMatrix();
            if(shouldCallListener) {
                callOnChangeListeners(LISTENER_CODE_SCALE);
            }
        }
    }

    private void updateValuesToMatrix() {
//            XRotationUtils.rotateMatrix(rotationSequence, rotation.x, rotation.y, rotation.z, matrix, false, true);
//        matrix.set(position, quaternion, scale);
//        matrixNotScaled.set(position, quaternion);
//        Quaternion quat = getQuaternion();
//        matrix.idt();
//        matrix.scale(scale.x, scale.y, scale.z);
//        matrix.rotate(quaternion);
//        matrix.translate(position);
//
//
//        matrixNotScaled.idt();
//        matrixNotScaled.rotate(quaternion);
//        matrixNotScaled.translate(position);

        XMatrix4Utils.toMatrix(XMatrix4Utils.MatrixOrder.TRS, position, quaternion, scale, matrix);
        XMatrix4Utils.toMatrix(XMatrix4Utils.MatrixOrder.TRS, position, quaternion, null, matrixNotScaled);
    }
}