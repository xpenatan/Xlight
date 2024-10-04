package xlight.engine.transform.ecs.component;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.transform.XTransform;

public class XTransformComponent implements XComponent, XUIDataListener, XDataMapListener {
    private static final int DATA_POSITION = 1;
    private static final int DATA_ROTATION = 2;
    private static final int DATA_SCALE = 3;
    private static final int DATA_SIZE = 4;
    private static final int DATA_OFFSET = 5;

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
    public void onSave(XDataMap map) {
        Vector3 position = transform.getPosition();
        map.put(DATA_POSITION, position);

        Vector3 rotation = transform.getRotation();
        map.put(DATA_ROTATION, rotation);

        Vector3 scale = transform.getScale();
        map.put(DATA_SCALE, scale);

        Vector3 size = transform.getSize();
        map.put(DATA_SIZE, size);

        Vector3 offset = transform.getOffset();
        map.put(DATA_OFFSET, offset);
    }

    @Override
    public void onLoad(XDataMap map) {
        Vector3 position = transform.getPosition();
        map.getVector3(DATA_POSITION, position);
        transform.setPosition(position);

        Vector3 rotation = transform.getRotation();
        map.getVector3(DATA_ROTATION, rotation);
        transform.setRotation(rotation);

        Vector3 scale = transform.getScale();
        map.getVector3(DATA_SCALE, scale);
        transform.setScale(scale);

        Vector3 size = transform.getSize();
        map.getVector3(DATA_SIZE, size);
        transform.setSize(size);

        Vector3 offset = transform.getOffset();
        map.getVector3(DATA_OFFSET, offset);
        transform.setOffset(offset);
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        XUIOpTransform op = XUIOpTransform.get();
        uiData.transform(transform, op);
    }
}
