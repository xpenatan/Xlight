package xlight.engine.math;

import com.badlogic.gdx.utils.NumberUtils;

public class XColor {

    public static int toRGBAIntBits(int r, int g, int b, int a) {
        return (r << 24) | (g << 16) | (b << 8) | (a);
    }

    public static int toRGBAIntBits(float r, float g, float b, float a) {
        int rr = (int)(r * 255);
        int gg = (int)(g * 255);
        int bb = (int)(b * 255);
        int aa = (int)(a * 255);
        return toRGBAIntBits(rr, gg, bb, aa);
    }

    public static int toABGRIntBits(int r, int g, int b, int a) {
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static float toABGRFloatBits(int r, int g, int b, int a) {
        int color = toABGRIntBits(r, g, b, a);
        float floatColor = NumberUtils.intToFloatColor(color);
        return floatColor;
    }

    public static float toABGRFloatBits(float r, float g, float b, float a) {
        int rr = (int)(r * 255);
        int gg = (int)(g * 255);
        int bb = (int)(b * 255);
        int aa = (int)(a * 255);
        int color = toABGRIntBits(rr, gg, bb, aa);
        return NumberUtils.intToFloatColor(color);
    }
}
