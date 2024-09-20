package xlight.engine.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import xlight.engine.debug.XShapeRenderer;
import xlight.engine.math.XRotSeq;

public class XCursor3DRenderer implements Disposable {
    private boolean isDebug = false;

    boolean shift = false;
    boolean ctrl = false;
    boolean alt = false;

    private static Vector3 tmpNormal = new Vector3(1, 0, 0);
    public Vector3 normalRotated = new Vector3(1, 0, 0);
    private static Vector3 tmpUp = new Vector3(0, 1, 0);
    public Vector3 upRotated = new Vector3(0, 1, 0);

    private static Plane tmpPlane = new Plane();
    private Vector3 planePosition = new Vector3(0, 0, 0);

    Vector3 intersection = new Vector3();

    public static enum AXIS_TYPE {
        X, Y, Z, X_Y, X_Z, Y_Z
    }

    private AXIS_TYPE moveState = AXIS_TYPE.X_Y;

    ModelInstance planeModel;
    ModelBatch modelBatch;
    Environment environment;

    boolean intersectFlag;

    public Quaternion angle = new Quaternion();

    public XCursor3DRenderer() {
        create();
    }

    @Override
    public void dispose() {
    }

    public void setGlobalAngle(Quaternion angle) {
        this.angle.set(angle);
    }

    protected void create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(2f, 1f, 0.05f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
        planeModel = new ModelInstance(model);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(1.0f, 1.0f, 1.0f, 0, -1, 0));
    }

    public void update(Camera editorCam) {
        if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
            ctrl = true;
        else
            ctrl = false;
        if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
            shift = true;
        else
            shift = false;
        if(Gdx.input.isKeyPressed(Keys.ALT_LEFT))
            alt = true;
        else
            alt = false;


        if(alt && !ctrl && Gdx.input.isKeyPressed(Keys.X)) {
            moveState = AXIS_TYPE.X;
        }
        else if(alt && !ctrl && Gdx.input.isKeyPressed(Keys.Y)) {
            moveState = AXIS_TYPE.Y;
        }
        else if(alt && !ctrl && Gdx.input.isKeyPressed(Keys.Z)) {
            moveState = AXIS_TYPE.Z;
        }
        else if(ctrl && alt && Gdx.input.isKeyPressed(Keys.X)) {
            moveState = AXIS_TYPE.X_Y;
        }
        else if(ctrl && alt && Gdx.input.isKeyPressed(Keys.Y)) {
            moveState = AXIS_TYPE.Y_Z;
        }
        else if(ctrl && alt && Gdx.input.isKeyPressed(Keys.Z)) {
            moveState = AXIS_TYPE.X_Z;
        }
        else if(alt && Gdx.input.isKeyPressed(Keys.C)) {
//			setPlanePosition(position);
        }

        //		if(ctrl == false)
        {
            intersectFlag = getLockedAxisPosition(editorCam, moveState, angle, planePosition, intersection, upRotated, normalRotated);
        }
    }

    @Deprecated
    public void render(Camera editorCam, XShapeRenderer shapeRenderer) {

        drawBox(shapeRenderer, 0.1f, 0.1f, 0.1f, planePosition.x, planePosition.y, planePosition.z, 0, 0, 0, 0, 1, 1);

        if(isDebug) {
            drawBox(shapeRenderer, 0.1f, 0.1f, 0.1f, intersection.x, intersection.y, intersection.z, 0, 0, 0, 1, 1, 0);

            float x = planePosition.x;
            float y = planePosition.y;
            float z = planePosition.z;

            float scale = 2;

            planeModel.transform.idt();
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(x, y, z, x + scale * normalRotated.x, y + scale * normalRotated.y, z + scale * normalRotated.z);
            //		shapeRenderer.setColor(Color.RED);
            //		shapeRenderer.line(x, y, z, x + scale * upRotated.x, y + scale * upRotated.y, z + scale * upRotated.z );

            planeModel.transform.setToLookAt(planePosition, tmp_0.set(planePosition).add(normalRotated), upRotated);
            if(intersectFlag)
                planeModel.transform.inv();

            modelBatch.begin(editorCam);
            modelBatch.render(planeModel, environment);
            modelBatch.end();
        }
    }

    void drawBox(XShapeRenderer shapeRenderer, float sizeX, float sizeY, float sizeZ, float x, float y, float z, float rx, float ry, float rz, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1);
        //		shapeRenderer.identity();
        //		shapeRenderer.translate(x, y, z);
        //		shapeRenderer.rotate(0, 1, 0, ry);
        //		shapeRenderer.rotate(0, 0, 1, rz);
        //		shapeRenderer.rotate(1, 0, 0, rx);
        shapeRenderer.box(x + -sizeX / 2, y + -sizeY / 2, z + sizeZ / 2, sizeX, sizeY, sizeZ);
        //		shapeRenderer.identity();
    }

    public void set3DCursorPosition(float x, float y, float z) {
        planePosition.set(x, y, z);
    }

    public void set3DCursorPosition(Vector3 point) {
        planePosition.set(point);
    }

    public Vector3 getCursorPosition() {
        return planePosition;
    }

//	/**
//	 * Raycast to a distance of 3 meters.
//	 */
//	public void fireOrigin (Ray ray)
//	{
//		if (alt == false)
//			return;
//
//		rayFrom.set(ray.origin.scl(1));
//		rayTo.set(ray.direction).scl(3).add(rayFrom);
//		setPlanePosition(rayTo);
//	}

    public void setDebug(boolean debug) {
        this.isDebug = debug;
    }

    public void setMoveState(AXIS_TYPE state) {
        moveState = state;
    }

    private static Matrix4 tmpM0 = new Matrix4();

    private static Vector3 tmp_0 = new Vector3();
    private static Vector3 tmp_2 = new Vector3();
    private static Vector3 tmp_3 = new Vector3();
    private static Vector3 tmp_4 = new Vector3();
    private static Vector3 tmp_intersection = new Vector3();

    public static boolean getLockedAxisPosition(Camera editorCam, AXIS_TYPE moveState, Quaternion angle, Vector3 lockedPosition) {
        return getLockedAxisPosition(editorCam, moveState, angle, lockedPosition, tmp_intersection, tmp_3, tmp_4);
    }

    public static boolean getLockedAxisPosition(Camera editorCam, AXIS_TYPE moveState, XRotSeq rotationSequence, Quaternion angle, Vector3 lockedPosition, Vector3 intersection) {
        return getLockedAxisPosition(editorCam, moveState, angle, lockedPosition, intersection, tmp_3, tmp_4);
    }

    public static boolean getLockedAxisPosition(Camera editorCam, AXIS_TYPE moveState, Quaternion angle, Vector3 lockedPosition, Vector3 intersection, Vector3 upRotated, Vector3 normalRotated) {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Ray ray = editorCam.getPickRay(x, y);

        if(axisIntersected(editorCam, ray, moveState, intersection, upRotated, normalRotated, angle, lockedPosition)) {
            updateAxisLockedPosition(moveState, angle, intersection, lockedPosition);
            return true;
        }
        return false;
    }

    public static boolean axisIntersected(Camera editorCam, Ray ray, AXIS_TYPE moveState, Vector3 intersection, Quaternion angle, Vector3 lockedPosition) {
        return axisIntersected(editorCam, ray, moveState, intersection, tmp_3, tmp_4, angle, lockedPosition);
    }

    public static boolean axisIntersected(Camera editorCam, Ray ray, AXIS_TYPE moveState, Vector3 intersection, Vector3 upRotated, Vector3 normalRotated, Quaternion quaternion, Vector3 lockedPosition) {

        tmpUp.set(0, 1, 0);

        if(moveState == AXIS_TYPE.X_Y) {
            tmpNormal.set(0, 0, 1);
            tmpUp.set(0, 1, 0);
        }
        else if(moveState == AXIS_TYPE.X_Z) {
            tmpNormal.set(0, 1, 0);
            tmpUp.set(0, 0, -1);
        }
        else if(moveState == AXIS_TYPE.Y_Z) {
            tmpNormal.set(1, 0, 0);
            tmpUp.set(0, 1, 0);
        }
        else if(moveState == AXIS_TYPE.X) {
            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(tmp_2.set(editorCam.position).sub(lockedPosition));
            tmpM0.getTranslation(tmp_0);

            tmp_0.x = 0;
            tmpNormal.set(tmp_0).nor();
        }
        else if(moveState == AXIS_TYPE.Y) {
            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(tmp_2.set(editorCam.position).sub(lockedPosition));
            tmpM0.getTranslation(tmp_0);

            tmp_0.y = 0;
            tmpNormal.set(tmp_0).nor();
        }
        else if(moveState == AXIS_TYPE.Z) {

            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(tmp_2.set(editorCam.position).sub(lockedPosition));
            tmpM0.getTranslation(tmp_0);

            tmp_0.z = 0;
            tmpNormal.set(tmp_0).nor();
        }

        if(tmpNormal.x == 0 && tmpNormal.y == 0 && tmpNormal.z == 0) {
            return false;
        }

        tmpM0.idt();
        tmpM0.rotate(quaternion);
//        XpeRotation.rotateMatrix(rotationSequence, angle.x, angle.y, angle.z, tmpM0);
        tmpM0.translate(tmpNormal);
        tmpM0.getTranslation(normalRotated);
        normalRotated.nor();

        tmpM0.idt();
        tmpM0.rotate(quaternion);
//        XpeRotation.rotateMatrix(rotationSequence, angle.x, angle.y, angle.z, tmpM0);
        tmpM0.translate(tmpUp);
        tmpM0.getTranslation(upRotated);
        upRotated.nor();

        tmpPlane.set(lockedPosition.x, lockedPosition.y, lockedPosition.z, normalRotated.x, normalRotated.y, normalRotated.z);
        return Intersector.intersectRayPlane(ray, tmpPlane, intersection);
    }

    public static void updateAxisLockedPosition(AXIS_TYPE moveState, Quaternion quaternion, Vector3 hitPosition, Vector3 lockedPosition) {
        tmpM0.idt();
        tmpM0.rotate(quaternion);
        tmpM0.inv();
//        XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
        tmpM0.translate(hitPosition);
        tmpM0.getTranslation(tmp_0);

        if(moveState == AXIS_TYPE.X) {
            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(lockedPosition);
            tmpM0.getTranslation(tmp_2);

            tmp_0.y = tmp_2.y;
            tmp_0.z = tmp_2.z;

            tmpM0.idt();
            tmpM0.rotate(quaternion);
//            XpeRotation.rotateMatrix(rotationSequence, angle.x,angle.y, angle.z, tmpM0);
            tmpM0.translate(tmp_0);
            tmpM0.getTranslation(tmp_0);

            lockedPosition.set(tmp_0);
        }
        else if(moveState == AXIS_TYPE.Y) {
            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(lockedPosition);
            tmpM0.getTranslation(tmp_2);

            tmp_0.x = tmp_2.x;
            tmp_0.z = tmp_2.z;

            tmpM0.idt();
            tmpM0.rotate(quaternion);
//            XpeRotation.rotateMatrix(rotationSequence, angle.x,angle.y, angle.z, tmpM0);
            tmpM0.translate(tmp_0);
            tmpM0.getTranslation(tmp_0);

            lockedPosition.set(tmp_0);
        }
        else if(moveState == AXIS_TYPE.Z) {
            tmpM0.idt();
            tmpM0.rotate(quaternion);
            tmpM0.inv();
//            XpeRotation.rotateMatrix(rotationSequence, -angle.x,-angle.y, -angle.z, tmpM0, true);
            tmpM0.translate(lockedPosition);
            tmpM0.getTranslation(tmp_2);

            tmp_0.x = tmp_2.x;
            tmp_0.y = tmp_2.y;

            tmpM0.idt();
            tmpM0.rotate(quaternion);
//            XpeRotation.rotateMatrix(rotationSequence, angle.x, angle.y, angle.z, tmpM0);

            tmpM0.translate(tmp_0);
            tmpM0.getTranslation(tmp_0);

            lockedPosition.set(tmp_0);
        }
        else {
            lockedPosition.set(hitPosition);
        }
    }
}