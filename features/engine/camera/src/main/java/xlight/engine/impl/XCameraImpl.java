package xlight.engine.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;

public class XCameraImpl extends XMultiCamera implements XCamera {
    private int type = 0;
    public String tag = "";
    private Viewport viewport;

    private int screenWidth;
    private int screenHeight;
    private boolean isPreviewCamera = false;
    public boolean isActiveCamera;

    private boolean isDirty = true;

    /**
     * the position of the camera
     **/
    private final Vector3 tmp_position = new Vector3();
    /**
     * the unit length direction vector of the camera
     **/
    private final Vector3 tmp_direction = new Vector3(0, 0, -1);
    /**
     * the unit length up vector of the camera
     **/
    private final Vector3 tmp_up = new Vector3(0, 1, 0);

    /**
     * the projection matrix
     **/
    private final Matrix4 tmp_projection = new Matrix4();
    /**
     * the view matrix
     **/
    private final Matrix4 tmp_view = new Matrix4();
    /**
     * the combined projection and view matrix
     **/
    private final Matrix4 tmp_combined = new Matrix4();
    /**
     * the inverse combined projection and view matrix
     **/
    private final Matrix4 tmp_invProjectionView = new Matrix4();

    public XCameraImpl() {
        setProjectionMode(PROJECTION_MODE.ORTHOGONAL);
    }

    public XCameraImpl(Viewport viewport) {
        setProjectionMode(PROJECTION_MODE.ORTHOGONAL);
        setViewport(viewport);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
        viewport.setCamera(this);
        isDirty = true;
    }

    @Override
    public boolean updateCamera() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        boolean flag = false;
        if(this.screenWidth != width || this.screenHeight != height) {
            //TODO should not enter here every frame. Only if game window updated.
            this.screenWidth = width;
            this.screenHeight = height;
            flag = true;
        }
        if(flag || isDirty) {
            isDirty = false;
            if(viewport != null) {
                viewport.update(screenWidth, screenHeight, false);
            }
            else {
                update();
            }
        }
        return flag;
    }

    @Override
    public boolean setProjectionMode(PROJECTION_MODE mode) {
        boolean flag = super.setProjectionMode(mode);
        if(flag) {
            isDirty = true;
        }
        return flag;
    }

    @Override
    public PROJECTION_MODE getProjectionMode() {
        return super.getProjectionMode();
    }

    public void reset() {
        // TODO
    }

    public boolean isPreviewCamera() {
        return isPreviewCamera;
    }

    public void setPreviewCamera(boolean flag) {
        isPreviewCamera = flag;
    }

    @Override
    public float getFieldOfView() {
        return fieldOfView;
    }

    @Override
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
        isDirty = true;
    }

    @Override
    public float getZoom() {
        return zoom;
    }

    @Override
    public void setZoom(float value) {
        zoom = value;
        isDirty = true;
    }

    @Override
    public float getFar() {
        return this.far;
    }

    @Override
    public void setFar(float value) {
        this.far = value;
        isDirty = true;
    }

    @Override
    public float getNear() {
        return this.near;
    }

    @Override
    public void setNear(float value) {
        this.near = value;
        isDirty = true;
    }

    @Override
    public Vector3 getPosition() {
        tmp_position.set(position);
        return tmp_position;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        if(position.x != x || position.y != y || position.z != z) {
            position.x = x;
            position.y = y;
            position.z = z;
            isDirty = true;
        }
    }

    @Override
    public void setPosition(Vector3 position) {
        setPosition(position.x, position.y, position.z);
    }

    @Override
    public void setTransform(Matrix4 transform) {
        up.set(0, 1, 0);
        direction.set(0, 0, -1);
        transform(transform);
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

    @Override
    public Vector3 getDirection() {
        tmp_direction.set(direction);
        return tmp_direction;
    }

    @Override
    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
        isDirty = true;
    }

    @Override
    public Vector3 getUp() {
        tmp_up.set(up);
        return tmp_up;
    }

    @Override
    public void setUp(float x, float y, float z) {
        up.set(x, y, z);
        isDirty = true;
    }

    @Override
    public Matrix4 getProjection() {
        tmp_projection.set(projection);
        return tmp_projection;
    }

    @Override
    public void setProjection(Matrix4 value) {
        projection.set(value);
        isDirty = true;
    }

    @Override
    public Matrix4 getView() {
        tmp_view.set(view);
        return tmp_view;
    }

    @Override
    public void setView(Matrix4 value) {
        view.set(value);
        isDirty = true;
    }

    @Override
    public Matrix4 getCombined() {
        tmp_combined.set(combined);
        return tmp_combined;
    }

    public Matrix4 getInvProjectionView() {
        tmp_combined.set(invProjectionView);
        return tmp_invProjectionView;
    }

    @Override
    public float getViewportWidth() {
        return viewportWidth;
    }

    @Override
    public float getViewportHeight() {
        return viewportHeight;
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

    @Override
    public void rotate(final Matrix4 transform) {
        super.rotate(transform);
        isDirty = true;
    }

    @Override
    public void rotate(final Quaternion quat) {
        super.rotate(quat);
        isDirty = true;
    }

    @Override
    public Camera asGDXCamera() {
        return this;
    }

    @Override
    public boolean isActiveCamera() {
        return isActiveCamera;
    }

    @Override
    public void setActiveCamera(boolean flag) {
        isActiveCamera = flag;
    }

    public void setDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }
}