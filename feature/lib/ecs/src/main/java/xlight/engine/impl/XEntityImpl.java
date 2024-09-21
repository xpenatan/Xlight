package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityState;

class XEntityImpl implements XEntity {

    public int index;
    public XEntityState state;
    Bits componentMask;
    IntArray componentsIndex;
    private Bits componentMaskReadOnly;
    private boolean isVisible;

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

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean flag) {
        isVisible = flag;
    }

    public void reset() {
        index = -1;
        isVisible = true;
        state = XEntityState.RELEASE;
        componentMask.clear();
        componentMaskReadOnly.clear();
        componentsIndex.clear();
    }
}