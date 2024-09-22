package xlight.engine.g3d.ecs.system;

import com.badlogic.gdx.graphics.Camera;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.ecs.component.XUIComponent;
import xlight.engine.ecs.system.XEntitySystem;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.g3d.model.XModelRenderer;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XRender3DSystem extends XEntitySystem {

    XModelRenderer renderer;
    private XSystemType systemType;
    private XCameraManager cameraManager;

    public XRender3DSystem(XSystemType systemType) {
        this.systemType = systemType;
        renderer = new XModelRenderer();
    }

    @Override
    public void onAttachSystem(XWorld world, XSystemData systemData) {
        cameraManager = world.getManager(XCameraManager.class);
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
        renderer.update(gdxCamera);
        return false;
    }
    @Override
    protected void onEndTick(XWorld world) {
        renderer.render();
        renderer.renderShadows();
        renderer.clear();
    }

    @Override
    public void onEntityTick(XEntity e) {
        XRender3DComponent modelComponent = e.getComponent(XRender3DComponent.class);
        XTransformComponent transformComponent = e.getComponent(XTransformComponent.class);
        modelComponent.onUpdate(transformComponent.transform);
        modelComponent.onRender(0, renderer);
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
            return XGameComponent.class;
        }
        else if(systemType == XSystemType.UI) {
            return XUIComponent.class;
        }
        return null;
    }

    @Override
    public XSystemType getType() {
        return systemType;
    }
}