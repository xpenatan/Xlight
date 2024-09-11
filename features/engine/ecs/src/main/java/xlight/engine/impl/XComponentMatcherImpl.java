package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.entity.XEntity;

public class XComponentMatcherImpl implements XComponentMatcher {
    private String debugClass;
    private final Bits all = new Bits();
    private final Bits one = new Bits();
    private final Bits exclude = new Bits();
    private String hashStr = "";
    private int hash = 0;

    private IntArray entities;

    private XComponentMatcherListener listener;

    public XComponentMatcherListener debugListener;

    public XComponentMatcherImpl(Bits all, Bits one, Bits exclude, String debugClass, String hashStr, int hash, XComponentMatcherListener listener) {
        this.all.or(all);
        this.one.or(one);
        this.exclude.or(exclude);
        this.hashStr = hashStr;
        this.debugClass = debugClass;
        this.hash = hash;
        this.listener = listener;
        if(listener == null) {
            entities = new IntArray();
        }
    }

    @Override
    public String toString() {
        String hash = "";
        if(!debugClass.isEmpty()) {
            hash = " - " + debugClass;
        }
        return super.toString() + hash;
    }

    @Override
    public String getID() {
        return hashStr;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public IntArray getEntities() {
        return entities;
    }

    @Override
    public boolean matches(Bits mask) {
        if(all.isEmpty() && exclude.isEmpty() && one.isEmpty()) {
            return false;
        }

        return (all.isEmpty() || mask.containsAll(all)) &&
                (exclude.isEmpty() || !mask.intersects(exclude)) &&
                (one.isEmpty() || mask.intersects(one));
    }

    @Override
    public boolean matches(XEntity entity) {
        return matches(entity.getComponentMask());
    }

    @Override
    public boolean contains(XEntity entity) {
        if(debugListener != null) {
            debugListener.contains(entity);
        }
        if(listener != null) {
            return listener.contains(entity);
        }
        else {
            return entities.contains(entity.getId());
        }
    }

    public void addEntity(XEntityImpl entity) {
        if(debugListener != null) {
            debugListener.onEntityUpdate(XComponentMatcherState.ADD, entity);
        }
        if(listener != null) {
            listener.onEntityUpdate(XComponentMatcherState.ADD, entity);
        }
        else {
            entities.add(entity.getId());
        }
    }

    public void removeEntity(XEntityImpl entity) {
        if(debugListener != null) {
            debugListener.onEntityUpdate(XComponentMatcherState.REMOVE, entity);
        }
        if(listener != null) {
            listener.onEntityUpdate(XComponentMatcherState.REMOVE, entity);
        }
        else {
            entities.removeValue(entity.getId());
        }
    }

    public static String getHash(int id, Bits all, Bits one, Bits exclude) {
        int allHash = all.hashCode();
        int oneHash = one.hashCode();
        int excludeHash = exclude.hashCode();
        return "{id:" + id + "}{all:" + allHash + "}{one:" + oneHash + "}{exclude:" + excludeHash + "}";
    }
}