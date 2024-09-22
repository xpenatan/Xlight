package xlight.engine.impl.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.math.XMath;
import xlight.engine.math.XRotSeq;
import xlight.engine.math.XRotationUtils;
import xlight.engine.transform.XTransform;

public class XTransformImpl implements XTransform {
    final private Vector3 previousPosition = new Vector3();
    final private Vector3 position = new Vector3();
    final private Vector3 rotation = new Vector3();
    final private Vector3 scale = new Vector3();
    final private Vector3 size = new Vector3();
    final private Vector3 offset = new Vector3();
    final private BoundingBox boundingBox = new BoundingBox();
    final private Matrix4 matrix = new Matrix4();
    final private Matrix4 matrixNotScaled = new Matrix4();
    final private Quaternion quaternion = new Quaternion();
    // Temp values are used for read only return values
    final private Vector3 tmpPosition = new Vector3();
    final private Vector3 tmpRotation = new Vector3();
    final private Vector3 tmpScale = new Vector3();
    final private Vector3 tmpSize = new Vector3();
    final private Vector3 tmpOffset = new Vector3();
    final private Quaternion tmpQuaternion = new Quaternion();
    private XRotSeq rotationSequence;
    private boolean forceUpdate;

    private boolean enableX = true, enableY = true, enableZ = true;
    private boolean enableRotateX = true, enableRotateY = true, enableRotateZ = true;
    private boolean negAxis = true;
    private int scalenRoundScale = 4;

    private boolean isDragging;

    public XTransformImpl() {
        reset();
    }

    @Override
    public void reset() {
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
        negAxis = true;
        scalenRoundScale = 4;
        rotationSequence = XRotSeq.yxz;

        isDragging = false;
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
        isDragging = false;
    }

    @Override
    public void forceUpdate() {
        forceUpdate = true;
    }

    /**
     * Return read-only rotation.
     */
    public Quaternion getQuaternion() {
        tmpQuaternion.set(quaternion);
        return tmpQuaternion;
    }

    public void setQuaternion(Quaternion quat) {
        // We round it to skip small numbers
        float x1 = XMath.round(quat.x, scalenRoundScale);
        float y1 = XMath.round(quat.y, scalenRoundScale);
        float z1 = XMath.round(quat.z, scalenRoundScale);
        float w1 = XMath.round(quat.w, scalenRoundScale);
        float x2 = XMath.round(quaternion.x, scalenRoundScale);
        float y2 = XMath.round(quaternion.y, scalenRoundScale);
        float z2 = XMath.round(quaternion.z, scalenRoundScale);
        float w2 = XMath.round(quaternion.w, scalenRoundScale);
        if(!(x1 == x2 && y1 == y2 && z1 == z2 && w1 == w2)) {
            tmpQuaternion.set(x1, y1, z1, w1);
            XRotationUtils.convertQuatToEuler(rotationSequence, tmpQuaternion, XMath.VEC3_1, true, negAxis);
            quaternion.set(tmpQuaternion);
            forceUpdate = true;
            setRotationInternal(XMath.VEC3_1.x, XMath.VEC3_1.y, XMath.VEC3_1.z, false);
        }
    }

    @Override
    public Matrix4 getMatrix4() {
        return getMatrix4(true);
    }

    @Override
    public Matrix4 getMatrix4(boolean applyScale) {
        if(!applyScale) {
            return matrixNotScaled;
        }
        return matrix;
    }

    @Override
    public void setSize(float sizeX, float sizeY, float sizeZ) {
        size.set(sizeX, sizeY, sizeZ);
    }

    @Override
    public void setPosition(float x, float y, float z) {
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
                updateValuesToMatrix(matrix, matrixNotScaled);
            }
        }
    }

    @Override
    public void setRotation(float rotationX, float rotationY, float rotationZ) {
        setRotationInternal(rotationX, rotationY,rotationZ, true);
    }

    @Override
    public void setScale(float scaleX, float scaleY, float scaleZ) {
        boolean forceChange = forceUpdate;
        forceUpdate = false;

        boolean toUpdate = false;
        if(forceChange) {
            toUpdate = true;
        }
        else {
            if(!isDragging && (scale.x != scaleX || scale.y != scaleY || scale.z != scaleZ)) {
                toUpdate = true;
            }
        }

        if(toUpdate) {
            scale.x = scaleX;
            scale.y = scaleY;
            scale.z = scaleZ;
            updateValuesToMatrix(matrix, matrixNotScaled);
        }
    }

    private void setRotationInternal(float rotationX, float rotationY, float rotationZ, boolean convertToQuat) {
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
                    XRotationUtils.convertEulerToQuat(rotationSequence, rotation, quaternion, negAxis);
                }
                updateValuesToMatrix(matrix, matrixNotScaled);
            }
        }
    }

    private void updateValuesToMatrix(Matrix4 scaledMatrix, Matrix4 notScaledMatrix) {
        scaledMatrix.idt();
        scaledMatrix.translate(position.x, position.y, position.z);
//            XRotationUtils.rotateMatrix(rotationSequence, rotation.x, rotation.y, rotation.z, matrix, false, true);
        scaledMatrix.rotate(getQuaternion());
        if(notScaledMatrix != null) {
            notScaledMatrix.idt();
            notScaledMatrix.set(scaledMatrix);
        }
        scaledMatrix.scale(scale.x, scale.y, scale.z);
    }
}