package xlight.engine.core.ecs.component;

import xlight.engine.ecs.component.XComponent;
import xpeengine.engine.core.transform.XTransform;

public class XTransformComponent implements XComponent {

    public final XTransform transform;

    public XTransformComponent() {
        transform = XTransform.newInstance();
    }
}
