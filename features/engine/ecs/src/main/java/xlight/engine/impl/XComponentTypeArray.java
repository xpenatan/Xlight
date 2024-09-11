package xlight.engine.impl;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;

class XComponentTypeArray {
    public IntMap<XComponent> components;
    public final int componentId;

    public XComponentTypeArray(int componentId) {
        components = new IntMap<>();
        this.componentId = componentId;
    }

    public void addComponent(XEntity entity, XComponent component) {
        int id = entity.getId();
        components.put(id, component);
    }

    public XComponent removeComponent(XEntity entity) {
        int id = entity.getId();
        XComponent oldComponent = components.remove(id);
        return oldComponent;
    }

    public XComponent getComponent(XEntity entity) {
        int id = entity.getId();
        return components.get(id);
    }
}
