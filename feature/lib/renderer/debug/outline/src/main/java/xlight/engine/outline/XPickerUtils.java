package xlight.engine.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;

public class XPickerUtils {

    /**
     * Needs to be inside framebuffer begin/end
     */
    public static int readPixel(int x, int y, int windowWidth, int windowHeight, Texture texture) {
        // get width and height is required. Does not working if its not called
        int width = texture.getWidth();
        int height = texture.getHeight();

        float windowW = windowWidth;
        float windowH = windowHeight;

        int x1 = x;
        int y1 = y;

//				System.out.println("X1: " + x1 + " Y1: " + y1);
        // Flip because the original texture is flipped
        y1 = (int)(windowH - y1);

//				System.out.println("X2: " + x1 + " Y2: " + y1);
        x1 = (int)((width * x1) / windowW);
        y1 = (int)((height * y1) / windowH);

        int color = readPixel(x1, y1);

        if(color == -1) {
            return color;
        }

        //Remove alpha from RGBA int
        color = (color & 0x000000FF);

        return color;
    }

    /**
     * Needs to be inside framebuffer begin/end
     */
    public static int readPixel(int x, int y) {
        int w = 1;
        int h = 1;

        int sizeBytes = w * h * 4;
        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(sizeBytes);

        Gdx.gl.glReadPixels(x, y, 1, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        byte r = pixelBuffer.get(0);
        byte g = pixelBuffer.get(1);
        byte b = pixelBuffer.get(2);
        byte a = pixelBuffer.get(3);

        if(r == -1 && g == -1 && b == -1) {
            return -1;
        }

//        String rStr = Integer.toHexString(r);
//        String gStr = Integer.toHexString(g);
//        String bStr = Integer.toHexString(b);
//        String aStr = Integer.toHexString(a);
//        System.out.println("R: " + rStr + " G: " + gStr + " B: " + bStr + " A: " + aStr);

        int color = ((a & 0xFF) << 24) |
                ((b & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((r & 0xFF) << 0);

        Color colorr = new Color();
        Color.rgba8888ToColor(colorr, color);
        return color;
    }
}
