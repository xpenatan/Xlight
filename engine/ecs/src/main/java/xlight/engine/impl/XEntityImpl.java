package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityState;

class XEntityImpl implements XEntity {

    public int index;
    public XEntityState state;
    Bits componentMask;
    Bits componentMaskReadOnly;
    IntArray componentsIndex;

    XEntityImpl(int index) {
        componentMask = new Bits();
        componentMaskReadOnly = new Bits();
        componentsIndex = new IntArray();
        reset();
        this.index = index;
    }

    @Override
    public int getId() {
        return index;
    }

    @Override
    public XEntityState getState() {
        return state;
    }

    @Override
    public Bits getComponentMask() {
        componentMaskReadOnly.clear();
        componentMaskReadOnly.or(componentMask);
        return componentMaskReadOnly;
    }

    @Override
    public boolean isAttached() {
        return state == XEntityState.ATTACHED;
    }

    @Override
    public boolean isDetached() {
        return state == XEntityState.DETACHED;
    }

    public void reset() {
        index = -1;
        state = XEntityState.RELEASE;
        componentMask.clear();
        componentMaskReadOnly.clear();
        componentsIndex.clear();
    }
}