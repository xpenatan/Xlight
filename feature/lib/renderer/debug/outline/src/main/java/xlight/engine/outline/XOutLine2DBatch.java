package xlight.engine.outline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import xlight.engine.g2d.XBatch2D;
import xlight.engine.g2d.XRender2D;
import xlight.engine.g2d.XSpriteBatch;
import xlight.engine.math.XMath;

public class XOutLine2DBatch {

    private XBatch2D spriteBatch;
    private ShaderProgram outlineShader;

    private boolean outlineShaderValid;

    public XOutLine2DBatch() {
//        spriteBatch.enableZ = true;
        this.spriteBatch = new XSpriteBatch() {

            @Override
            public void drawSprite(Texture texture, float[] spriteVertices, int offset, int count, float regionWidth, float regionHeight) {
            }

            @Override
            public void draw (Texture texture, float[] spriteVertices, int offset, int count) {
                outlineShader.setUniformf("u_texture_size_n", 1.0f / texture.getWidth(), 1.0f / texture.getHeight());
                outlineShader.setUniformf("u_selected_color", XMath.VEC3_1.set(0, 1, 0));
                outlineShader.setUniformf("u_width", 1.0f);
                outlineShader.setUniformi("u_pattern", 1);
                outlineShader.setUniformi("u_inside", 1);
                super.draw(texture, spriteVertices, offset, count);
            }
        };
        outlineShaderValid = false;
    }

    private void updateShader() {
        if(!outlineShaderValid) {
            outlineShaderValid = true;
            if(outlineShader != null)
                outlineShader.dispose();
            String prefix = "";

            outlineShader = new ShaderProgram(
                    vertexShader,
                    prefix + fragmentShader);
            if(!outlineShader.isCompiled())
                throw new GdxRuntimeException("Outline Shader failed: " + outlineShader.getLog());
        }
    }

    public void render(int engineType, Matrix4 cameraCombined, Array<XRender2D> objectsToRender) {
        //TODO shader not working with teavm
        if(objectsToRender.size <= 0) {
            return;
        }
        updateShader();
        spriteBatch.setProjectionMatrix(cameraCombined);
        spriteBatch.setShader(outlineShader);

        boolean pedantic = ShaderProgram.pedantic;
        ShaderProgram.pedantic = false;

//        XBaseSpriteBatch.Z_VALUE = 2;
//        boolean keyPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        spriteBatch.begin();
        for(int i = 0; i < objectsToRender.size; i++) {
            XRender2D renderer = objectsToRender.get(i);


//            if(!keyPressed)
                renderer.onRender(engineType, spriteBatch);
        }
        spriteBatch.end();
//        XBaseSpriteBatch.Z_VALUE = 0;
        ShaderProgram.pedantic = pedantic;
    }


    // port from https://godotshaders.com/shader/2d-outline-inline/

    String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "uniform mat4 u_projTrans;\n" //
            + "varying vec4 v_color;\n" //
            + "varying vec2 v_texCoords;\n" //
            + "\n" //
            + "void main()\n" //
            + "{\n" //
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
            + "   v_color.a = v_color.a * (255.0/254.0);\n" //
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
            + "}\n";
    String fragmentShader = "#ifdef GL_ES\n"
            + "#define LOWP lowp\n"
            + "precision mediump float;\n"
            + "#else\n"
            + "#define LOWP \n"
            + "#endif\n"
            + "varying LOWP vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "uniform sampler2D u_texture;\n"
            + "uniform vec2 u_texture_size_n;\n"
            + "uniform int u_pattern;\n"
            + "uniform float u_width;\n"
            + "uniform vec3 u_selected_color;\n"
            + "uniform bool u_inside;\n"

            + "bool hasContraryNeighbour(vec2 uv, vec2 texture_pixel_size, sampler2D texture) {" + "\n"
            + "     bool inside = u_inside != 0;" + "\n"
            + "     float width = u_width;" + "\n"
            + "     int pattern = u_pattern;" + "\n"
            + "     for (float i = -ceil(width); i <= ceil(width); i++) {" + "\n"
            + "         float x = abs(i) > width ? width * sign(i) : i;" + "\n"
            + "         float offset;" + "\n"
            + "         if (pattern == 0) {" + "\n"
            + "             offset = width - abs(x);" + "\n"
            + "         } else if (pattern == 1) {" + "\n"
            + "             offset = floor(sqrt(pow(width + 0.5, 2) - x * x));" + "\n"
            + "         } else if (pattern == 2) {" + "\n"
            + "             offset = width;" + "\n"
            + "         }" + "\n"
            + "         for (float j = -ceil(offset); j <= ceil(offset); j++) {" + "\n"
            + "             float y = abs(j) > offset ? offset * sign(j) : j;" + "\n"
            + "             vec2 xy = uv + texture_pixel_size * vec2(x, y);" + "\n"
            + "             if ((xy != clamp(xy, vec2(0.0), vec2(1.0)) || texture2D(texture, xy).a == 0.0) == inside) {" + "\n"
            + "                 return true;" + "\n"
            + "             }" + "\n"
            + "         }" + "\n"
            + "     }" + "\n"
            + "    return false; " + "\n"
            + "}" + "\n"
            + "" + "\n"
            + "void main()\n"
            + "{\n"
            + "  bool inside = u_inside != 0;" + "\n"
            + "  vec4 end_pixel = v_color * texture2D(u_texture, v_texCoords);"
            + "  if((end_pixel.a > 0.0) == inside && hasContraryNeighbour(v_texCoords, u_texture_size_n, u_texture)) { "
            + "     gl_FragColor = vec4(u_selected_color, 1.0);"
            + "  }"
            + "}";


//    String fragmentShader = "#ifdef GL_ES\n" //
//            + "#define LOWP lowp\n" //
//            + "precision mediump float;\n" //
//            + "#else\n" //
//            + "#define LOWP \n" //
//            + "#endif\n" //
//            + "varying LOWP vec4 v_color;\n" //
//            + "varying vec2 v_texCoords;\n" //
//            + "uniform sampler2D u_texture;\n" //
//            + "void main()\n"//
//            + "{\n" //
//            + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
//            + "}";


//    public void render(Camera camera, Texture colorBufferTexture) {
//        this.camera = camera;
//        updateShader();
//
//        outlineShader.bind();
//
//        //TODO
//        outlineShader.setUniformf("u_strokeColor", XpeMath.VEC3_1.set(0, 0, 1f));
//
//        spriteBatch.enableBlending();
//        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
//        spriteBatch.setShader(outlineShader);
//        spriteBatch.begin();
//        spriteBatch.draw(colorBufferTexture, 0, 0, 1, 1, 0f, 0f, 1f, 1f);
//        spriteBatch.end();
//        spriteBatch.setShader(null);
//        this.camera = null;
//    }


    private static String vertex = "" +
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
    private static String fragment = "" +
            "#ifdef GL_ES\n" +
            "    precision mediump float;\n" +
            "    precision mediump int;\n" +
            "#endif\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoord;\n" +
            "\n" +
            "\n" +
            "uniform vec2 u_textureSize;\n" +
            "uniform vec2 u_minUV;\n" +
            "uniform vec2 u_maxUV;\n" +
            "uniform vec3 u_strokeColor;\n" +
            "\n" +
            "uniform vec2 u_topBottomV;\n" +
            "uniform vec2 u_leftRightU;\n" +
            "\n" +
            "vec2 getPixelUVPosition(int x, int y) {\n" +
            "    vec2 onePixel = vec2(1.0, 1.0) / u_textureSize;\n" +
            "    return onePixel * vec2(x, y);\n" +
            "}\n" +
            "\n" +
            "bool isWhite(int x, int y) {\n" +
            "    vec2 onePixel = vec2(1.0, 1.0) / u_textureSize;\n" +
            "    vec4 pixel = texture2D(u_texture, v_texCoord + onePixel * vec2(x, y));\n" +
            "    return pixel.r == 1.0;\n" +
            "}\n" +
            "\n" +
            "bool outline(int width) {\n" +
            "\n" +
            "//    for(int i = 0; i < width; i++) {\n" +
            "//        bool topWhite = isWhite(0, width);\n" +
            "//        bool bottomWhite = isWhite(0, -width);\n" +
            "//        bool leftWhite = isWhite(-width, 0);\n" +
            "//        bool rightWhite = isWhite(width, 0);\n" +
            "//\n" +
            "//        if(topWhite || bottomWhite || leftWhite || rightWhite)\n" +
            "//            return true;\n" +
            "//    }\n" +
            "    return false;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    float topV = u_topBottomV.x;\n" +
            "    float bottomV = u_topBottomV.y;\n" +
            "\n" +
            "    float leftU = u_leftRightU.x;\n" +
            "    float rightU = u_leftRightU.y;\n" +
            "\n" +
            "    vec4 strokeColor = vec4(0, 0, 1, 1);\n" +
            "\n" +
            "    if(v_texCoord.y >= bottomV) {\n" +
            "        gl_FragColor = strokeColor;\n" +
            "    }\n" +
            "    else if(v_texCoord.y <= topV) {\n" +
            "        gl_FragColor = strokeColor;\n" +
            "    }\n" +
            "    else if(v_texCoord.x >= rightU) {\n" +
            "        gl_FragColor = strokeColor;\n" +
            "    }\n" +
            "    else if(v_texCoord.x <= leftU) {\n" +
            "        gl_FragColor = strokeColor;\n" +
            "    }\n" +
            "    else {\n" +
            "        gl_FragColor = vec4(0,0,0,0);\n" +
            "    }\n" +
            "\n" +
            "//    gl_FragColor = v_color * texture2D(u_texture, v_texCoord);\n" +
            "}";
}