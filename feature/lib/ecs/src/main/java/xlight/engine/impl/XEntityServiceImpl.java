package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.Iterator;
import java.util.Objects;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XWorldEvent;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;
import xlight.engine.math.XMath;

class XEntityServiceImpl implements XEntityService {

    private XEntityArray entities;
    private OrderedMap<String, XComponentMatcherImpl> matchersMap;
    private XList<XEntity> entityList;
    private int entitySize;
    private XWorld world;

    public XEntityServiceImpl(XWorld world) {
        this.world = world;
    }

    public void init(XWorld world) {
        int initialSize = 200;
        entities = new XEntityArray(initialSize, world);
        matchersMap = new OrderedMap<>();

        entityList = new XList<>() {
            private int index;
            private final Iterator<XEntity> iterator = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return entities.getIndexOrNext(index) >= 0;
                }

                @Override
                public XEntity next() {
                    int indexOrNext = entities.getIndexOrNext(index);
                    XEntityImpl entity = entities.get(indexOrNext);
                    index = indexOrNext + 1;
                    return entity;
                }
            };


            @Override
            public int getSize() {
                return entitySize;
            }

            @Override
            public Iterator<XEntity> iterator() {
                index = 0;
                return iterator;
            }
        };
    }

    @Override
    public XEntity obtain() {
        return entities.obtainEntity();
    }

    @Override
    public XEntity obtain(int id) {
        return entities.obtainEntity(id);
    }

    @Override
    public boolean releaseEntity(XEntity entity) {
        return releaseEntityInternal(entity, true);
    }

    @Override
    public boolean attachEntity(XEntity entity) {
        if(entities.attachEntity(entity.getId())) {
            entitySize++;

            int componentsSize = entity.getComponentsSize();
            for(int i = 0; i < componentsSize; i++) {
                XComponent c = entity.getComponentAt(i);
                c.onAttach(world, entity);
            }

            // Loop all matchers if entity mask bits match
            updateEntityAdded((XEntityImpl)entity);

            // Send event as soon as possible.
            world.getWorldService().getEventService().sendEvent(XWorldEvent.EVENT_ATTACH_ENTITY, entity, null, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean detachEntity(XEntity entity) {
        return detachEntityInternal(entity, true);
    }

    @Override
    public XEntity getEntity(int id) {
        return entities.get(id);
    }

    @Override
    public XList<XEntity> getEntities() {
        return entityList;
    }

    @Override
    public void clear() {
        for(int i = 0; i < entities.items.length; i++) {
            XEntityImpl entity = entities.get(i);
            if(entity != null) {
                if(detachEntityInternal(entity, false)) {
                    releaseEntityInternal(entity, false);
                }
            }
        }
    }

    public boolean releaseEntityInternal(XEntity entity, boolean detachChild) {
        if(detachChild) {
            XList<XIntSetNode> list = entity.getChildList();
            if(list.getSize() > 0) {
                // Removing parent also removes
                for(XIntSetNode node : list) {
                    int childId = node.getKey();
                    XEntity childEntity = getEntity(childId);
                    releaseEntityInternal(childEntity, true);
                }
            }
        }
        // Try to detach it first before releasing.
        detachEntityInternal(entity, false);
        return entities.releaseEntity(entity.getId());
    }

    private boolean detachEntityInternal(XEntity entity, boolean detachChild) {
        if(detachChild) {
            XList<XIntSetNode> list = entity.getChildList();
            if(list.getSize() > 0) {
                // Detaching parent also removes children
                for(XIntSetNode node : list) {
                    int childId = node.getKey();
                    XEntity childEntity = getEntity(childId);
                    detachEntityInternal(childEntity, true);
                }
            }
        }

        boolean flag = entities.detachEntity(entity.getId());
        if(flag) {
            // Send event as soon as possible.
            world.getWorldService().getEventService().sendEvent(XWorldEvent.EVENT_DETACH_ENTITY, entity, null, false);
            entitySize--;
            XEntityImpl e = (XEntityImpl)entity;
            // Loop all matchers if entity mask bits match
            XMath.BITS_1.clear();
            updateEntityRemove(e, e.componentMask, XMath.BITS_1);
            entity.clearChildren();
            entity.setParent(null);
        }
        return flag;
    }

    public XComponentMatcher getOrCreate(int id, XComponentMatcherBuilderImpl builder, XComponentMatcher.XComponentMatcherListener listener) {
        String hash = XComponentMatcherImpl.getHash(id, builder.all, builder.one, builder.exclude);
        XComponentMatcherImpl matcher = matchersMap.get(hash);
        if(matcher == null) {
            String debugClass = "{all:" + builder.debugAllClasses + "}{one:" + builder.debugOneClasses + "}{exclude:" + builder.debugExcludeClasses + "}";
            int hashId = Objects.hashCode(hash);
            matcher = new XComponentMatcherImpl(builder.all, builder.one, builder.exclude, debugClass, hash, hashId, listener);
            matchersMap.put(hash, matcher);

            /*
             * When adding a matcher we need to loop all active entities
             * and add it to the matcher if it matches
             */
            for(int i = 0; i < entities.items.length; i++) {
                XEntityImpl entity = entities.get(i);
                if(entity != null && entity.isAttached()) {
                    updateEntityAdded(entity, matcher);
                }
            }
        }
        else {
            if(id != 0) {
                // Only 1 matcher can be obtained if id is not 0
                return null;
            }
        }
        return matcher;
    }

    public void onComponentAdded(XEntityImpl entity, XComponent component) {
        // Loop all matchers if entity mask bits match
        if(entity.isAttached()) {
            component.onAttach(world, entity);
            updateEntityAdded(entity);
        }
    }

    public void updateEntityAdded(XEntityImpl entity) {
        // Loop all matchers if entity mask bits match
        for(ObjectMap.Entry<String, XComponentMatcherImpl> entry : matchersMap) {
            updateEntityAdded(entity, entry.value);
        }
    }

    public void updateEntityAdded(XEntityImpl entity, XComponentMatcherImpl matcher) {
        if(matcher.matches(entity.componentMask)) {
            if(!matcher.contains(entity)) {
                matcher.addEntity(entity);
            }
        }
    }

    public void updateEntityRemoved(XEntityImpl entity, XComponentMatcherImpl matcher, Bits oldBit, Bits newBit) {
        if(matcher.matches(oldBit)) {
            // Original bit is matched before change
            if(!matcher.matches(newBit)) {
                // Updated bit does not match so we remove it
                if(matcher.contains(entity)) {
                    matcher.removeEntity(entity);
                }
            }
        }
    }

    private void updateEntityRemove(XEntityImpl entity, Bits oldBit, Bits newBit) {
        // Loop all matchers if entity mask bits does not match.
        for(ObjectMap.Entry<String, XComponentMatcherImpl> entry : matchersMap) {
            updateEntityRemoved(entity, entry.value, oldBit, newBit);
        }
    }

    public void onComponentRemoved(XEntityImpl entity, Bits oldBit, Bits newBit) {
        if(entity.isAttached()) {
            updateEntityRemove(entity, oldBit, newBit);
        }
    }

}