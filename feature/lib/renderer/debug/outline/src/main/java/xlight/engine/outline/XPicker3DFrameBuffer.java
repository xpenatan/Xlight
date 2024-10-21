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
        depthConfig.defaultCullFace = GL20.GL_BACK;
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
//                        context.setDepthTest(GL20.GL_LEQUAL);
//                        context.setCullFace(GL20.GL_BACK);
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
