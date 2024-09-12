package xlight.engine.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class XRotationUtils {
    /*
     * Array of info for Rotation Order calculations WARNING: must be kept in
     * same order as eEulerRotationOrders
     */

    static RotOrderInfo rotOrders[];

    static {
        rotOrders = new RotOrderInfo[6];
        rotOrders[0] = new RotOrderInfo(0, 1, 2, false); /* XYZ */
        rotOrders[1] = new RotOrderInfo(0, 2, 1, true); /* XZY */
        rotOrders[2] = new RotOrderInfo(1, 0, 2, true); /* YXZ */
        rotOrders[3] = new RotOrderInfo(1, 2, 0, false); /* YZX */
        rotOrders[4] = new RotOrderInfo(2, 0, 1, false); /* ZXY */
        rotOrders[5] = new RotOrderInfo(2, 1, 0, true); /* ZYX */
    }

    static class RotOrderInfo {
        int i;
        int j;
        int k;
        boolean parity;

        public RotOrderInfo(int i, int j, int k, boolean n) {
            this.i = i;
            this.j = j;
            this.k = k;
            this.parity = n;
        }

        int at(int index) {
            if(index == 0)
                return i;
            else if(index == 1)
                return j;
            else if(index == 2)
                return k;
            return 0;
        }
    }

    static RotOrderInfo get_rotation_order_info(short order) {
        assert (order >= 0 && order <= 6);
        if(order < 1)
            return rotOrders[0];
        else if(order < 6)
            return rotOrders[order - 1];
        else
            return rotOrders[5];
    }

    static float auxX;
    static float auxY;
    static float auxZ;

    static void threeaxisrot(double r11, double r12, double r21, double r31, double r32, Vector3 result) {
        double x = Math.atan2(r31, r32);
        double y = Math.asin(r21);
        double z = Math.atan2(r11, r12);

        double degX = x * MathUtils.radDeg;
        double degY = y * MathUtils.radDeg;
        double degZ = z * MathUtils.radDeg;

        float x1 = (float)degX;
        float y1 = (float)degY;
        float z1 = (float)degZ;

//        System.out.println("x: " + x + " y: " + y + " z: " + z);
//        System.out.println("x1: " + x1 + " y1: " + y1 + " z1: " + z1);
        result.x = XMath.round(x1, 2);
        result.y = XMath.round(y1, 2);
        result.z = XMath.round(z1, 2);
    }

    static void twoaxisrot(double r11, double r12, double r21, double r31, double r32, Vector3 result) {
        double x = Math.atan2(r11, r12);
        double y = Math.acos(r21);
        double z = Math.atan2(r31, r32);

        double degX = x * MathUtils.radDeg;
        double degY = y * MathUtils.radDeg;
        double degZ = z * MathUtils.radDeg;

        float x1 = (float)degX;
        float y1 = (float)degY;
        float z1 = (float)degZ;

        result.x = XMath.round(x1, 2);
        result.y = XMath.round(y1, 2);
        result.z = XMath.round(z1, 2);
    }

    public static void getAngleAround(XRotSeq rotSeq, Quaternion in, Vector3 out) {
        switch(rotSeq) {
            case yzx:
                out.x = in.getAngleAround(Vector3.Y);
                out.y = in.getAngleAround(Vector3.Z);
                out.z = in.getAngleAround(Vector3.X);
                break;
            case yxz:
                out.x = in.getAngleAround(Vector3.Y);
                out.y = in.getAngleAround(Vector3.X);
                out.z = in.getAngleAround(Vector3.Z);
                break;
            case zyx:
                out.x = in.getAngleAround(Vector3.Z);
                out.y = in.getAngleAround(Vector3.Y);
                out.z = in.getAngleAround(Vector3.X);
                break;
            case zxy:
                out.x = in.getAngleAround(Vector3.Z);
                out.y = in.getAngleAround(Vector3.X);
                out.z = in.getAngleAround(Vector3.Y);
                break;
            case xyz:
                out.x = in.getAngleAround(Vector3.X);
                out.y = in.getAngleAround(Vector3.Y);
                out.z = in.getAngleAround(Vector3.Z);
                break;
            case xzy:
                out.x = in.getAngleAround(Vector3.X);
                out.y = in.getAngleAround(Vector3.Z);
                out.z = in.getAngleAround(Vector3.Y);
                break;
        }
    }


    private static Quaternion QUAT = new Quaternion();
    public static Matrix4 MAT4_1 = new Matrix4();

    public static void convertQuatToEuler(XRotSeq rotSeq, final Quaternion quaternion, Vector3 out) {
        convertQuatToEuler(rotSeq, quaternion, out, true, false);
    }

    public static void convertQuatToEuler(XRotSeq rotSeq, final Quaternion quaternion, Vector3 out, boolean invert, boolean negAxis) {
        //http://bediyap.com/programming/convert-quaternion-to-euler-rotations/
        //https://forum.unity.com/threads/rotation-order.13469/

        // Y = heading
        // X = BANK
        // Z = ATTITUDE

        Quaternion q = QUAT.set(quaternion);
        q.nor();

        switch(rotSeq) {
            case yzx:
                threeaxisrot(-2 * (q.x * q.z - q.w * q.y),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z,
                        2 * (q.x * q.y + q.w * q.z),
                        -2 * (q.y * q.z - q.w * q.x),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z, out);

                if(invert) {
                    auxX = out.x;
                    auxY = out.z;
                    auxZ = out.y;
                    out.set(auxX, auxY, auxZ);
                }
                break;
            case yxz:
                threeaxisrot(2 * (q.x * q.z + q.w * q.y),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z,
                        -2 * (q.y * q.z - q.w * q.x),
                        2 * (q.x * q.y + q.w * q.z),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z, out);
                if(invert) {
                    auxX = out.y;
                    auxY = out.z;
                    auxZ = out.x;
                    out.set(auxX, auxY, auxZ);
                }
                break;
            case zyx:
                threeaxisrot(2 * (q.x * q.y + q.w * q.z),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z,
                        -2 * (q.x * q.z - q.w * q.y),
                        2 * (q.y * q.z + q.w * q.x),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z, out);
                if(invert) {
                }
                break;
            case zxy:
                threeaxisrot(-2 * (q.x * q.y - q.w * q.z),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z,
                        2 * (q.y * q.z + q.w * q.x),
                        -2 * (q.x * q.z - q.w * q.y),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z, out);
                if(invert) {
                    auxX = out.y;
                    auxY = out.x;
                    auxZ = out.z;
                    out.set(auxX, auxY, auxZ);
                }
                break;
            case xyz:
                threeaxisrot(-2 * (q.y * q.z - q.w * q.x),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z,
                        2 * (q.x * q.z + q.w * q.y),
                        -2 * (q.x * q.y - q.w * q.z),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z, out);
                if(invert) {
                    auxX = out.z;
                    auxY = out.y;
                    auxZ = out.x;
                    out.set(auxX, auxY, auxZ);
                }
                break;
            case xzy:
                threeaxisrot(2 * (q.y * q.z + q.w * q.x),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z,
                        -2 * (q.x * q.y - q.w * q.z),
                        2 * (q.x * q.z + q.w * q.y),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z, out);
                if(invert) {
                    auxX = out.z;
                    auxY = out.x;
                    auxZ = out.y;
                    out.set(auxX, auxY, auxZ);
                }
                break;
            case zyz:
                twoaxisrot(2 * (q.y * q.z - q.w * q.x),
                        2 * (q.x * q.z + q.w * q.y),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z,
                        2 * (q.y * q.z + q.w * q.x),
                        -2 * (q.x * q.z - q.w * q.y), out);
                break;
            case zxz:
                twoaxisrot(2 * (q.x * q.z + q.w * q.y),
                        -2 * (q.y * q.z - q.w * q.x),
                        q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z,
                        2 * (q.x * q.z - q.w * q.y),
                        2 * (q.y * q.z + q.w * q.x), out);
                break;
            case yxy:
                twoaxisrot(2 * (q.x * q.y - q.w * q.z),
                        2 * (q.y * q.z + q.w * q.x),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z,
                        2 * (q.x * q.y + q.w * q.z),
                        -2 * (q.y * q.z - q.w * q.x), out);
                break;
            case yzy:
                twoaxisrot(2 * (q.y * q.z + q.w * q.x),
                        -2 * (q.x * q.y - q.w * q.z),
                        q.w * q.w - q.x * q.x + q.y * q.y - q.z * q.z,
                        2 * (q.y * q.z - q.w * q.x),
                        2 * (q.x * q.y + q.w * q.z), out);
                break;
            case xyx:
                twoaxisrot(2 * (q.x * q.y + q.w * q.z),
                        -2 * (q.x * q.z - q.w * q.y),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z,
                        2 * (q.x * q.y - q.w * q.z),
                        2 * (q.x * q.z + q.w * q.y), out);
                break;
            case xzx:
                twoaxisrot(2 * (q.x * q.z - q.w * q.y),
                        2 * (q.x * q.y + q.w * q.z),
                        q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z,
                        2 * (q.x * q.z + q.w * q.y),
                        -2 * (q.x * q.y - q.w * q.z), out);
                break;
            default:
                break;
        }

        if(negAxis) {
            out.y *= -1;
            out.z *= -1;
        }

        out.x = XMath.normalizeAngle(out.x, 0 , 360);
        out.y = XMath.normalizeAngle(out.y, 0 , 360);
        out.z = XMath.normalizeAngle(out.z, 0 , 360);
    }

    public static void convertEulerToQuat(XRotSeq rotSeq, Vector3 in, Quaternion q, boolean negAxis) {
        // https://raw.githubusercontent.com/waiwnf/pilotguru/master/img/readme/pitch-roll-yaw.png
        // https://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm
        // http://www.euclideanspace.com/maths/geometry/rotations/euler/

        float x = in.x;
        float y = in.y;
        float z = in.z;

        if(negAxis) {
            y = -y;
            z = -z;
        }

        MAT4_1.idt();
        XRotationUtils.rotateMatrix(rotSeq, x, y, z, MAT4_1, false, false);
        MAT4_1.getRotation(q);

//        switch(rotSeq) {
//            case yzx:
//                q.setEulerAngles(y, z, x);
//                break;
//            case xyz:
//                q.setEulerAngles(x, y, z);
//                break;
//            case xzy:
//                q.setEulerAngles(x, z, y);
//                break;
//            case yxz:
//                q.setEulerAngles(y, x, z);
//                break;
//            case zxy:
//                q.setEulerAngles(z, x, y);
//                break;
//            case zyx:
//                q.setEulerAngles(z, y, x);
//                break;
//        }
    }

    /**
     * Convert rotation euler angles to quaternion. Values are passed in degrees
     */
    public static void convertEulerToQuat(float yaw, float pitch, float roll, Quaternion q) {
        q.idt();
        // https://raw.githubusercontent.com/waiwnf/pilotguru/master/img/readme/pitch-roll-yaw.png
        // https://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm
        // http://www.euclideanspace.com/maths/geometry/rotations/euler/
        // applied first     heading	 yaw    X
        // applied second    attitude    pitch  Y
        // applied last      bank        roll   Z

//        // Convert degree to radian
        float heading = yaw * MathUtils.degRad;
        float attitude = pitch * MathUtils.degRad;
        float bank = roll * MathUtils.degRad;

//        float bank = yaw * MathUtils.degRad;
//        float heading = pitch * MathUtils.degRad;
//        float attitude = roll * MathUtils.degRad;

        double c1 = Math.cos(heading/2);
        double s1 = Math.sin(heading/2);
        double c2 = Math.cos(attitude/2);
        double s2 = Math.sin(attitude/2);
        double c3 = Math.cos(bank/2);
        double s3 = Math.sin(bank/2);
        double c1c2 = c1*c2;
        double s1s2 = s1*s2;
        q.w = (float)(c1c2*c3 - s1s2*s3);
        q.x = (float)(c1c2*s3 + s1s2*c3);
        q.y = (float)(s1*c2*c3 + c1*s2*s3);
        q.z = (float)(c1*s2*c3 - s1*c2*s3);
    }


    public static void rotateMatrix(XRotSeq rotSeq, Vector3 in, Matrix4 out) {
        rotateMatrix(rotSeq, in.x, in.y, in.z, out);
    }

    public static void rotateMatrix(XRotSeq rotSeq, Vector3 in, Matrix4 out, boolean invert) {
        rotateMatrix(rotSeq, in.x, in.y, in.z, out, invert);
    }

    public static void rotateMatrix(XRotSeq rotSeq, Vector3 in, Matrix4 out, boolean invert, boolean negAxis) {
        rotateMatrix(rotSeq, in.x, in.y, in.z, out, invert, negAxis);
    }

    public static void rotateMatrix(XRotSeq rotSeq, float x, float y, float z, Matrix4 out) {
        rotateMatrix(rotSeq, x, y, z, out, false, true);
    }

    public static void rotateMatrix(XRotSeq rotSeq, float x, float y, float z, Matrix4 out, boolean invert) {
        rotateMatrix(rotSeq, x, y, z, out, invert, true);
    }

    public static void rotateMatrix(XRotSeq rotSeq, float x, float y, float z, Matrix4 out, boolean invert, boolean negAxis) {
        // Makes positive rotation values;
        if(negAxis) {
            y *= -1;
            z *= -1;
        }

        switch(rotSeq) {
            case yxz:
                if(invert) {
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Y, y);
                }
                else {
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Z, z);
                }
                break;
            case yzx:
                if(invert) {
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.Y, y);
                }
                else {
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.X, x);
                }
                break;
            case xyz:
                if(invert) {
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.X, x);
                }
                else {
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.Z, z);
                }
                break;
            case xzy:
                if(invert) {
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.X, x);
                }
                else {
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.Y, y);
                }
                break;
            case zxy:
                if(invert) {
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Z, z);
                }
                else {
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Y, y);
                }
                break;
            case zyx:
                if(invert) {
                    out.rotate(Vector3.X, x);
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.Z, z);
                }
                else {
                    out.rotate(Vector3.Z, z);
                    out.rotate(Vector3.Y, y);
                    out.rotate(Vector3.X, x);
                }
                break;
        }
    }

    public static void getMatrixRotation(XRotSeq rotSec, Matrix4 mat, Vector3 returnValue) {
        getMatrixRotation(rotSec, mat, true, returnValue);
    }

    public static void getMatrixRotation(XRotSeq rotSec, Matrix4 mat, boolean normalizeAxe, Vector3 returnValue) {
        Quaternion quat = QUAT;
        mat.getRotation(quat, normalizeAxe);
        convertQuatToEuler(rotSec, quat, returnValue);
    }

    // Testing method
    public static void getMatrixRotation(Matrix4 mat, Vector3 returnValue) {
        getMatrixRotation(mat, true, returnValue);
    }

    public static void getMatrixRotation(Matrix4 mat, boolean normalizeAxe, Vector3 returnValue) {
        Quaternion quat = QUAT;
        mat.getRotation(quat, normalizeAxe);
        returnValue.x = quat.getAngleAround(Vector3.X);
        returnValue.y = quat.getAngleAround(Vector3.Y);
        returnValue.z = quat.getAngleAround(Vector3.Z);
    }
}
