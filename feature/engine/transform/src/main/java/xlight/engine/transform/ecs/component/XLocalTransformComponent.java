package xlight.engine.transform.ecs.component;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.pool.XPoolable;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformListener;

public class XLocalTransformComponent implements XComponent, XUIDataListener, XDataMapListener, XPoolable {
    private static final int DATA_POSITION = 1;
    private static final int DATA_ROTATION = 2;
    private static final int DATA_SCALE = 3;

    public final XTransform transform;

    private XEntity entity;

    private XTransformListener listener = new XTransformListener() {
        @Override
        public void onChange(XTransform transform, int code) {
            if(entity != null) {
                XEntity parent = entity.getParent();
                if(parent != null) {
                    XTransformComponent parentTransformComponent = parent.getComponent(XTransformComponent.class);
                    if(parentTransformComponent != null) {
                        XTransform parentTransform = parentTransformComponent.transform;
                        parentTransform.callOnChangeListeners(XTransformComponent.LISTENER_CODE_LOCAL_TRANSFORM_CHANGED);
                    }
                }
            }
        }
    };

    public XLocalTransformComponent() {
        transform = XTransform.newInstance();
    }

    @Override
    public void onAttach(XWorld world, XEntity entity) {
        this.entity = entity;
        XTransformComponent transformComponent = entity.getComponent(XTransformComponent.class);
        if(getClass() == XLocalTransformComponent.class && transformComponent != null) {
            transform.addTransformListener(listener);
            // Force Calculating offset
            XTransform otherTransform = transformComponent.transform;
            otherTransform.setDragging(true);
            otherTransform.callOnChangeListeners(XTransformComponent.LISTENER_CODE_LOCAL_TRANSFORM_UPDATE_OFFSET);
            otherTransform.setDragging(false);
        }
    }

    public XLocalTransformComponent position(float x, float y, float z) {
        transform.setPosition(x, y, z);
        return this;
    }

    public XLocalTransformComponent rotate(float rx, float ry, float rz) {
        transform.setRotation(rx, ry, rz);
        return this;
    }

    public XLocalTransformComponent scale(float sx, float sy, float sz) {
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
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        XUIOpTransform op = XUIOpTransform.get();
        op.drawOffset = false;
        op.drawSize = false;
        uiData.transform(transform, op);
    }

    @Override
    public void onReset() {
        transform.reset();
    }
}