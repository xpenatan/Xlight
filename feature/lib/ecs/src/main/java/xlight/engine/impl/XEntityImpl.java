package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityState;

class XEntityImpl implements XEntity {
    private static String EMPTY_NAME = "NoName";
    public int index;
    public XEntityState state;
    Bits componentMask;
    IntArray componentsIndex;
    private Bits componentMaskReadOnly;
    private boolean isVisible;
    private XComponentService componentService;
    private String name;

    XEntityImpl(int index, XComponentService componentService) {
        componentMask = new Bits();
        componentMaskReadOnly = new Bits();
        componentsIndex = new IntArray();
        this.componentService = componentService;
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

    @Override
    public <T extends XComponent> T getComponent(Class<T> type) {
        return componentService.getComponent(this, type);
    }

    @Override
    public <T extends XComponent> void attachComponent(XComponent component) {
        componentService.attachComponent(this, component);
    }

    @Override
    public <T extends XComponent> void detachComponent(Class<T> type) {
        componentService.detachComponent(this, type);
    }

    @Override
    public void setName(String name) {
        String trim = name.trim();
        if(trim.isEmpty()) {
            trim = EMPTY_NAME;
        }
        this.name = trim;
    }

    @Override
    public String getName() {
        return name;
    }

    public void reset() {
        name = EMPTY_NAME;
        index = -1;
        isVisible = true;
        state = XEntityState.RELEASE;
        componentMask.clear();
        componentMaskReadOnly.clear();
        componentsIndex.clear();
    }
}