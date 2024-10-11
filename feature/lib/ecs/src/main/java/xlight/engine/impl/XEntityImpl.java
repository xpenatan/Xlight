package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.entity.XEntityState;
import xlight.engine.list.XIntSet;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;

class XEntityImpl implements XEntity {
    private static String EMPTY_NAME = "NoName";
    public int index;
    public XEntityState state;
    Bits componentMask;
    IntArray componentsIndex;
    private Bits componentMaskReadOnly;
    private boolean isVisible;
    private String name;
    private boolean isSavable;

    private int parentId;

    private XIntSet children;

    private XWorld world;

    XEntityImpl(int index, XWorld world) {
        this.world = world;
        componentMask = new Bits();
        componentMaskReadOnly = new Bits();
        componentsIndex = new IntArray();
        children = new XIntSet();
        reset(true);
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
        XComponentService componentService = world.getWorldService().getComponentService();
        return componentService.getComponent(this, type);
    }

    @Override
    public boolean attachComponent(XComponent component) {
        XComponentService componentService = world.getWorldService().getComponentService();
        return componentService.attachComponent(this, component);
    }

    @Override
    public boolean detachComponent(Class<?> type) {
        XComponentService componentService = world.getWorldService().getComponentService();
        return componentService.detachComponent(this, type);
    }

    @Override
    public boolean detachComponent(XComponent component) {
        XComponentService componentService = world.getWorldService().getComponentService();
        return componentService.detachComponent(this, component);
    }

    @Override
    public boolean containsComponent(Class<?> type) {
        XComponentService componentService = world.getWorldService().getComponentService();
        return componentService.containsComponent(this, type);
    }

    @Override
    public int getComponentsSize() {
        return componentsIndex.size;
    }

    @Override
    public XComponent getComponentAt(int index) {
        if(index < 0 || index >= componentsIndex.size) {
            return null;
        }
        int i = componentsIndex.get(index);
        return world.getWorldService().getComponentService().getComponentIndex(this, i);
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

    @Override
    public boolean isSavable() {
        return isSavable;
    }

    @Override
    public void setSavable(boolean flag) {
        isSavable = flag;
    }

    @Override
    public XEntity getParent() {
        if(parentId == -1) {
            return null;
        }
        XEntityService entityService = world.getWorldService().getEntityService();
        return entityService.getEntity(parentId);
    }

    @Override
    public boolean setParent(XEntity parent) {
        if(parent != null && parent.getId() == index) {
            throw new GdxRuntimeException("Cannot add the same entity");
        }
        if(parentId == -1 && parent == null) {
            return false;
        }

        XEntityService entityService = world.getWorldService().getEntityService();
        XEntity thisParent = null;
        if(parentId != -1) {
            thisParent = entityService.getEntity(parentId);
        }

        if(containsParentChild(parent, this) || thisParent == parent || parent != null && children.contains(parent.getId())) {
            return false;
        }

        if(thisParent != null) {
            thisParent.removeChild(index);
        }
        if(parent != null) {
            parentId = parent.getId();
            XEntityImpl e = (XEntityImpl)parent;
            e.children.put(index);
        }
        else {
            parentId = -1;
        }
        return true;
    }

    @Override
    public XEntity getChild(int id) {
        if(children.containsKey(id)) {
            XEntityService entityService = world.getWorldService().getEntityService();
            return entityService.getEntity(id);
        }
        return null;
    }

    @Override
    public XList<XIntSetNode> getChildList() {
        return children.getNodeList();
    }

    @Override
    public XIntSetNode getChildHead() {
        return children.getHead();
    }

    @Override
    public boolean putChild(XEntity entity) {
        return children.put(entity.getId());
    }

    @Override
    public XEntity removeChild(int id) {
        if(children.remove(id)) {
            XEntityService entityService = world.getWorldService().getEntityService();
            XEntityImpl entity = (XEntityImpl)entityService.getEntity(id);
            entity.parentId = -1;
            return entity;
        }
        return null;
    }

    @Override
    public void clearChildren() {
        for(XIntSetNode node : children.getNodeList()) {
            int id = node.getKey();
            XEntityService entityService = world.getWorldService().getEntityService();
            XEntityImpl entity = (XEntityImpl)entityService.getEntity(id);
            entity.parentId = -1;
        }
        children.clear();
    }

    @Override
    public String toString() {
        return getName() + "[" + "id:" + index + ";parent:" + parentId + ";attached:" + isAttached() + ";components:" + children + "]";
    }

    public void reset(boolean isFirst) {
        if(!isFirst) {
            clearChildren();
            setParent(null);
        }

        name = EMPTY_NAME;
        index = -1;
        isVisible = true;
        parentId = -1;
        isSavable = false;
        state = XEntityState.RELEASE;
        componentMask.clear();
        componentMaskReadOnly.clear();
        componentsIndex.clear();
    }

    private static boolean containsParentChild(XEntity cur, XEntity parent) {
        while(cur != null) {
            if(cur == parent) {
                return true;
            }
            cur = cur.getParent();
        }
        return false;
    }
}