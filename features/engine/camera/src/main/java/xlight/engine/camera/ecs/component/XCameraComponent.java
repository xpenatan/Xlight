package xlight.engine.camera.ecs.component;

import xlight.engine.camera.XCamera;
import xlight.engine.ecs.component.XComponent;

public class XCameraComponent implements XComponent {

    public final XCamera camera;

    public XCameraComponent() {
        camera = XCamera.newInstance();
    }
}