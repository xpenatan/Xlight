package xlight.engine.core.ecs.system;

import xlight.engine.camera.XCamera;
import xlight.engine.core.ecs.component.XCameraComponent;
import xlight.engine.core.ecs.component.XModelComponent;
import xlight.engine.core.ecs.component.XGameComponent;
import xlight.engine.core.ecs.component.XTransformComponent;
import xlight.engine.core.ecs.component.XUIComponent;
import xlight.engine.core.ecs.service.camera.XCameraService;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.g3d.model.XModelRenderer;

public class XModelSystem extends XEntitySystem {

    XModelRenderer renderer;
    private XSystemType systemType;
    private XCameraService cameraService;

    public XModelSystem(XSystemType systemType) {
        this.systemType = systemType;
        renderer = new XModelRenderer();
    }

    @Override
    public void onAttachSystem(XECSWorld world) {
        cameraService = world.getService(XCameraService.class);
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        Class<?> renderComponentType = getRenderComponentType();
        return builder.all(XModelComponent.class, XTransformComponent.class, renderComponentType).build();
    }

    @Override
    protected void onBeginTick() {
        XCamera gameCamera = getRenderingCamera();
    }

    @Override
    public void onEntityTick(XComponentService cs, XEntity e) {
        XModelComponent modelComponent = cs.getComponent(e, XModelComponent.class);
        XTransformComponent transformComponent = cs.getComponent(e, XTransformComponent.class);


    }

    private XCamera getRenderingCamera() {
        if(systemType == XSystemType.GAME) {
            return cameraService.getRenderingGameCamera();
        }
        else if(systemType == XSystemType.UI) {
            return cameraService.getRenderingUICamera();
        }
        return null;
    }

    private Class<?> getRenderComponentType() {
        if(systemType == XSystemType.GAME) {
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