package xlight.engine.camera.ecs.component;

import xlight.engine.camera.XCamera;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.transform.XTransform;

public class XCameraComponent implements XComponent {

    public final XTransform localTransform;
    public final XCamera camera;

    public XCameraComponent() {
        camera = XCamera.newInstance();
        camera.setActiveCamera(true);
        localTransform = XTransform.newInstance();
    }
}