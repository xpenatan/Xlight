package xlight.engine.camera.ecs.component;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.engine.camera.XCamera;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.transform.XTransform;

public class XCameraComponent implements XComponent {

    public final XTransform localTransform;
    public final XCamera camera;

    public XCameraComponent() {
        localTransform = XTransform.newInstance();
        camera = XCamera.newInstance();
        camera.setActiveCamera(true);
        ScreenViewport screenViewport = new ScreenViewport();
        camera.setViewport(screenViewport);
    }
}