package xlight.engine.transform.ecs.component;

import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.transform.XTransform;

public class XTransformComponent implements XComponent, XUIDataListener {

    public final XTransform transform;

    public XTransformComponent() {
        transform = XTransform.newInstance();
    }

    public XTransformComponent position(float x, float y, float z) {
        transform.setPosition(x, y, z);
        return this;
    }

    public XTransformComponent rotate(float rx, float ry, float rz) {
        transform.setRotation(rx, ry, rz);
        return this;
    }

    public XTransformComponent scale(float sx, float sy, float sz) {
        transform.setScale(sx, sy, sz);
        return this;
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        XUIOpTransform op = XUIOpTransform.get();
        uiData.transform(transform, op);
    }
}
