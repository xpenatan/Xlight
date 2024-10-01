package xlight.engine.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.XBatch3DOp;
import xlight.engine.g3d.XIDAttribute;
import xlight.engine.g3d.XRender3D;

public class XOutLine3DBatch {

    private SpriteBatch spriteBatch;
    private ShaderProgram outlineShader;

    private boolean outlineShaderValid;

    private Color outlineInnerColor = Color.BLUE;
    private Color outlineOuterColor = Color.BLUE;

    private float outlinesWidth = .23f;
    private float outlineDepthMin = 0.38f;
    private float outlineDepthMax = 0.42f;

    private boolean outlineDistFalloffOption = true;
    private float outlineDistFalloff = 1f;

    private XFrameBufferExt frameBufferExt;
    private ModelBatch modelBatch;
    private XBatch3D batch3D;

    public XOutLine3DBatch() {
        spriteBatch = new SpriteBatch();
        outlineShaderValid = false;

        frameBufferExt = new XFrameBufferExt();

        DepthShader.Config depthConfig = new DepthShader.Config();

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

        batch3D = new XBatch3D() {
            @Override
            public void drawModel(RenderableProvider renderableProvider, XBatch3DOp op) {
                if(op.environment) {
                    modelBatch.render(renderableProvider);
                }
            }

            @Override
            public void drawLight(BaseLight<?> light) {

            }
        };
    }

    private void updateShader() {
        if(!outlineShaderValid) {
            outlineShaderValid = true;
            if(outlineShader != null)
                outlineShader.dispose();
            String prefix = "";
//            if(ui.outlineDistFalloffOption.isOn()){
                prefix += "#define DISTANCE_FALLOFF\n";
//            }

            outlineShader = new ShaderProgram(
                    defaultVertexShader,
                    prefix + defaultFragmentShader);
            if(!outlineShader.isCompiled())
                throw new GdxRuntimeException("Outline Shader failed: " + outlineShader.getLog());
        }
    }

    public void render(float cameraFar, float cameraNear, Texture colorBufferTexture) {
        updateShader();

        outlineShader.bind();
        float size = 1 - outlinesWidth;

        // float depthMin = ui.outlineDepthMin.getValue() * .001f;
        float depthMin = (float)Math.pow(outlineDepthMin, 10); // 0.35f
        float depthMax = (float)Math.pow(outlineDepthMax, 10); // 0.9f

        // TODO use an integer instead and divide w and h
        outlineShader.setUniformf("u_size", Gdx.graphics.getWidth() * size, Gdx.graphics.getHeight() * size);
        outlineShader.setUniformf("u_depth_min", depthMin);
        outlineShader.setUniformf("u_depth_max", depthMax);
        outlineShader.setUniformf("u_inner_color", outlineInnerColor);
        outlineShader.setUniformf("u_outer_color", outlineOuterColor);

        if(outlineDistFalloffOption) {
            float distanceFalloff = outlineDistFalloff;
            if(distanceFalloff <= 0) {
                distanceFalloff = .001f;
            }
            outlineShader.setUniformf("u_depthRange", cameraFar/ (cameraNear * distanceFalloff));
        }

        spriteBatch.enableBlending();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
        spriteBatch.setShader(outlineShader);
        spriteBatch.begin();
        spriteBatch.draw(colorBufferTexture, 0, 0, 1, 1, 0f, 0f, 1f, 1f);
        spriteBatch.end();
        spriteBatch.setShader(null);
    }

    public void render(int engineType, Camera camera, Array<XRender3D> objectsToRender) {
        if(objectsToRender.size <= 0) {
            return;
        }

        boolean keyPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);


        if(keyPressed && objectsToRender.size > 1) {
            // This feature is tied to XEntitySelectionManagerImpl when there is multiple selections and want to move only a single entity

            outlineInnerColor = Color.YELLOW;
            outlineOuterColor = Color.YELLOW;

            frameBufferExt.begin();
            modelBatch.begin(camera);
            objectsToRender.get(0).onRender(engineType, batch3D);
            modelBatch.end();
            frameBufferExt.end();

            render(camera.far, camera.near, frameBufferExt.getColorBufferTexture().texture);

            outlineInnerColor = Color.BLUE;
            outlineOuterColor = Color.BLUE;

            frameBufferExt.begin();
            modelBatch.begin(camera);
            for(int i = 1; i < objectsToRender.size; i++) {
                XRender3D render3D = objectsToRender.get(i);
                render3D.onRender(engineType, batch3D);
            }
            modelBatch.end();
            frameBufferExt.end();

            render(camera.far, camera.near, frameBufferExt.getColorBufferTexture().texture);
        }
        else {
            frameBufferExt.begin();
            modelBatch.begin(camera);
            for(int i = 0; i < objectsToRender.size; i++) {
                XRender3D render3D = objectsToRender.get(i);
                render3D.onRender(engineType, batch3D);
            }
            modelBatch.end();
            frameBufferExt.end();
            render(camera.far, camera.near, frameBufferExt.getColorBufferTexture().texture);
        }
    }

    private static String defaultVertexShader = "" +
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
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "\n" +
            "uniform mat4 u_projTrans;\n" +
            "uniform vec2 u_size;\n" +
            "\n" +
            "varying MED vec2 v_texCoords0;\n" +
            "varying MED vec2 v_texCoords1;\n" +
            "varying MED vec2 v_texCoords2;\n" +
            "varying MED vec2 v_texCoords3;\n" +
            "varying MED vec2 v_texCoords4;\n" +
            "\n" +
            "void main(){\n" +
            "    v_texCoords0 = a_texCoord0 + vec2(0.0, -1.0 / u_size.y);\n" +
            "    v_texCoords1 = a_texCoord0 + vec2(-1.0 / u_size.x, 0.0);\n" +
            "    v_texCoords2 = a_texCoord0;\n" +
            "    v_texCoords3 = a_texCoord0 + vec2(1.0 / u_size.x, 0.0);\n" +
            "    v_texCoords4 = a_texCoord0 + vec2(0.0, 1.0 / u_size.y);\n" +
            "\n" +
            "\tgl_Position = u_projTrans * a_position;\n" +
            "}";

    private static String defaultFragmentShader = "" +
            "#ifdef GL_ES\n" +
            "#define LOWP lowp\n" +
            "#define MED mediump\n" +
            "precision lowp float;\n" +
            "#else\n" +
            "#define LOWP\n" +
            "#define MED\n" +
            "#endif\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "\n" +
            "varying MED vec2 v_texCoords0;\n" +
            "varying MED vec2 v_texCoords1;\n" +
            "varying MED vec2 v_texCoords2;\n" +
            "varying MED vec2 v_texCoords3;\n" +
            "varying MED vec2 v_texCoords4;\n" +
            "\n" +
            "uniform float u_depth_min;\n" +
            "uniform float u_depth_max;\n" +
            "\n" +
            "uniform vec4 u_outer_color;\n" +
            "uniform vec4 u_inner_color;\n" +
            "\n" +
            "#ifdef DISTANCE_FALLOFF\n" +
            "uniform float u_depthRange;\n" +
            "#endif\n" +
            "\n" +
            "void main() {\n" +
            "\tconst vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);\n" +
            "\n" +
            "\n" +
            "\tfloat depth = abs(\n" +
            "\t\tdot(texture2D(u_texture, v_texCoords0), bitShifts) +\n" +
            "\t\tdot(texture2D(u_texture, v_texCoords1), bitShifts) -\n" +
            "\t\tdot(4.0 * texture2D(u_texture, v_texCoords2), bitShifts) +\n" +
            "\t\tdot(texture2D(u_texture, v_texCoords3), bitShifts) +\n" +
            "\t\tdot(texture2D(u_texture, v_texCoords4), bitShifts)\n" +
            "\t);\n" +
            "\n" +
            "\tif (depth > u_depth_min){\n" +
            "\t\tif(depth < u_depth_max){\n" +
            "\t\t\tgl_FragColor = u_inner_color;\n" +
            "\t\t}\n" +
            "\t\telse{\n" +
            "\t\t\tgl_FragColor = u_outer_color;\n" +
            "\t\t}\n" +
            "#ifdef DISTANCE_FALLOFF\n" +
            "\t\tfloat centerDepth = dot(texture2D(u_texture, v_texCoords2), bitShifts);\n" +
            "\t\tgl_FragColor.a *= 1.0 - pow(centerDepth, u_depthRange);\n" +
            "#endif\n" +
            "\t}\n" +
            "\telse{\n" +
            "\t\tgl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);\n" +
            "\t}\n" +
            "\n" +
            "}\n";


    private static String depthV = "" +
            "#ifdef GL_ES\n" +
            "    #define LOWP lowp\n" +
            "    #define MED mediump\n" +
            "    #define HIGH highp\n" +
            "    precision mediump float;\n" +
            "#else\n" +
            "    #define MED\n" +
            "    #define LOWP\n" +
            "    #define HIGH\n" +
            "#endif\n" +
            "\n" +
            "\n" +
            "#ifdef boneWeight0Flag\n" +
            "    #define boneWeightsFlag\n" +
            "    attribute vec2 a_boneWeight0;\n" +
            "#endif //boneWeight0Flag\n" +
            "\n" +
            "#ifdef boneWeight1Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight1;\n" +
            "#endif //boneWeight1Flag\n" +
            "\n" +
            "#ifdef boneWeight2Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight2;\n" +
            "#endif //boneWeight2Flag\n" +
            "\n" +
            "#ifdef boneWeight3Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight3;\n" +
            "#endif //boneWeight3Flag\n" +
            "\n" +
            "#ifdef boneWeight4Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight4;\n" +
            "#endif //boneWeight4Flag\n" +
            "\n" +
            "#ifdef boneWeight5Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight5;\n" +
            "#endif //boneWeight5Flag\n" +
            "\n" +
            "#ifdef boneWeight6Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight6;\n" +
            "#endif //boneWeight6Flag\n" +
            "\n" +
            "#ifdef boneWeight7Flag\n" +
            "    #ifndef boneWeightsFlag\n" +
            "        #define boneWeightsFlag\n" +
            "    #endif\n" +
            "    attribute vec2 a_boneWeight7;\n" +
            "#endif //boneWeight7Flag\n" +
            "\n" +
            "#if defined(numBones) && defined(boneWeightsFlag)\n" +
            "    #if (numBones > 0)\n" +
            "        #define skinningFlag\n" +
            "    #endif\n" +
            "#endif\n" +
            "\n" +
            "#if defined(numBones)\n" +
            "    #if numBones > 0\n" +
            "        uniform mat4 u_bones[numBones];\n" +
            "    #endif //numBones\n" +
            "#endif\n" +
            "\n" +
            "\n" +
            "attribute vec3 a_position;\n" +
            "\n" +
            "uniform mat4 u_projViewTrans;\n" +
            "uniform mat4 u_worldTrans;\n" +
            "\n" +
            "varying vec4 v_position;\n" +
            "\n" +
            "varying float v_depth;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    #ifdef skinningFlag\n" +
            "\t\tmat4 skinning = mat4(0.0);\n" +
            "\t\t#ifdef boneWeight0Flag\n" +
            "\t\t\tskinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];\n" +
            "\t\t#endif //boneWeight0Flag\n" +
            "\t\t#ifdef boneWeight1Flag\n" +
            "\t\t\tskinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];\n" +
            "\t\t#endif //boneWeight1Flag\n" +
            "\t\t#ifdef boneWeight2Flag\n" +
            "\t\t\tskinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];\n" +
            "\t\t#endif //boneWeight2Flag\n" +
            "\t\t#ifdef boneWeight3Flag\n" +
            "\t\t\tskinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];\n" +
            "\t\t#endif //boneWeight3Flag\n" +
            "\t\t#ifdef boneWeight4Flag\n" +
            "\t\t\tskinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];\n" +
            "\t\t#endif //boneWeight4Flag\n" +
            "\t\t#ifdef boneWeight5Flag\n" +
            "\t\t\tskinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];\n" +
            "\t\t#endif //boneWeight5Flag\n" +
            "\t\t#ifdef boneWeight6Flag\n" +
            "\t\t\tskinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];\n" +
            "\t\t#endif //boneWeight6Flag\n" +
            "\t\t#ifdef boneWeight7Flag\n" +
            "\t\t\tskinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];\n" +
            "\t\t#endif //boneWeight7Flag\n" +
            "\t#endif //skinningFlag\n" +
            "\n" +
            "    vec3 morph_pos = a_position;\n" +
            "\n" +
            "    #ifdef skinningFlag\n" +
            "\t\tvec4 pos = u_worldTrans * skinning * vec4(morph_pos, 1.0);\n" +
            "\t#else\n" +
            "\t\tvec4 pos = u_worldTrans * vec4(morph_pos, 1.0);\n" +
            "\t#endif\n" +
            "\n" +
            "    gl_Position = u_projViewTrans * pos;\n" +
            "}\n";

    private static String depthF = "" +
            "#ifdef GL_ES\n" +
            "    #define LOWP lowp\n" +
            "    #define MED mediump\n" +
            "    #define HIGH highp\n" +
            "    precision mediump float;\n" +
            "#else\n" +
            "    #define MED\n" +
            "    #define LOWP\n" +
            "    #define HIGH\n" +
            "#endif\n" +
            "\n" +
            "uniform vec3 u_entityID;\n" +
            "uniform int u_depth_debug;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    HIGH float depth = gl_FragCoord.z;\n" +
            "\n" +
            "    depth = clamp(depth, 0.0, 1.0);\n" +
            "//    gl_FragColor = vec4(vec3(depth, depth, u_entityID), 1.0);\n" +
            "    // Cannot use alpha because it mess up rgb\n" +
            "    if(u_depth_debug == 1) {\n" +
            "        gl_FragColor = vec4(depth, depth, depth, depth);\n" +
            "    }\n" +
            "    else {\n" +
            "        gl_FragColor = vec4(u_entityID.x/255.0, u_entityID.y/255.0, u_entityID.z/255.0, depth);\n" +
            "    }\n" +
            "}\n";
}
