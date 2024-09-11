package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import java.util.Objects;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;

class XEntityServiceImpl implements XEntityService {

    XEntityArray entities;
    OrderedMap<String, XComponentMatcherImpl> matchersMap;

    public XEntityServiceImpl() {
        int initialSize = 100;
        entities = new XEntityArray(initialSize);
        matchersMap = new OrderedMap<>();
    }

    @Override
    public XEntity obtain() {
        return entities.obtainEntity();
    }

    @Override
    public void releaseEntity(XEntity entity) {
        entities.releaseEntity(entity.getId());
    }

    @Override
    public void attachEntity(XEntity entity) {
        entities.attachEntity(entity.getId());
    }

    @Override
    public boolean detachEntity(XEntity entity) {
        return entities.detachEntity(entity.getId());
    }

    @Override
    public XEntity getEntity(int id) {
        return entities.get(id);
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