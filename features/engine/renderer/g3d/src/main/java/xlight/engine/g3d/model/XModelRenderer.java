package xlight.engine.g3d.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentCache;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import xlight.engine.camera.XCameraUtils;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.XBatch3DOp;
import xlight.engine.debug.XMultiShapeRenderer;

public class XModelRenderer implements XBatch3D {

    private ModelBatch batch;
    private ModelBatch depthBatch;
    private SpriteBatch spriteBatch;

    private final Array<RenderableProvider> drawables;
    private final Array<RenderableProvider> drawablesNoEnvironment;
    private final Environment environment;
    private final EnvironmentCache computedEnvironement;
    private final RenderableSorter renderableSorter;

    Cubemap environmentCubemap;
    Cubemap diffuseCubemap;
    Cubemap specularCubemap;
    private CascadeShadowMap cascadeShadowMap;

    private Camera camera;

    public XModelRenderer() {
        drawables = new Array<>();
        drawablesNoEnvironment = new Array<>();
        environment = new Environment();
        computedEnvironement = new EnvironmentCache();
        cascadeShadowMap = new CascadeShadowMap(4);

        int maxBones = 80;
        renderableSorter = new SceneRenderableSorter();
        ShaderProvider shaderProvider = PBRShaderProvider.createDefault(maxBones);
        DepthShaderProvider depthShaderProvider = PBRShaderProvider.createDefaultDepth(maxBones);

        batch = new ModelBatch(shaderProvider, renderableSorter);
        depthBatch = new ModelBatch(depthShaderProvider);
//        frameBufferExt = new XFrameBufferExt();
        spriteBatch = new SpriteBatch();

        float lum = 1f;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, lum, lum, lum, 1));

        DirectionalLightEx defaultLight = new DirectionalLightEx();
        defaultLight.color.set(Color.WHITE);
        defaultLight.intensity = 100f;
        defaultLight.direction.set(0.3f, -1.0f, -0.0f);
        defaultLight.updateColor();

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(defaultLight);

        iblBuilder.lights.get(0).direction.set(defaultLight.direction);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
    }

    public void setShaderProvider(ShaderProvider shaderProvider) {
        batch.dispose();
        batch = new ModelBatch(shaderProvider, renderableSorter);
    }

    public void setDepthShaderProvider(DepthShaderProvider depthShaderProvider) {
        depthBatch.dispose();
        depthBatch = new ModelBatch(depthShaderProvider);
    }

    public void update(Camera camera) {
        this.camera = camera;
        updateSkyboxRotation();
    }

    public void render() {
        renderColor(camera);
    }

    private void renderColor(Camera camera) {
        batch.begin(camera);
        batch.render(drawables, environment);
        batch.render(drawablesNoEnvironment);
        batch.end();
    }

    public void renderShadows() {
        DirectionalShadowLight shadowLight = getFirstDirectionalShadowLight();
        float depth = 10;
        if(shadowLight != null) {
            cascadeShadowMap.setCascades(camera, shadowLight, depth, 2f);
        }

        if(shadowLight != null){
//            shadowLight.begin();
//            renderDepth(shadowLight.getCamera());
//            shadowLight.end();

            environment.shadowMap = shadowLight;
        }else{
            environment.shadowMap = null;
        }

        if(shadowLight != null){
            for(DirectionalShadowLight light : cascadeShadowMap.lights){
                light.begin();
                renderDepth(light.getCamera());
                light.end();
            }
            environment.set(cascadeShadowMap.attribute);
        }


//        if(shadowLight != null) {
//            spriteBatch.begin();
//            Texture texture = (Texture)shadowLight.getDepthMap().texture;
//            spriteBatch.draw(texture, 0, 0, 256, 256, 0, 0, texture.getWidth(), texture.getHeight(), false, true);
//            spriteBatch.end();
//        }
    }

    public void renderCSMCameras(XMultiShapeRenderer shapeRenderer) {
        for(DirectionalShadowLight light : cascadeShadowMap.lights){
            Frustum frustum = light.getCamera().frustum;
            XCameraUtils.renderFrustum(shapeRenderer.getLineRenderer(), frustum, true);
        }
    }

    private void renderDepth(Camera camera) {
        depthBatch.begin(camera);
        depthBatch.render(drawables);
        depthBatch.end();
    }

    private void renderDepthToTexture(Camera camera){

    }

    /**
     * Automatically set skybox rotation matching this environement rotation.
     * Subclasses could override this method in order to change this behavior.
     */
    protected void updateSkyboxRotation(){
//        if(skyBox != null){
//            PBRMatrixAttribute rotationAttribute = environment.get(PBRMatrixAttribute.class, PBRMatrixAttribute.EnvRotation);
//            if(rotationAttribute != null){
//                skyBox.setRotation(rotationAttribute.matrix);
//            }
//        }
    }

    public void setAmbientLight(float lum) {
        environment.get(ColorAttribute.class, ColorAttribute.AmbientLight).color.set(lum, lum, lum, 1);
    }

    public int getActiveLightsCount(){
        return EnvironmentUtil.getLightCount(computedEnvironement);
    }

    public int getTotalLightsCount(){
        return EnvironmentUtil.getLightCount(environment);
    }

    public PointLightsAttribute getPointLightsAttribute() {
        return environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
    }

    public SpotLightsAttribute getSpotLightsAttribute() {
        return environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
    }

    public DirectionalLightsAttribute getDirectionLightsAttribute() {
        return environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
    }

    public DirectionalShadowLight getFirstDirectionalShadowLight(){
        DirectionalLightsAttribute dla = getDirectionLightsAttribute();
        if(dla != null){
            for(DirectionalLight dl : dla.lights){
                if(dl instanceof DirectionalShadowLight){
                    return (DirectionalShadowLight)dl;
                }
            }
        }
        return null;
    }

    public void clear() {
        drawables.clear();
        drawablesNoEnvironment.clear();
        DirectionalLightsAttribute directionLightsAttribute = getDirectionLightsAttribute();
        if(directionLightsAttribute != null) {
            directionLightsAttribute.lights.clear();
        }
        SpotLightsAttribute spotLightsAttribute = getSpotLightsAttribute();
        if(spotLightsAttribute != null) {
            spotLightsAttribute.lights.clear();
        }
        PointLightsAttribute pointLightsAttribute = getPointLightsAttribute();
        if(pointLightsAttribute != null) {
            pointLightsAttribute.lights.clear();
        }
    }

    public void dispose() {
        batch.dispose();
        depthBatch.dispose();
    }

    @Override
    public void drawModel(RenderableProvider renderableProvider, XBatch3DOp options) {
        if(options.environment) {
            drawables.add(renderableProvider);
        }
        else {
            drawablesNoEnvironment.add(renderableProvider);
        }
    }

    @Override
    public void drawLight(BaseLight<?> light) {
        environment.add(light);
    }
}
