package xlight.engine.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.impl.transform.XTransformImpl;
import xlight.engine.math.XRotSeq;

public interface XTransform {

    static XTransform newInstance() { return new XTransformImpl(); }

    void reset();

    /** Return read-only position. */
    Vector3 getPosition();
    void setX(float value);
    void setY(float value);
    void setZ(float value);
    void setPosition(Vector3 value);
    void setPosition(float x, float y, float z);

    /** Return read-only rotation. */
    Vector3 getRotation();
    /** Return read-only quaternion. */
    Quaternion getQuaternion();
    /** Return read-only scaled matrix. Apply scale is true */
    Matrix4 getMatrix4();
    /** Return read-only scaled matrix. */
    Matrix4 getMatrix4(boolean applyScale);
    void setRX(float value);
    void setRY(float value);
    void setRZ(float value);
    void setRotation(Vector3 value);
    void setRotation(float x, float y, float z);
    /** Will copy quat values and convert it to euler. */
    void setQuaternion(Quaternion quat);

    /** Return read-only scale. */
    Vector3 getScale();
    void setSX(float value);
    void setSY(float value);
    void setSZ(float value);
    void setScale(Vector3 value);
    void setScale(float x, float y, float z);

    /** Return read-only size. */
    Vector3 getSize();
    void setSizeX(float value);
    void setSizeY(float value);
    void setSizeZ(float value);
    void setSize(Vector3 value);
    void setSize(float x, float y, float z);

    /** Return read-only offset. */
    Vector3 getOffset();
    void setOffsetX(float value);
    void setOffsetY(float value);
    void setOffsetZ(float value);
    void setOffset(Vector3 value);
    void setOffset(float x, float y, float z);

    /** return local bounding box */
    void setLocalBoundingBox(BoundingBox boundingBox);
    /** return local bounding box */
    BoundingBox getLocalBoundingBox();
    BoundingBox getBoundingBox();
    XRotSeq getRotationSequence();

    boolean isDragging();
    void setDragging(boolean flag);
    void forceUpdate();

    class XTransformMode {
        final public static int GLOBAL = 0;
        final public static int LOCAL = 1;
    }
}