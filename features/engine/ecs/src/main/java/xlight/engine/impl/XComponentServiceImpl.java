package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.math.XBit;
import xlight.engine.math.XPair;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.component.XComponentType;
import xlight.engine.ecs.entity.XEntity;

class XComponentServiceImpl implements XComponentService {

    Array<XPair<XComponentType, XComponentArray>> components;

    XEntityServiceImpl entityService;
    private XComponentMatcherBuilderImpl matcher;

    public XComponentServiceImpl(XEntityServiceImpl entityService) {
        components = new Array<>();
        this.entityService = entityService;
        matcher = new XComponentMatcherBuilderImpl(this, entityService);
    }

    @Override
    public <T extends XComponent> boolean registerComponent(Class<T> type) {
        XComponentType componentType = getComponentType(type);
        if(componentType == null) {
            int nextIndex = components.size;
            componentType = new XComponentType(type, nextIndex);
            XPair<XComponentType, XComponentArray> pair = new XPair<>();
            pair.a = componentType;
            pair.b = new XComponentArray();
            components.add(pair);
            return true;
        }
        return false;
    }

    @Override
    public <T extends XComponent> XComponentType getComponentType(Class<T> type) {
        return getComponentInternal(type);
    }

    public int getComponentIndex(Class<?> type) {
        XComponentType componentType = getComponentInternal(type);
        if(componentType == null) {
            return -1;
        }
        return componentType.getIndex();
    }

    @Override
    public <T extends XComponent> T getComponent(XEntity entity, Class<T> type) {
        XComponentType componentType = getComponentType(type);
        T component = null;
        if(componentType != null) {
            int index = componentType.getIndex();
            XPair<XComponentType, XComponentArray> pair = components.get(index);
            XComponentArray componentList = pair.b;
            component = getComponent(componentList, entity.getId());
        }
        return component;
    }

    @Override
    public <T extends XComponent> void attachComponent(XEntity entity, XComponent component) {
        XComponentType componentType = getComponentType(component.getClass());
        if(componentType != null) {
            int index = componentType.getIndex();
            XPair<XComponentType, XComponentArray> pair = components.get(index);
            XComponentArray componentList = pair.b;
            setComponent((XEntityImpl)entity, componentList, componentType, component);
        }
    }

    @Override
    public <T extends XComponent> void detachComponent(XEntity entity, Class<T> type) {
        XComponentType componentType = getComponentType(type);
        if(componentType != null) {
            int index = componentType.getIndex();
            XPair<XComponentType, XComponentArray> pair = components.get(index);
            XComponentArray componentList = pair.b;
            setComponent((XEntityImpl)entity, componentList, componentType, null);
        }
    }

    @Override
    public XComponentMatcherBuilder getMatcherBuilder() {
        matcher.reset();
        return matcher;
    }

    private XComponentType getComponentInternal(Class<?> type) {
        for(int i = 0; i < components.size; i++) {
            XPair<XComponentType, XComponentArray> pair = components.get(i);
            XComponentType componentType = pair.a;
            if(componentType.getType() == type) {
                return componentType;
            }
        }
        return null;
    }

    private <T extends XComponent> T getComponent(XComponentArray componentList, int entityId) {
        if(entityId < 0 || entityId >= componentList.items.length) {
            return null;
        }
        XComponent component = componentList.get(entityId);
        return (T)component;
    }

    private void setComponent(XEntityImpl entity, XComponentArray componentList, XComponentType componentType, XComponent component) {
        int entityIndex = entity.getId();
        if(entityIndex >= 0) {
            componentList.ensureCapacity(entityIndex + 1);
            int componentIndex = componentType.getIndex();
            if(component != null) {
                // Add component only if entity does not contain it
                if(getComponent(componentList, entityIndex) == null) {
                    entity.componentMask.set(componentIndex);
                    entity.componentsIndex.add(componentIndex);
                    entityService.onComponentAdded(entity, component);
                }
            }
            else {
                if(getComponent(componentList, entityIndex) != null) {
                    Bits tmpBits = XBit.BITS_1;
                    tmpBits.clear();
                    tmpBits.or(entity.componentMask);
                    entity.componentMask.clear(componentIndex);
                    entityService.onComponentRemoved(entity, tmpBits, entity.componentMask);
                    entity.componentsIndex.removeValue(componentIndex);
                }
            }
            componentList.set(entityIndex, component);
        }
    }
}