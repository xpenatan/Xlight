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
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;

class XComponentServiceImpl implements XComponentService {

    Array<XPair<XComponentType, XComponentArray>> components;

    XEntityServiceImpl entityService;
    private XComponentMatcherBuilderImpl matcher;

    private XECSWorldImpl world;

    private XPoolController poolController;

    public void init(XECSWorldImpl world, XEntityServiceImpl entityService) {
        this.entityService = entityService;
        this.world = world;
        poolController = world.getGlobalData(XPoolController.class);
        components = new Array<>();
        matcher = new XComponentMatcherBuilderImpl(this, entityService);
    }

    @Override
    public <T extends XComponent> boolean registerComponent(Class<T> type) {
        XComponentType componentType = getComponentInternal(type);
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
    public XComponentType getComponentType(Class<?> type) {
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
        XComponentType componentType = getComponentInternal(type);
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
    public boolean containsComponent(XEntity entity, Class<?> type) {
        XComponentType componentType = getComponentInternal(type);
        if(componentType != null) {
            int index = componentType.getIndex();
            XComponent component = getComponentIndex(entity, index);
            return component != null;
        }
        return false;
    }

    public <T extends XComponent> T getComponentIndex(XEntity entity, int index) {
        T component = null;
        XPair<XComponentType, XComponentArray> pair = components.get(index);
        XComponentArray componentList = pair.b;
        component = getComponent(componentList, entity.getId());
        return component;
    }

    @Override
    public boolean attachComponent(XEntity entity, XComponent component) {
        XComponentType componentType = getComponentInternal(component.getMatcherType());
        if(componentType != null) {
            int index = componentType.getIndex();
            XPair<XComponentType, XComponentArray> pair = components.get(index);
            XComponentArray componentList = pair.b;
            return setComponent((XEntityImpl)entity, componentList, componentType, component);
        }
        return false;
    }

    @Override
    public boolean attachComponent(XEntity entity, Class<? extends XComponent> type) {
        if(!entity.containsComponent(type)) {
            XComponent component = poolController.obtainObject(type);
            if(!attachComponent(entity, component)) {
                poolController.releaseObject(component);
            }
        }
        return false;
    }

    @Override
    public boolean detachComponent(XEntity entity, Class<?> type) {
        XComponentType componentType = getComponentInternal(type);
        if(componentType != null) {
            int index = componentType.getIndex();
            XPair<XComponentType, XComponentArray> pair = components.get(index);
            XComponentArray componentList = pair.b;
            return setComponent((XEntityImpl)entity, componentList, componentType, null);
        }
        return false;
    }

    @Override
    public boolean detachComponent(XEntity entity, XComponent component) {
        if(component == null) {
            return false;
        }
        return detachComponent(entity, component.getMatcherType());
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

    private boolean setComponent(XEntityImpl entity, XComponentArray componentList, XComponentType componentType, XComponent component) {
        int entityIndex = entity.getId();
        boolean ret = false;
        if(entityIndex >= 0) {
            componentList.ensureCapacity(entityIndex + 1);
            int componentIndex = componentType.getIndex();
            if(component != null) {
                // Add component only if entity does not contain it
                if(getComponent(componentList, entityIndex) == null) {
                    entity.componentMask.set(componentIndex);
                    entity.componentsIndex.add(componentIndex);
                    componentList.set(entityIndex, component);
                    entityService.onComponentAdded(entity, component);
                    ret = true;
                }
            }
            else {
                XComponent componentFound = getComponent(componentList, entityIndex);
                if(componentFound != null) {
                    Bits tmpBits = XBit.BITS_1;
                    tmpBits.clear();
                    tmpBits.or(entity.componentMask);
                    entity.componentMask.clear(componentIndex);
                    componentFound.onDetach(world, entity);
                    entityService.onComponentRemoved(entity, tmpBits, entity.componentMask);
                    entity.componentsIndex.removeValue(componentIndex);
                    componentList.set(entityIndex, null);
                    if(componentFound instanceof XPoolable) {
                        poolController.releaseObject(componentFound);
                    }
                    ret = true;
                }
            }
        }
        return ret;
    }
}