package xlight.engine.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import xlight.engine.camera.PROJECTION_MODE;

public class XMultiCamera extends Camera {

    public PROJECTION_MODE projectionMode;

    private final Vector3 tmp = new Vector3();

    // **************************** ORTHOGONAL
    public float zoom = 1;

    public XMultiCamera() {
        setProjectionMode(PROJECTION_MODE.ORTHOGONAL);
    }

    public void update2D(boolean updateFrustum) {
        projection.setToOrtho(zoom * -viewportWidth / 2, zoom * (viewportWidth / 2), zoom * -(viewportHeight / 2), zoom * viewportHeight / 2, near, far);
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        if(updateFrustum) {
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
        }
    }

    /**
     * Sets this camera to an orthographic projection, centered at
     * (viewportWidth/2, viewportHeight/2), with the y-axis pointing up or down.
     *
     * @param yDown          whether y should be pointing down.
     * @param viewportWidth
     * @param viewportHeight
     */
    public void setToOrtho(boolean yDown, float viewportWidth, float viewportHeight) {
        if(yDown) {
            up.set(0, -1, 0);
            direction.set(0, 0, 1);
        }
        else {
            up.set(0, 1, 0);
            direction.set(0, 0, -1);
        }
        position.set(zoom * viewportWidth / 2.0f, zoom * viewportHeight / 2.0f, 0);
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    // ***************************** PERSPECTIVE

    /**
     * the field of view of the height, in degrees
     **/
    public float fieldOfView = 67;

    public void update3D(boolean updateFrustum) {
        float aspect = viewportWidth / viewportHeight;
        projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        if(updateFrustum) {
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
        }
    }

    float centerX, centerY;
    float gutterWidth, gutterHeight;

    float lastWidth;
    float lastHeight;
    float width;
    float height;

    public void setViewport(int width, int height, boolean keepAspectRatio) {

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // if(this.keepAspectRatio == keepAspectRatio && width == this.width &&
        // height == this.height && lastWidth == screenWidth && lastHeight ==
        // screenHeight)
        // return;

        lastWidth = screenWidth;
        lastHeight = screenHeight;

        this.width = width;
        this.height = height;

        //coordinates.setToUpdate(true);
        float tmpWidth = 0;
        float tmpHeight = 0;

        if(keepAspectRatio) {

            if(screenHeight / screenWidth < height / (float)width) {
                float toScreenSpace = screenHeight / height;
                float toViewportSpace = height / screenHeight;
                float deviceWidth = width * toScreenSpace;
                float lengthen = (screenWidth - deviceWidth) * toViewportSpace;
                tmpWidth = width + lengthen;
                tmpHeight = height;
                gutterWidth = lengthen / 2;
                gutterHeight = 0;
            }
            else {
                float toScreenSpace = screenWidth / width;
                float toViewportSpace = width / screenWidth;
                float deviceHeight = height * toScreenSpace;
                float lengthen = (screenHeight - deviceHeight) * toViewportSpace;
                tmpHeight = height + lengthen;
                tmpWidth = width;
                gutterWidth = 0;
                gutterHeight = lengthen / 2;
            }
        }
        else {
            tmpWidth = width;
            tmpHeight = height;
            gutterWidth = 0;
            gutterHeight = 0;
        }

        centerX = tmpWidth / 2;
        centerY = tmpHeight / 2;

        // transform.setX(centerX);
        // transform.setY(centerY);
        // position.set(centerX, centerY, 0);
        viewportWidth = tmpWidth;
        viewportHeight = tmpHeight;
    }

    @Override
    public void update() {
        if(projectionMode == PROJECTION_MODE.ORTHOGONAL)
            update2D(true);
        else if(projectionMode == PROJECTION_MODE.PERSPECTIVE)
            update3D(true);
        else
            throw new GdxRuntimeException("Projection Mode not set");
    }

    @Override
    final public void update(boolean updateFrustum) {
        if(projectionMode == PROJECTION_MODE.ORTHOGONAL)
            update2D(updateFrustum);
        else if(projectionMode == PROJECTION_MODE.PERSPECTIVE)
            update3D(updateFrustum);
    }

    public PROJECTION_MODE getProjectionMode() {
        return projectionMode;
    }

    public boolean setProjectionMode(PROJECTION_MODE projectionMode) {
        if(projectionMode != this.projectionMode) {
            if(projectionMode == PROJECTION_MODE.ORTHOGONAL)
                near = 0;
            else
                near = 1;
            this.projectionMode = projectionMode;
            return true;
        }
        return false;
    }
}