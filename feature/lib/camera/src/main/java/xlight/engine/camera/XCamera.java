package xlight.engine.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.Viewport;
import xlight.engine.impl.XCameraImpl;

public interface XCamera {
    static XCamera newInstance() { return new XCameraImpl(); }

    /** Return a read only position */
    Vector3 getPosition();
    void setPosition(float x, float y, float z);
    void setPosition(Vector3 position);
    void setTransform (final Matrix4 transform);
    void setX(float x);
    void setY(float y);
    void setZ(float z);
    void transform(final Matrix4 transform);

    float getViewportWidth();
    float getViewportHeight();
    void setZoom(float zoom);
    float getZoom();
    float getFar();
    void setFar(float value);
    float getNear();
    void setNear(float value);
    void setDirection(float x, float y, float z);
    Vector3 getDirection();
    void setUp(float x, float y, float z);
    Vector3 getUp();
    Matrix4 getProjection();
    void setProjection(Matrix4 value);
    Matrix4 getView();
    void setView(Matrix4 value);
    float getFieldOfView();
    void setFieldOfView(float value);

    Matrix4 getCombined();
    Frustum getFrustum();
    PROJECTION_MODE getProjectionMode();
    boolean setProjectionMode(PROJECTION_MODE projectionMode);
    void setViewport(Viewport viewport);
    Viewport getViewport();

    boolean updateCamera();

    void rotate (final Matrix4 transform);
    void rotate(final Quaternion quat);

    Camera asGDXCamera();

    boolean isActiveCamera();
    void setActiveCamera(boolean flag);

    void setDirty();
    boolean isDirty();

    void setType(int type);
    int getType();
    Ray getPickRay(float x, float y);
}
