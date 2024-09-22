package xlight.engine.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import xlight.engine.g3d.XIDAttribute;

public class XPicker3DFrameBuffer extends XFrameBufferExt {
    static boolean DEBUG_TAKE_SCREENSHOT = false;

    private ModelBatch modelBatch;

    public XPicker3DFrameBuffer(int frameBufferWidth, int frameBufferHeight, Pixmap.Format format, boolean hasDepth) {
        super(frameBufferWidth, frameBufferHeight, format, hasDepth);
    }

    public XPicker3DFrameBuffer() {
    }

    @Override
    protected void init() {
        super.init();

        DepthShader.Config depthConfig = new DepthShader.Config();

        String depthV = Gdx.files.classpath("net/mgsx/gltf/shaders/depth.vs.glsl").readString();
        String depthF = FS;

        depthConfig.vertexShader = depthV;
        depthConfig.fragmentShader = depthF;
        depthConfig.numBones = 89;
        modelBatch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(final Renderable renderable) {
                return new DepthShader(renderable, depthConfig) {
                    int u_entityID;
                    int u_depth_debug;
                    public boolean debug = false;
                    private final Uniform entityID = new Uniform("u_entityID");
                    private final Uniform depthDebug = new Uniform("u_depth_debug");


                    @Override
                    public void init() {
                        u_entityID = register(entityID);
                        u_depth_debug = register(depthDebug);
                        super.init();
                    }

                    @Override
                    public void begin(final Camera camera, final RenderContext context) {
                        super.begin(camera, context);
                        // make model non transparent
                        context.setDepthTest(GL20.GL_LEQUAL);
                        context.setCullFace(GL20.GL_BACK);
                    }

                    @Override
                    public void render(Renderable renderable) {

                        XIDAttribute attr = renderable.material.get(XIDAttribute.class, XIDAttribute.SelectAttributeType);
                        if(attr != null) {
                            int id = attr.id;

                            float r = id & 0x000000FF;
                            float g = (id & 0x0000FF00) >>> 8;
                            float b = (id & 0x00FF0000) >>> 16;
                            set(u_entityID, r, g, b);
                            set(u_depth_debug, debug ? 1 : 0);
                        }
                        super.render(renderable);
                    }

                    @Override
                    public void end() {
                        super.end();
                    }
                };
            }
        }, new SceneRenderableSorter());
    }

    public void render(Camera camera, Array<RenderableProvider> modelInstances) {
        render(camera, modelBatch, modelInstances);
    }

    public void beginBatch(Camera camera) {
        begin();
        modelBatch.begin(camera);
    }

    public void render(RenderableProvider modelInstances) {
        if(modelInstances != null)
            modelBatch.render(modelInstances);
    }

    public void render(Array<? extends RenderableProvider> modelInstances) {
        if(modelInstances != null)
            modelBatch.render(modelInstances);
    }

    public void endBatch() {
        modelBatch.end();
        end();
    }

    public int getShaderRayPickingID(Camera camera, Array<RenderableProvider> modelInstances, int x, int y) {
        begin();
        render(camera, modelBatch, modelInstances);
        TextureDescriptor<Texture> depthMap = getColorBufferTexture();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int i = XPickerUtils.readPixel(x, y, width, height, depthMap.texture);

        if(DEBUG_TAKE_SCREENSHOT) {
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
//        System.out.println("ID: " + i);

        end();

        return i;
    }

    public int getShaderRayPickingID(Camera camera, RenderableProvider modelInstance, int x, int y) {
        begin();
        render(camera, modelBatch, modelInstance);

        TextureDescriptor<Texture> depthMap = getColorBufferTexture();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int i = XPickerUtils.readPixel(x, y, width, height, depthMap.texture);
        //Remove alpha from RGBA int
        i = (i & 0x000000FF);
        end();
        return i;
    }

    private static String VS = "" +
            "attribute vec3 a_position;\n" +
            "uniform mat4 u_projViewWorldTrans;\n" +
            "\n" +
            "\n" +
            "#ifdef position0Flag\n" +
            "attribute vec3 a_position0;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position1Flag\n" +
            "attribute vec3 a_position1;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position2Flag\n" +
            "attribute vec3 a_position2;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position3Flag\n" +
            "attribute vec3 a_position3;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position4Flag\n" +
            "attribute vec3 a_position4;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position5Flag\n" +
            "attribute vec3 a_position5;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position6Flag\n" +
            "attribute vec3 a_position6;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position7Flag\n" +
            "attribute vec3 a_position7;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position0Flag\n" +
            "#ifndef morphTargetsFlag\n" +
            "#define morphTargetsFlag\n" +
            "#endif\n" +
            "uniform vec4 u_morphTargets1;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef position4Flag\n" +
            "uniform vec4 u_morphTargets2;\n" +
            "#endif\n" +
            "\n" +
            "\n" +
            "#if defined(diffuseTextureFlag) && defined(blendedFlag)\n" +
            "#define blendedTextureFlag\n" +
            "attribute vec2 a_texCoord0;\n" +
            "varying vec2 v_texCoords0;\n" +
            "#endif\n" +
            "\n" +
            "\n" +
            "#ifdef boneWeight0Flag\n" +
            "#define boneWeightsFlag\n" +
            "attribute vec2 a_boneWeight0;\n" +
            "#endif //boneWeight0Flag\n" +
            "\n" +
            "#ifdef boneWeight1Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight1;\n" +
            "#endif //boneWeight1Flag\n" +
            "\n" +
            "#ifdef boneWeight2Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight2;\n" +
            "#endif //boneWeight2Flag\n" +
            "\n" +
            "#ifdef boneWeight3Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight3;\n" +
            "#endif //boneWeight3Flag\n" +
            "\n" +
            "#ifdef boneWeight4Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight4;\n" +
            "#endif //boneWeight4Flag\n" +
            "\n" +
            "#ifdef boneWeight5Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight5;\n" +
            "#endif //boneWeight5Flag\n" +
            "\n" +
            "#ifdef boneWeight6Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight6;\n" +
            "#endif //boneWeight6Flag\n" +
            "\n" +
            "#ifdef boneWeight7Flag\n" +
            "#ifndef boneWeightsFlag\n" +
            "#define boneWeightsFlag\n" +
            "#endif\n" +
            "attribute vec2 a_boneWeight7;\n" +
            "#endif //boneWeight7Flag\n" +
            "\n" +
            "#if defined(numBones) && defined(boneWeightsFlag)\n" +
            "#if (numBones > 0)\n" +
            "#define skinningFlag\n" +
            "#endif\n" +
            "#endif\n" +
            "\n" +
            "#if defined(numBones)\n" +
            "#if numBones > 0\n" +
            "uniform mat4 u_bones[numBones];\n" +
            "#endif //numBones\n" +
            "#endif\n" +
            "\n" +
            "#ifdef PackedDepthFlag\n" +
            "varying float v_depth;\n" +
            "#endif //PackedDepthFlag\n" +
            "\n" +
            "void main() {\n" +
            "    #ifdef blendedTextureFlag\n" +
            "        v_texCoords0 = a_texCoord0;\n" +
            "    #endif // blendedTextureFlag\n" +
            "\n" +
            "    #ifdef skinningFlag\n" +
            "        mat4 skinning = mat4(0.0);\n" +
            "        #ifdef boneWeight0Flag\n" +
            "            skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];\n" +
            "        #endif //boneWeight0Flag\n" +
            "        #ifdef boneWeight1Flag\n" +
            "            skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];\n" +
            "        #endif //boneWeight1Flag\n" +
            "        #ifdef boneWeight2Flag\n" +
            "            skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];\n" +
            "        #endif //boneWeight2Flag\n" +
            "        #ifdef boneWeight3Flag\n" +
            "            skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];\n" +
            "        #endif //boneWeight3Flag\n" +
            "        #ifdef boneWeight4Flag\n" +
            "            skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];\n" +
            "        #endif //boneWeight4Flag\n" +
            "        #ifdef boneWeight5Flag\n" +
            "            skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];\n" +
            "        #endif //boneWeight5Flag\n" +
            "        #ifdef boneWeight6Flag\n" +
            "            skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];\n" +
            "        #endif //boneWeight6Flag\n" +
            "        #ifdef boneWeight7Flag\n" +
            "            skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];\n" +
            "        #endif //boneWeight7Flag\n" +
            "    #endif //skinningFlag\n" +
            "\n" +
            "    #ifdef morphTargetsFlag\n" +
            "        vec3 morph_pos = a_position;\n" +
            "        #ifdef position0Flag\n" +
            "            morph_pos += a_position0 * u_morphTargets1.x;\n" +
            "        #endif\n" +
            "        #ifdef position1Flag\n" +
            "            morph_pos += a_position1 * u_morphTargets1.y;\n" +
            "        #endif\n" +
            "        #ifdef position2Flag\n" +
            "            morph_pos += a_position2 * u_morphTargets1.z;\n" +
            "        #endif\n" +
            "        #ifdef position3Flag\n" +
            "            morph_pos += a_position3 * u_morphTargets1.w;\n" +
            "        #endif\n" +
            "        #ifdef position4Flag\n" +
            "            tmorph_pos += a_position4 * u_morphTargets2.x;\n" +
            "        #endif\n" +
            "        #ifdef position5Flag\n" +
            "            morph_pos += a_position5 * u_morphTargets2.y;\n" +
            "        #endif\n" +
            "        #ifdef position6Flag\n" +
            "            morph_pos += a_position6 * u_morphTargets2.z;\n" +
            "        #endif\n" +
            "        #ifdef position7Flag\n" +
            "            morph_pos += a_position7 * u_morphTargets2.w;\n" +
            "        #endif\n" +
            "    #else\n" +
            "        vec3 morph_pos = a_position;\n" +
            "    #endif\n" +
            "\n" +
            "    #ifdef skinningFlag\n" +
            "        vec4 pos = u_projViewWorldTrans * skinning * vec4(morph_pos, 1.0);\n" +
            "    #else\n" +
            "        vec4 pos = u_projViewWorldTrans * vec4(morph_pos, 1.0);\n" +
            "    #endif\n" +
            "\n" +
            "    #ifdef PackedDepthFlag\n" +
            "        v_depth = pos.z / pos.w * 0.5 + 0.5;\n" +
            "    #endif //PackedDepthFlag\n" +
            "\n" +
            "    gl_Position = pos;\n" +
            "}\n";

    private static String FS = "" +
            "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "#define MED mediump\n" +
            "#define HIGH highp\n" +
            "precision mediump float;\n" +
            "#else\n" +
            "#define MED\n" +
            "#define LOWP\n" +
            "#define HIGH\n" +
            "#endif\n" +
            "\n" +
            "#if defined(diffuseTextureFlag) && defined(blendedFlag)\n" +
            "#define blendedTextureFlag\n" +
            "varying MED vec2 v_texCoords0;\n" +
            "uniform sampler2D u_diffuseTexture;\n" +
            "uniform float u_alphaTest;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef PackedDepthFlag\n" +
            "varying HIGH float v_depth;\n" +
            "#endif //PackedDepthFlag\n" +
            "\n" +
            "" +
            "uniform vec3 u_entityID;\n" +
            "uniform int u_depth_debug;" +
            "" +
            "void main() {\n" +
            "    HIGH float depth = gl_FragCoord.z;\n" +
            "    depth = clamp(depth, 0.0, 1.0);\n" +
            "    if(u_depth_debug == 1) {\n" +
            "        gl_FragColor = vec4(depth, depth, depth, depth);\n" +
            "    }\n" +
            "    else {\n" +
            "        gl_FragColor = vec4(u_entityID.x/255.0, u_entityID.y/255.0, u_entityID.z/255.0, depth);\n" +
            "    }" +
            "}\n";
}
