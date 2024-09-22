package xlight.engine.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.XBaseSpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import xlight.engine.g2d.XRender2D;
import xlight.engine.math.XMath;

public class XPicker2DFrameBuffer {

    private ShaderProgram shaderProgram;

    XBaseSpriteBatch batch;

    XFrameBufferExt frameBuffer;

    public XPicker2DFrameBuffer() {
        String vertexShader = VS;
        String fragmentShader = FS;
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if(!shaderProgram.isCompiled())
            throw new GdxRuntimeException("Shader failed: " + shaderProgram.getLog());

        batch = new XBaseSpriteBatch();
        batch.enableZ = true;
        frameBuffer = new XFrameBufferExt(Pixmap.Format.RGB888, false);
    }

    public void render(Matrix4 combined, Array<? extends XRender2D> objectsToRender) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.setProjectionMatrix(combined);

        boolean pedantic = ShaderProgram.pedantic;
        ShaderProgram.pedantic = false;

        for(int i = 0; i < objectsToRender.size; i++) {
            XRender2D renderer = objectsToRender.get(i);
            int renderID = renderer.getRenderId();
            float r = renderID & 0x000000FF;
            float g = (renderID & 0x0000FF00) >>> 8;
            float b = (renderID & 0x00FF0000) >>> 16;
            XMath.VEC3_1.set(r, g, b);
            batch.setShader(shaderProgram);
            batch.begin();
            shaderProgram.setUniformf("u_entityID", XMath.VEC3_1);
            renderer.onRender(2, batch);
            batch.end();
        }
        ShaderProgram.pedantic = pedantic;
    }

    public void renderToFramebuffer(Matrix4 combined, Array<? extends XRender2D> objectsToRender) {
        frameBuffer.begin();
        render(combined, objectsToRender);
        frameBuffer.end();
    }

    public int getShaderRayPickingID(Matrix4 combined, Array<? extends XRender2D> objectsToRender, int screenX, int screenY) {
        frameBuffer.begin();
        render(combined, objectsToRender);
        TextureDescriptor<Texture> depthMap = getColorBufferTexture();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int entityID = XPickerUtils.readPixel(screenX, screenY, width, height, depthMap.texture);

        if(XPicker3DFrameBuffer.DEBUG_TAKE_SCREENSHOT) {
            int frameBufferWidth = frameBuffer.getFrameBufferWidth();
            int frameBufferHeight = frameBuffer.getFrameBufferHeight();
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, frameBufferWidth, frameBufferHeight, true);

            // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            for(int ii = 4; ii <= pixels.length; ii += 4) {
                pixels[ii - 1] = (byte)255;
            }

            // Take Screenshot
            Pixmap pixmap = new Pixmap(frameBufferWidth, frameBufferHeight, Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            PixmapIO.writePNG(Gdx.files.absolute("E:\\mypixmap.png"), pixmap);
            pixmap.dispose();
        }

        frameBuffer.end();
        return entityID;
    }

    public TextureDescriptor<Texture> getColorBufferTexture() {
        return frameBuffer.getColorBufferTexture();
    }

    private static String VS = "" +
            "uniform mat4 u_projTrans;\n" +
            "\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "attribute vec4 a_color;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "    v_texCoord = a_texCoord0;\n" +
            "    v_color = a_color;\n" +
            "}";

    private static String FS = "" +
            "#ifdef GL_ES\n" +
            "    #define LOWP lowp\n" +
            "    precision mediump float;\n" +
            "#else\n" +
            "    #define LOWP\n" +
            "#endif\n" +
            "\n" +
            "uniform vec3 u_entityID;\n" +
            "varying LOWP vec4 v_color;\n" +
            "varying vec2 v_texCoord;\n" +
            "uniform sampler2D u_texture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 testColor = v_color * texture2D(u_texture, v_texCoord);\n" +
            "    if(testColor.a == 0.0) {\n" +
            "        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
            "    }\n" +
            "    else {\n" +
            "        gl_FragColor = vec4(u_entityID.x/255.0, u_entityID.y/255.0, u_entityID.z/255.0, 1.0);\n" +
            "    }\n" +
            "}";
}