package xlight.engine.math;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class XMath {

    /**
     * Pixel to Meter.
     */
    public final static float PTM = 0.02f;

    /**
     * Meter to Pixel.
     */
    public final static float MTP = 50.0f;

    public static Vector2 VEC2_1 = new Vector2();
    public static Vector2 VEC2_2 = new Vector2();

    public static Vector3 VEC3_1 = new Vector3();
    public static Vector3 VEC3_2 = new Vector3();
    public static Vector3 VEC3_3 = new Vector3();
    public static Vector3 VEC3_4 = new Vector3();
    public static Vector3 VEC3_5 = new Vector3();
    public static Vector3 VEC3_6 = new Vector3();
    public static Vector4 VEC4_1 = new Vector4();
    public static Vector4 VEC4_2 = new Vector4();
    public static Vector4 VEC4_3 = new Vector4();

    public static Quaternion QUAT_1 = new Quaternion();
    public static Quaternion QUAT_2 = new Quaternion();

    public static Matrix4 MAT4_1 = new Matrix4();
    public static Matrix4 MAT4_2 = new Matrix4();
    public static Matrix4 MAT4_3 = new Matrix4();
    public static Matrix4 MAT4_4 = new Matrix4();

    public static BoundingBox BOUNDING_BOX_1 = new BoundingBox();
    public static BoundingBox BOUNDING_BOX_2 = new BoundingBox();

    public static FloatArray F_ARRAY_1 = new FloatArray();

    public static ShortArray S_ARRAY_1 = new ShortArray();

    public static Color COLOR_1 = new Color();

    public static Bits BITS_1 = new Bits();
    public static Bits BITS_2 = new Bits();

    public static void clear(int r, int g, int b) {
        Gdx.gl20.glClearColor(r / 255f, g / 255f, b / 255, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Limpa a tela.
     */
    public static void clear() {
        clear(0, 0, 0);
    }

    /**
     * Ellipse check.
     */
    public static boolean ellipseCheck(float a, float b, float x, float y, float x2, float y2, float angle) {

        float A = (float)((Math.pow((((x - x2) * MathUtils.cosDeg(angle)) - (y - y2) * MathUtils.sinDeg(angle)), 2)) / (Math.pow(a, 2)));
        float B = (float)((Math.pow((((x - x2) * MathUtils.sinDeg(angle)) + (y - y2) * MathUtils.cosDeg(angle)), 2)) / (Math.pow(b, 2)));
        return A + B <= 1.0f;
    }

    /**
     * Circle check.
     *
     * @param radius the radius
     * @param x      the x
     * @param y      the y
     * @param x2     the x2
     * @param y2     the y2
     * @return true, if successful
     */
    public static boolean circleCheck(float radius, float x, float y, float x2, float y2) {
        return radius < XMath.getDistance(x, y, x2, y2);
    }

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = z1 - z2;

        // We should avoid Math.pow or Math.hypot due to perfomance reasons
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Retorna a distancia entre dois pontos.
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Return distance within two points. Object Angle is used to determine if distance is positive or negative
     */
    public static float getDistance(float angle, float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        float targetAngle = XMath.getAngleBetweenTwoPosition(x1, y1, x2, y2, true, true);
        float angleOffset = XMath.getAngleOffset(angle, targetAngle);
        if(angleOffset < 90 && angleOffset > -90) {
            distance *= -1;
        }
        return distance;
    }

    /**
     * Returns angle between 0 e 360.
     */
    public static float getAngleBetweenTwoPosition(float x1, float y1, float x2, float y2) {
        return getAngleBetweenTwoPosition(x1, y1, x2, y2, false, true);
    }

    /**
     * By giving 2 angles, return how many degrees is needed to get to the target <br>
     * Ex:
     * angle 300, target 350: 50 <br>
     * angle 300, target 260: -40 <br>
     */
    public static float getAngleOffset(float angle, float targetAngle) {
        angle = XMath.normalizeAngle(angle, -180 + angle, 180 + angle);
        targetAngle = XMath.normalizeAngle(targetAngle, -180 + angle, 180 + angle);
        return targetAngle - angle;
    }

    /**
     * Get Angle between 2 points using clockwise or anti clockwise rotation. <br>
     * True Clockwise rotation <br>
     * P1(0,0), P2(-1,0) = 0 degrees <br>
     * P1(0,0), P2(0,1) = 90 degrees <br>
     * P1(0,0), P2(1,0) = 180 degrees <br>
     * P1(0,0), P2(0,-1) = 270 degrees <br>
     * <br>
     * False Anti Clockwise rotation <br>
     * P1(0,0), P2(1,0) = 0 degrees <br>
     * P1(0,0), P2(0,1) = 90 degrees <br>
     * P1(0,0), P2(-1,0) = 180 degrees <br>
     * P1(0,0), P2(0,-1) = 270 degrees <br>
     */
    public static float getAngleBetweenTwoPosition(float x1, float y1, float x2, float y2, boolean clockwiseRotation, boolean makePositive) {
        float angleRad = 0;
        if(clockwiseRotation) {
            angleRad = -MathUtils.atan2(y1 - y2, x1 - x2);
        }
        else {
            angleRad = MathUtils.atan2(y2 - y1, x2 - x1);
        }

        if(angleRad < 0.0F && makePositive) {
            angleRad += Math.PI * 2;
        }
        float angle = angleRad * (180.0F / MathUtils.PI);
        return angle;
    }

    /**
     * Retorna em radiano de dois pontos.
     */
    public static float getRadian(float x1, float y1, float x2, float y2) {
        return MathUtils.degreesToRadians * XMath.getAngleBetweenTwoPosition(x1, y1, x2, y2);
    }

    /**
     * Uses Vector2.tmp
     * <p>
     * addiciona o centro X e Y com determinado anglo. Examplo: bom para ajustar
     * a posi��o da bala quando � atirado em um determinado angulo. Uses
     * Vector2.tmp
     */
    public static Vector2 getAimPosition(float angle, float centerX, float centerY) {

        float x1 = centerX * MathUtils.cosDeg(angle) - centerY * MathUtils.sinDeg(angle);
        float y1 = centerX * MathUtils.sinDeg(angle) + centerY * MathUtils.cosDeg(angle);

        VEC2_1.set(x1, y1);

        return VEC2_1;
    }

    /**
     * Uses Vector2.tmp and Vector2.tmp2
     */
    public static void getAimPosition(float centerX, float centerY, float x, float y, float destX, float destY) {
        float angle = getAngleBetweenTwoPosition(x, y, destX, destY, false,  true);

        float x1 = centerX * MathUtils.cosDeg(angle) - centerY * MathUtils.sinDeg(angle);
        float y1 = centerX * MathUtils.sinDeg(angle) + centerY * MathUtils.cosDeg(angle);
        //Vector2.tmp.set(x1, y1);

        x1 = centerX * MathUtils.cosDeg(angle) - centerY * MathUtils.sinDeg(angle);
        y1 = centerX * MathUtils.sinDeg(angle) + centerY * MathUtils.cosDeg(angle);
        //Vector2.tmp2.set(x1, y1);

    }

    /**
     *  Get target direction. Used to move an object to a target. It returns vector2 to add it to a position.
     */
    public static void getTargetDirection(Vector2 out, float x, float y, float targetX, float targetY, boolean isClockwiseRotation) {
        float angle = getAngleBetweenTwoPosition(x, y, targetX, targetY, isClockwiseRotation, true);
        out.x = MathUtils.cosDeg(angle);
        out.y = MathUtils.sinDeg(angle);
    }

    /**
     * Check which side the object should rotate to.
     * -1 if needs to turn left and +1 right
     */
    public static int checkAngle(float curAngle, float x, float y, float targetX, float targetY) {
        float targetAngle = getAngleBetweenTwoPosition(x, y, targetX, targetY);
        float angleCheck = checkAngle(curAngle, targetAngle);
        if(angleCheck > 180) {
            // left
            return -1;
        }
        else if(angleCheck < 180) {
            // right
            return 1;
        }
        return 0;
    }

    /**
     * Make angle stay in the start end range.
     */
    public static float normalizeAngle(float angle, float start, float end) {
        // 361 -> 1
        // -1  -> 359
        // 721 -> 1
        float width = end - start;
        float offsetValue = angle - start;
        return (float)(offsetValue - (Math.floor(offsetValue / width) * width)) + start;
    }

    /**
     * If value is close to targetValue than it returns the targetValue
     */
    public static float clampValueToTarget(float value, float targetValue, float precision) {
        // angle 90 || angle 92
        // targetAngle 91
        float minAngle = (targetValue - precision);
        float maxAngle = (targetValue + precision);
        if(value >= minAngle && value <= maxAngle) {
            return targetValue;
        }
        else {
            return value;
        }
    }

    /**
     * Check if the object that is to be checked is left or right of the angle
     * your facing.
     * <p>
     * if the return angle is bigger than 180 then the object is at left side.
     * <p>
     * if the return angle is less than 180 then the object is at right side.
     * <p>
     * Warning: Both angles needs to be between 0-360 degrees.
     */
    public static float checkAngle(float angle, float angleCheck) {
        float resto = 180 - angle;

        if(resto < 0) {
            resto = resto + 360;
        }

        float angle2 = angleCheck + resto;
        if(angle2 > 360) {
            angle2 = angle2 - 360;
        }

        return angle2;
    }

    /**
     * Essa interpolacao serve para diminuir os effeitos estranho na imagem para
     * hardware rapidos e lentos
     * <p>
     * Uses Vector3.tmp
     *
     * @param prevX       posicap x antes de entrar no acumulador
     * @param prevY       posicao y antes de entrar no acumulador
     * @param angle1      angulo antes de entrar no acumulador
     * @param x2          posicao x depois que saiu do acumulador
     * @param y2          posicaoo y depois que saiu do acumulador
     * @param angle2      angulo depois que saiu do acumulador
     * @param accumulator
     * @param tick
     * @return
     */

    public static Vector3 interpolation(float prevX, float prevY, float angle1, float x2, float y2, float angle2, float accumulator, float tick) {

        float alpha = accumulator / tick;
        float x3 = prevX * (1.0f - alpha) + x2 * alpha;
        float y3 = prevY * (1.0f - alpha) + y2 * alpha;
        float angle3 = angle1 * (1.0f - alpha) + angle2 * alpha;

        VEC3_1.x = x3;
        VEC3_1.y = y3;
        VEC3_1.z = angle3;
        return VEC3_1;
    }

    /**
     * Essa interpolacao serve para diminuir os effeitos estranho na imagem para
     * hardware rapidos e lentos
     */
    public static float interpolation(float pos, float prevPos, float alpha) {
        return pos * alpha + prevPos * (1.0f - alpha);
    }

    public static float metersToPixel(float meters) {
        return meters * MTP;
    }

    public static float pixelToMeters(float pixel) {
        return pixel * PTM;
    }

    /**
     * Meters to pixel
     */
    public static float MTP(float meters) {
        return metersToPixel(meters);
    }

    /**
     * Pixel to meters
     */
    public static float PTM(float pixel) {
        return pixelToMeters(pixel);
    }

    /**
     * Compara se dois floats sao iguais
     *
     * @param float1
     * @param float2
     * @param epsilon quantas casasar depois da virgula comparar Ex: 0.00000001
     * @return
     */

    public static boolean compareFloat(float float1, float float2, float epsilon) {
        return Math.abs(float1 - float2) < epsilon;
    }

    public static float round(float number, int scale) {
        boolean neg = false;
        if(number < 0) {
            neg = true;
            number *= -1;
        }

        int pow = 10;
        for(int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        // Error in JS The number NaN cannot be converted to a BigInt because it is not an integer
        float ret = (float)(long)((tmp - (long)tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
        if(neg)
            ret *= -1;
        return ret;
    }

    public static double round(double number, int scale) {
        boolean neg = false;
        if(number < 0) {
            neg = true;
            number *= -1;
        }

        int pow = 10;
        for(int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        double ret = (double)(long)((tmp - (long)tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
        if(neg)
            ret *= -1;
        return ret;
    }

    public static float convertDpToPixel(float dp) {
        float px = dp * Gdx.graphics.getDensity();
        return px;
    }

    public static float convertPixelsToDp(float px) {
        float dp = px / Gdx.graphics.getDensity();
        return dp;
    }

    private static Vector3 edge1 = new Vector3();
    private static Vector3 edge2 = new Vector3();
    private static Vector3 edge3 = new Vector3();
    private static Vector3 tvec = new Vector3();
    private static Vector3 qvec = new Vector3();
    private static Vector3 pvec = new Vector3();
    private static Vector3 s0 = new Vector3();
    private static Vector3 s1 = new Vector3();
    private static Vector3 s2 = new Vector3();
    private static Vector3 tmp0 = new Vector3();

    public static long intersectRayTriangle(Ray ray, Vector3 V0, Vector3 V1, Vector3 V2) {
        return intersectRayTriangle(ray.origin, ray.direction, V0, V1, V2);
    }

    public static long intersectRayTriangle(Vector3 P0, Vector3 dir, Vector3 V0, Vector3 V1, Vector3 V2) {
        // Libgdx triangle intersection has problem so this is a better version

        float det, invDet;
        edge1.set(V1).sub(V2);
        edge2.set(V2).sub(V0);
        pvec.set(dir).crs(edge2);
        det = edge1.dot(pvec);
        if(det == 0.0)
            return 0;
        invDet = 1.0f / det;
        tvec.set(P0).sub(V0);
        qvec.set(tvec).crs(edge1);
        float t = (edge2.dot(qvec)) * invDet;
        if(t < 0.0) {
            return 0;
        }
        edge3.set(V0).sub(V1);

        tmp0.set(dir).scl(t).add(P0);
        Vector3 I = new Vector3(tmp0);

        s0.set(tmp0.set(I).sub(V0)).crs(edge3);   // Vector3 s0 = (I - V0) ^ edge3;
        s1.set(tmp0.set(I).sub(V1)).crs(edge1);   // Vector3 s1 = (I - V1) ^ edge1;
        s2.set(tmp0.set(I).sub(V2)).crs(edge2);   // Vector3 s2 = (I - V2) ^ edge2;

        if(s0.dot(s1) > -1e-9 && s2.dot(s1) > -1e-9) {
            return 1;
        }
        return 0;
    }

    public static float getAxisDistance(float origin, float other) {
        // (x2 - x1 or otherX - origin) = offset
        // offset should add from origin.  origin + offset = real position
        return other - origin;
    }

    public static Matrix4 getRotationOffset(Quaternion first, Quaternion second) {
        XMath.MAT4_1.idt();
        XMath.MAT4_1.rotate(first);
        XMath.MAT4_1.inv();

        XMath.MAT4_2.idt();
        XMath.MAT4_2.rotate(second);

        XMath.MAT4_3.idt();
        XMath.MAT4_3.mul(XMath.MAT4_2); // Multiplication order is important here
        XMath.MAT4_3.mul(XMath.MAT4_1);
        return XMath.MAT4_3;
    }
}
