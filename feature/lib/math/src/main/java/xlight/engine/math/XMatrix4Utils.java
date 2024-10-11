package xlight.engine.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class XMatrix4Utils {

    private static Matrix4 tmpTransform = new Matrix4();
    private static Matrix4 tmpRotate = new Matrix4();
    private static Matrix4 tmpScale = new Matrix4();

    public static void rotateAroundOrigin(Matrix4 translate, Matrix4 rotation, Matrix4 out) {
        // https://www.youtube.com/watch?v=IZFHwd03nO4
        // T * R = out
        out.set(translate);
        out.mulLeft(rotation);
        // Or
//        out.set(rotation);
//        out.mul(translate);
    }

    public static void rotateAtOrigin(Matrix4 translate, Matrix4 rotation, Matrix4 out) {
        // R * T = out
        out.set(rotation);
        out.mulLeft(translate);
        // Or
//        out.set(translate);
//        out.mul(rotation);
    }

    /**
     * B_local = A_world^-1 * B_world
     */
    public static void getChildLocalFromWorld(Matrix4 A_world, Matrix4 B_world, Matrix4 B_local) {
        if(B_local == A_world || B_local == B_world) {
            throw  new RuntimeException("Out Matrix should not be parent or child");
        }
        B_local.set(A_world).inv().mul(B_world);
    }

    /**
     * B_world = A_world * B_local
     */
    public static void getChildWorldFromLocal(Matrix4 A_world, Matrix4 B_local, Matrix4 B_world) {
        if(B_world == A_world || B_world == B_local) {
            throw  new RuntimeException("Out Matrix should not be parent or child");
        }
        B_world.set(A_world).mul(B_local);
    }

    public static void toMatrix(MatrixOrder order, Vector3 position, Quaternion rotation, Vector3 scale, Matrix4 outWorldChild) {
        outWorldChild.idt();
        tmpTransform.idt();
        tmpRotate.idt();
        tmpScale.idt();
        if(position != null) {
            tmpTransform.translate(position);
        }
        if(rotation != null) {
            tmpRotate.rotate(rotation);
        }
        if(scale != null) {
            tmpScale.scale(scale.x, scale.y, scale.z);
        }

        if(order == MatrixOrder.TRS) {
            outWorldChild.set(tmpTransform).mul(tmpRotate).mul(tmpScale);
        }
        else if(order == MatrixOrder.SRT) {
            outWorldChild.set(tmpScale).mul(tmpRotate).mul(tmpTransform);
        }
    }

    static Quaternion row(Matrix4 mat, int row) {
        Quaternion quat = new Quaternion();
        return new Quaternion(
                mat.val[row * 4],
                mat.val[row * 4 + 1],
                mat.val[row * 4 + 2],
                mat.val[row * 4 + 3]
        );
    }

    static double toDegrees(double radians) {
//        return radians * (180 / MathUtils.PI);
        return radians * MathUtils.radDeg;
    }

    //Testing method
    public static void extractEulerAngleXYZ(Matrix4 mat, Vector3 out) {
        double rotXangle = Math.atan2(-row(mat, 1).z, row(mat, 2).z);
        double cosYangle = Math.sqrt(Math.pow(row(mat, 0).x, 2) + Math.pow(row(mat, 0).y, 2));
        double rotYangle = Math.atan2(row(mat, 0).z, cosYangle);
        double sinXangle = Math.sin(rotXangle);
        double cosXangle = Math.cos(rotXangle);
        double rotZangle = Math.atan2(cosXangle * row(mat, 1).x + sinXangle * row(mat, 2).x, cosXangle * row(mat, 1).y + sinXangle * row(mat, 2).y);
        int rotationRoundScale = 2;

        double rx = toDegrees(rotXangle);
        double ry = toDegrees(rotYangle);
        double rz = toDegrees(rotZangle);
        float rrx = XMath.round((float)rx, rotationRoundScale);
        float rry = XMath.round((float)ry, rotationRoundScale);
        float rrz = XMath.round((float)rz, rotationRoundScale);
        out.set(rrx, rry, rrz);
    }

    public enum MatrixOrder {
        TRS,
        SRT
    }
}
