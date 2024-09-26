package xlight.engine.core.ecs.system;

import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XGameWorldComponent;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.transform.ecs.component.XTransformComponent;
import xlight.engine.ecs.component.XUIWorldComponent;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XEntitySystem;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.transform.XTransform;

public class XCameraSystem extends XEntitySystem {

    private XSystemType systemType;
    XCameraManager cameraManager;

    public XCameraSystem(XSystemType systemType) {
        this.systemType = systemType;
    }

    @Override
    public void onAttachSystem(XWorld world, XSystemData systemData) {
        cameraManager = world.getManager(XCameraManager.class);
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        Class<?> renderComponentType = getRenderComponentType();
        return builder.all(XCameraComponent.class, XTransformComponent.class, renderComponentType).build();
    }

    @Override
    public void onEntityTick(XEntity e) {
        XCameraComponent cameraComponent = e.getComponent(XCameraComponent.class);
        XTransformComponent transformComponent = e.getComponent(XTransformComponent.class);

        XCamera camera = cameraComponent.camera;
        XTransform transform = transformComponent.transform;
        XTransform localTransform = cameraComponent.localTransform;

        // TODO prevent calculating every frame
        camera.setUp(0, 1, 0);
        camera.setDirection(0, 0, -1);
        camera.setPosition(0, 0, 0);
        camera.transform(localTransform.getMatrix4());
        camera.transform(transform.getMatrix4());

        boolean activeCamera = camera.isActiveCamera();
        // Component active flag will replace the manager camera
        if(activeCamera) {
            if(systemType == XSystemType.RENDER) {
                XCamera activeGameCamera = cameraManager.getGameCamera();
                if(activeGameCamera != null) {
                    if(camera != activeGameCamera) {
                        cameraManager.setGameCamera(camera);
                    }
                }
                else {
                    cameraManager.setGameCamera(camera);
                }
            }
            else if(systemType == XSystemType.UI) {
                XCamera activeUICamera = cameraManager.getUICamera();
                if(activeUICamera != null) {
                    if(camera != activeUICamera) {
                        cameraManager.setUICamera(camera);
                    }
                }
                else {
                    cameraManager.setUICamera(camera);
                }
            }
        }
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
}