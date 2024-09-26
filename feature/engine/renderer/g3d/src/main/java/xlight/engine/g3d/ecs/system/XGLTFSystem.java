package xlight.engine.g3d.ecs.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XGameWorldComponent;
import xlight.engine.ecs.component.XUIWorldComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XEntitySystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.XBatch3DOp;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XGLTFSystem extends XEntitySystem implements XBatch3D {

    private XSystemType systemType;
    private XCameraManager cameraManager;

    private SceneManager sceneManager;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private CascadeShadowMap csm;

    public XGLTFSystem(XSystemType systemType) {
        this.systemType = systemType;
    }

    @Override
    public void onAttachSystem(XWorld world, XSystemData systemData) {
        cameraManager = world.getManager(XCameraManager.class);

        setupSceneManager();
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        Class<?> renderComponentType = getRenderComponentType();
        return builder.all(XRender3DComponent.class, XTransformComponent.class, renderComponentType).build();
    }

    @Override
    protected boolean onBeginTick(XWorld world) {
        XCamera gameCamera = getRenderingCamera();
        if(gameCamera == null) {
            return true;
        }
        gameCamera.updateCamera();
        Camera gdxCamera = gameCamera.asGDXCamera();

        DirectionalShadowLight shadowLight = sceneManager.getFirstDirectionalShadowLight();
        if(shadowLight != null){
            if(csm != null){
                csm.setCascades(gdxCamera, shadowLight, 0, 6f);
            }
        }
        sceneManager.setCamera(gdxCamera);
        return false;
    }

    @Override
    protected void onEndTick(XWorld world) {
        sceneManager.update(world.getDeltaTime());
        sceneManager.render();
        sceneManager.getRenderableProviders().clear();
    }

    @Override
    public void onEntityTick(XEntity e) {
        if(!e.isVisible()) {
            return;
        }
        XRender3DComponent modelComponent = e.getComponent(XRender3DComponent.class);
        XTransformComponent transformComponent = e.getComponent(XTransformComponent.class);
        modelComponent.onUpdate(transformComponent.transform);
        modelComponent.onRender(0, this);
    }

    private XCamera getRenderingCamera() {
        if(systemType == XSystemType.RENDER) {
            return cameraManager.getRenderingGameCamera();
        }
        else if(systemType == XSystemType.UI) {
            return cameraManager.getRenderingUICamera();
        }
        return null;
    }

    private Class<?> getRenderComponentType() {
        if(systemType == XSystemType.RENDER) {
            return XGameWorldComponent.class;
        }
        else if(systemType == XSystemType.UI) {
            return XUIWorldComponent.class;
        }
        return null;
    }

    @Override
    public XSystemType getType() {
        return systemType;
    }

    @Override
    public void drawModel(RenderableProvider renderableProvider, XBatch3DOp op) {
        sceneManager.getRenderableProviders().add(renderableProvider);
    }

    @Override
    public void drawLight(BaseLight<?> ligt) {

    }

    private void setupSceneManager() {
        sceneManager = new SceneManager();

        // setup light
        light = new DirectionalShadowLight();
        light.direction.set(-1f, -2f, -1f).nor();
        light.color.set(Color.WHITE);
        light.intensity = 5.0f;
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(0.4f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        sceneManager.setCascadeShadowMap(csm = new CascadeShadowMap(3));
    }
}