package xlight.engine.core.ecs.system;

import xlight.engine.camera.XCamera;
import xlight.engine.core.ecs.component.XCameraComponent;
import xlight.engine.core.ecs.component.XRenderComponent;
import xlight.engine.core.ecs.component.XTransformComponent;
import xlight.engine.core.ecs.component.XUIComponent;
import xlight.engine.core.ecs.service.camera.XCameraService;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XSystemType;
import xpeengine.engine.core.transform.XTransform;

public class XCameraSystem extends XEntitySystem {

    private XSystemType systemType;

    public XCameraSystem(XSystemType systemType) {
        this.systemType = systemType;
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        Class<?> renderComponentType = getRenderComponentType();
        return builder.all(XCameraComponent.class, XTransformComponent.class, renderComponentType).build();
    }

    @Override
    public void onEntityTick(XComponentService cs, XEntity e) {
        XCameraComponent cameraComponent = cs.getComponent(e, XCameraComponent.class);
        XTransformComponent transformComponent = cs.getComponent(e, XTransformComponent.class);

        XCamera camera = cameraComponent.camera;
        XTransform transform = transformComponent.transform;
    }

    private Class<?> getRenderComponentType() {
        if(systemType == XSystemType.RENDERER) {
            return XRenderComponent.class;
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