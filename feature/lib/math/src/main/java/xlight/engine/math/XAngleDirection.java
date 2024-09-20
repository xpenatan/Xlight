package xlight.engine.math;

import com.badlogic.gdx.math.Quaternion;

/**
 * This class calculate which side of 2d or 3D the point is by checking an object position looking direction
 */
public class XAngleDirection {
    private static XAngleEnumData enumData = new XAngleEnumData();

    public static boolean ENABLE = true;

    public static XAngleEnumData getLocation(float x, float y, float z,
                                             Quaternion quaternion,
                                             float pointX, float pointY, float pointZ) {

        if(XAngleDirection.ENABLE == false) {
            enumData.axisX = XpeAngleEnum.TOP_RIGHT;
            enumData.axisY = XpeAngleEnum.TOP_RIGHT;
            enumData.axisZ = XpeAngleEnum.TOP_RIGHT;
            return enumData;
        }

        if(!quaternion.isIdentity()) {
            // Invert everything with angle to calculate the direction
            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(quaternion);
            XMath.MAT4_1.inv();
            XMath.MAT4_1.translate(x, y, z);
            XMath.MAT4_1.getTranslation(XMath.VEC3_1);
            x = XMath.VEC3_1.x;
            y = XMath.VEC3_1.y;
            z = XMath.VEC3_1.z;

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(quaternion);
            XMath.MAT4_1.inv();
            XMath.MAT4_1.translate(pointX, pointY, pointZ);
            XMath.MAT4_1.getTranslation(XMath.VEC3_1);
            pointX = XMath.VEC3_1.x;
            pointY = XMath.VEC3_1.y;
            pointZ = XMath.VEC3_1.z;
        }

        float angleZZ = XMath.getAngleBetweenTwoPosition(pointX, pointY, x, y);
        float angleXX = XMath.getAngleBetweenTwoPosition(pointZ, pointY, z, y);
        float angleYY = XMath.getAngleBetweenTwoPosition(pointX, -pointZ, x, -z); // from top looking down

//		System.out.println("angleYY: " + angleYY);

        if(angleZZ > 0 && angleZZ <= 90) {
            enumData.axisZ = XpeAngleEnum.TOP_RIGHT;
        }
        else if(angleZZ > 90 && angleZZ <= 180) {
            enumData.axisZ = XpeAngleEnum.TOP_LEFT;
        }
        else if(angleZZ > 180 && angleZZ <= 270) {
            enumData.axisZ = XpeAngleEnum.BOTTOM_LEFT;
        }
        else if(angleZZ > 270 && angleZZ <= 360) {
            enumData.axisZ = XpeAngleEnum.BOTTOM_RIGHT;
        }
        else
            enumData.axisZ = XpeAngleEnum.NONE;

        if(angleXX > 0 && angleXX <= 90) {
            enumData.axisX = XpeAngleEnum.TOP_RIGHT;
        }
        else if(angleXX > 90 && angleXX <= 180) {
            enumData.axisX = XpeAngleEnum.TOP_LEFT;
        }
        else if(angleXX > 180 && angleXX <= 270) {
            enumData.axisX = XpeAngleEnum.BOTTOM_LEFT;
        }
        else if(angleXX > 270 && angleXX <= 360) {
            enumData.axisX = XpeAngleEnum.BOTTOM_RIGHT;
        }
        else
            enumData.axisX = XpeAngleEnum.NONE;

        if(angleYY > 0 && angleYY <= 90) {
            enumData.axisY = XpeAngleEnum.TOP_RIGHT;
        }
        else if(angleYY > 90 && angleYY <= 180) {
            enumData.axisY = XpeAngleEnum.TOP_LEFT;
        }
        else if(angleYY > 180 && angleYY <= 270) {
            enumData.axisY = XpeAngleEnum.BOTTOM_LEFT;
        }
        else if(angleYY > 270 && angleYY <= 360) {
            enumData.axisY = XpeAngleEnum.BOTTOM_RIGHT;
        }
        else
            enumData.axisY = XpeAngleEnum.NONE;

        return enumData;
    }

    public static class XAngleEnumData {
        public XpeAngleEnum axisX = XpeAngleEnum.NONE;
        public XpeAngleEnum axisY = XpeAngleEnum.NONE;
        public XpeAngleEnum axisZ = XpeAngleEnum.NONE;
    }

    public enum XpeAngleEnum {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}