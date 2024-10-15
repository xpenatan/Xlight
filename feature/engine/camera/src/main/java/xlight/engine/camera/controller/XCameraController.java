package xlight.engine.camera.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.github.xpenatan.gdx.multiview.EmuInput;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;

public class XCameraController {
    private final Vector3 tmp = new Vector3();

    private XVelocityTracker velocityTracker = new XVelocityTracker();

    private XCamera camera;
    private float velocity = 5;

    public float targetRY;
    public float targetRX;
    public float targetX;
    public float targetY;
    public float targetZ;

    public float rotationX;
    public float rotationY;
    public float rotationZ;

    float rotationSpringiness = 0.95f; // tweak to taste.
    float positionSpringiness = 15; // tweak to taste.
    boolean smooth = true;
    public boolean enableSlide = false;
    private float degreesPerPixel = 0.3f;

    private float rotationXtmp;
    private float rotationYtmp;
    private float rotationZtmp;
    public Matrix4 transformation = new Matrix4();
    private final boolean yUp = true;
    private boolean pan;

    float zoomAmount = 0.03f;

    private boolean rightShift;
    private boolean leftShift;
    private boolean rightCtrl;
    private boolean leftCtrl;

    private int STRAFE_LEFT = Input.Keys.A;
    private int STRAFE_RIGHT = Input.Keys.D;
    private int FORWARD = Input.Keys.W;
    private int BACKWARD = Input.Keys.S;
    private int UP = Input.Keys.E;
    private int DOWN = Input.Keys.Q;

    private boolean moveRight = false;
    private boolean moveLeft = false;
    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveUp = false;
    private boolean moveDown = false;

    private boolean rightButtonPressed;
    private int dragState = 0;

    private int mouseX;
    private int mouseY;

    private int deltaX;
    private int deltaY;

    private int lastDragX;
    private int lastDragY;

    public void update() {
        if(camera == null) {
            return;
        }

        float deltaTime = Gdx.graphics.getDeltaTime();

        moveRight = Gdx.input.isKeyPressed(STRAFE_RIGHT);
        moveLeft = Gdx.input.isKeyPressed(STRAFE_LEFT);
        moveForward = Gdx.input.isKeyPressed(FORWARD);
        moveBackward = Gdx.input.isKeyPressed(BACKWARD);
        moveUp = Gdx.input.isKeyPressed(UP);
        moveDown = Gdx.input.isKeyPressed(DOWN);

        rightShift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        leftShift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        rightCtrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        leftCtrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        if(leftCtrl) {
            // Cancel movements if ctrl is pressed
            moveRight = false;
            moveLeft = false;
            moveForward = false;
            moveBackward = false;
            moveUp = false;
            moveDown = false;
        }

        boolean cursorCatched = Gdx.input.isCursorCatched();
        int oldMouseX = mouseX;
        int oldMouseY = mouseY;
        mouseX = Gdx.input.getX();
        mouseY = Gdx.input.getY();

        deltaX = mouseX - oldMouseX;
        deltaY = mouseY - oldMouseY;

        rightButtonPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        if(rightButtonPressed) {
            if(dragState == 0) {
                lastDragX = mouseX;
                lastDragY = mouseY;
                dragState = 1;
            }
            else if(dragState == 1 && !(lastDragX == mouseX && lastDragY == mouseY)) {
                dragState = 2;

                // TODO setCursorCatched bug deltaX/Y by jumping to big values. Need to find a way to fix it.
//                Gdx.input.setCursorCatched(true);
                pan = true;
            }
        }
        else {
            if(dragState == 2 || dragState == 1) {
//                Gdx.input.setCursorCatched(false);
            }
            dragState = 0;
            pan = false;
        }

        if(dragState == 2) {
            touchDragged(mouseX, mouseY);
        }


        if(Gdx.input instanceof EmuInput) {
            EmuInput input = (EmuInput)Gdx.input;
            float scrollY = input.getScrollY();
            if(scrollY != 0) {
                scrolled(scrollY);
            }
        }
        moveCamera(deltaTime);

        if(enableSlide == false)
            smooth(Gdx.graphics.getDeltaTime());

    }

    public boolean touchDragged(int screenX, int screenY) {
        if(enableSlide) {
            if(camera != null && pan) {
                velocityTracker.update(screenX, screenY, Gdx.input.getCurrentEventTime());
                float zoom = camera.getZoom();
                float centerX = velocityTracker.deltaX * zoom;
                float centerY = velocityTracker.deltaY * zoom;

                tmp.set(camera.getDirection()).crs(camera.getUp()).scl(-centerX, -centerX, -centerX);
                Vector3 position = camera.getPosition();
                position.add(tmp);
                tmp.set(camera.getUp()).nor().scl(centerY);
                position.add(tmp);

                targetX = position.x;
                targetY = position.y;
                targetZ = position.z;
                camera.setPosition(targetX, targetY, targetZ);
                return true;
            }
        }
        else {
            if(pan) {
                float deltaX = -this.deltaX * degreesPerPixel;
                float deltaY = -this.deltaY * degreesPerPixel;
                rotate(deltaX); // mouse pros lados altera o Y na coordenada cartaseana se Y aponta para cima
                tilt(deltaY);
                updateRotation();
            }
        }
        return false;
    }

    public boolean scrolled(float amountY) {
        if(leftShift == false) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL) {
                float tmpAmount = zoomAmount;
                if(leftCtrl)
                    tmpAmount = zoomAmount * 10;
                float tmpZoom = camera.getZoom() + (amountY * tmpAmount);
                if(tmpZoom > 0.01f)
                    camera.setZoom(tmpZoom);
            }
            else if(pan) {
                float tmpAmount = 0;
                float multiply = 1;
                if(leftCtrl)
                    multiply = 20;

                tmpAmount = velocity - (amountY * multiply) * 5;

                float min = 0.1f;
                if(tmpAmount > min)
                    velocity = tmpAmount;
                else
                    velocity = min;
            }
        }
        return false;
    }

    void rotate(float angle) {
        this.targetRY += angle;

        if(smooth == false)
            return;
        float oneEntireLap = 6.283186F * MathUtils.radDeg;

        if(this.targetRY > oneEntireLap) {
            this.targetRY -= oneEntireLap;
            this.rotationY -= oneEntireLap;
        }
        if(this.targetRY < 0.0F) {
            this.targetRY += oneEntireLap;
            this.rotationY += oneEntireLap;
        }
    }

    void tilt(float angle) {
        this.targetRX += angle;

        if(smooth == false)
            return;

        if(this.targetRX > 1.570796326794897D * MathUtils.radDeg) {
            this.targetRX = 1.570796F * MathUtils.radDeg;
        }
        if(this.targetRX < -1.570796326794897D * MathUtils.radDeg) {
            this.targetRX = -1.570796F * MathUtils.radDeg;
        }
    }

    private void moveCamera(float deltaTime) {

        if(moveForward) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL)
                return;
            tmp.set(camera.getDirection()).nor().scl(deltaTime * velocity);
            targetX += tmp.x;
            targetY += tmp.y;
            targetZ += tmp.z;
        }
        if(moveBackward) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL)
                return;
            tmp.set(camera.getDirection()).nor().scl(-deltaTime * velocity);
            targetX += tmp.x;
            targetY += tmp.y;
            targetZ += tmp.z;
        }
        if(moveLeft) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL)
                return;
            tmp.set(camera.getDirection()).crs(camera.getUp()).nor().scl(-deltaTime * velocity);
            targetX += tmp.x;
            targetY += tmp.y;
            targetZ += tmp.z;
        }
        if(moveRight) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL)
                return;
            tmp.set(camera.getDirection()).crs(camera.getUp()).nor().scl(deltaTime * velocity);
            targetX += tmp.x;
            targetY += tmp.y;
            targetZ += tmp.z;
        }

        if(moveUp) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL) {
                if(yUp)
                    rotationZ++;
                else
                    rotationX++;
                updateRotation();
                return;
            }
            if(yUp) {
                if(leftShift) {
                }
                else {
                    tmp.set(Vector3.Y);
                    tmp.rotate(Vector3.X, rotationX);
                    tmp.rotate(Vector3.Y, rotationY);
                    tmp.rotate(Vector3.Z, rotationZ);
                    tmp.scl(deltaTime * velocity);
                }
            }
            else {
                tmp.set(Vector3.Z).nor().scl(deltaTime * velocity);
            }
            if(leftShift == false) {
                targetX += tmp.x;
                targetY += tmp.y;
                targetZ += tmp.z;
            }
        }

        if(moveDown) {
            if(camera.getProjectionMode() == PROJECTION_MODE.ORTHOGONAL) {
                if(yUp)
                    rotationZ--;
                else
                    rotationX--;
                updateRotation();
                return;
            }

            if(yUp) {
                if(leftShift) {
                }
                else {
                    tmp.set(Vector3.Y);
                    tmp.rotate(Vector3.X, rotationX);
                    tmp.rotate(Vector3.Y, rotationY);
                    tmp.rotate(Vector3.Z, rotationZ);
                    tmp.scl(-deltaTime * velocity);
                }
            }
            else {
                tmp.set(Vector3.Z).nor().scl(-deltaTime * velocity);
            }
            if(leftShift == false) {
                targetX += tmp.x;
                targetY += tmp.y;
                targetZ += tmp.z;
            }
        }
    }

    private void smooth(float time_d) {
        //	    double d = 1f - Math.exp(Math.log(0.5) * springiness * time_d);

        float x = 0;
        float y = 0;
        float z = 0;

        if(smooth == false) {
            x = targetX;
            y = targetY;
            z = targetZ;

            rotationX = targetRX;
            rotationY = targetRY;
        }
        else {
            rotationX += (targetRX - rotationX) * rotationSpringiness;
            rotationY += (targetRY - rotationY) * rotationSpringiness;

            Vector3 position = camera.getPosition();
            x = position.x;
            y = position.y;
            z = position.z;

            x += (targetX - x) * positionSpringiness * time_d;
            y += (targetY - y) * positionSpringiness * time_d;
            z += (targetZ - z) * positionSpringiness * time_d;
        }

        camera.setPosition(x, y, z);
        updateRotation();
    }

    private void updateRotation() {

        if(rotationXtmp != rotationX || rotationYtmp != rotationY || rotationZtmp != rotationZ) {
            rotationXtmp = rotationX;
            rotationYtmp = rotationY;
            rotationZtmp = rotationZ;
//			camera.setNeedToUpdate(); // solution to stop updating every frame, only if rotation changes
        }

        transformation.idt();
        if(yUp) {
            transformation.rotate(Vector3.Y, rotationY);
            transformation.rotate(Vector3.X, rotationX);
            transformation.rotate(Vector3.Z, rotationZ);

            camera.setDirection(0, 0, -1);
            camera.setUp(0, 1, 0);
            camera.rotate(transformation);
        }
        else {

            transformation.rotate(Vector3.Z, rotationZ);
            transformation.rotate(Vector3.Y, rotationY);
            transformation.rotate(Vector3.X, rotationX);

            camera.setDirection(0, 1, 0);
            camera.setUp(0, 0, 1);
            camera.rotate(transformation);
        }
    }

    public void setCamera(XCamera camera) {
        if(camera == this.camera)
            return;

        this.camera = camera;

        if(camera != null) {
            Vector3 position = this.camera.getPosition();
            targetX = position.x; // update target also
            targetY = position.y;
            targetZ = position.z;
        }
    }

    public void setPosition(Vector3 pos) {
        targetX = pos.x;
        targetY = pos.y;
        targetZ = pos.z;
        camera.setPosition(targetX, targetY, targetZ);
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}