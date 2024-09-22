package xlight.engine.impl;


import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityState;

class XEntityArray {
    public XEntityImpl[] items;

    private IntArray reusableIds;

    private XComponentService componentService;

    public XEntityArray(int capacity, XComponentService componentService) {
        reusableIds = new IntArray(false, capacity);
        this.componentService = componentService;
        items = new XEntityImpl[capacity];
        fillEntities(items);
    }

    public XEntityImpl obtainEntity() {
        int nextId = getNextId();
        XEntityImpl entity = items[nextId];
        entity.index = nextId;
        entity.state = XEntityState.DETACHED;
        return entity;
    }

    public boolean detachEntity(int id) {
        if(id < 0 || id >= items.length) {
            return false;
        }
        XEntityImpl entity = items[id];
        if(entity.state == XEntityState.DETACHED) {
            return false;
        }
        entity.state = XEntityState.DETACHED;
        return true;
    }

    public boolean attachEntity(int id) {
        if(id < 0 || id >= items.length) {
            return false;
        }
        XEntityImpl entity = items[id];
        if(entity.state == XEntityState.ATTACHED) {
            return false;
        }
        entity.state = XEntityState.ATTACHED;
        return true;
    }

    public boolean releaseEntity(int id) {
        if(id < 0 || id >= items.length) {
            return false;
        }
        XEntityImpl entity = items[id];
        if(entity.state == XEntityState.RELEASE) {
            return false;
        }
        reusableIds.insert(0, id);
        entity.reset();
        return true;
    }

    public XEntityImpl get(int id) {
        if(id < 0 || id >= items.length) {
            return null;
        }
        XEntityImpl entity = items[id];
        if(entity.state == XEntityState.ATTACHED || entity.state == XEntityState.DETACHED) {
            return entity;
        }
        return null;
    }

    public void resize(int newSize) {
        XEntityImpl[] items = this.items;
        XEntityImpl[] newItems = new XEntityImpl[newSize];
        int size;
        if(items.length < newSize) {
            size = items.length;
        }
        else {
            size = newSize;
        }
        System.arraycopy(items, 0, newItems, 0, size);
        this.items = newItems;
    }

    public int getDetachedEntities() {
        int count = 0;
        for(int i = 0; i < items.length; i++) {
            XEntityState state = items[i].state;
            if(state == XEntityState.DETACHED) {
                count++;
            }
        }
        return count;
    }

    public int getAttachedEntities() {
        int count = 0;
        for(int i = 0; i < items.length; i++) {
            XEntityState state = items[i].state;
            if(state == XEntityState.ATTACHED) {
                count++;
            }
        }
        return count;
    }

    public int getReleaseEntities() {
        int count = 0;
        for(int i = 0; i < items.length; i++) {
            XEntityState state = items[i].state;
            if(state == XEntityState.RELEASE) {
                count++;
            }
        }
        if(count != reusableIds.size) {
            throw new RuntimeException("[DEBUG] Wrong reusable size");
        }
        return reusableIds.size;
    }

    private int getNextId() {
        if(reusableIds.size == 0) {
            int newSize = (int)(items.length * 1.75f);
            resize(newSize);
            fillEntities(items);
        }
        return reusableIds.removeIndex(0);
    }

    private void fillEntities(XEntityImpl[] items) {
        for(int i = 0; i < items.length; i++) {
            XEntityImpl item = items[i];
            if(item == null) {
                items[i] = new XEntityImpl(i, componentService);
                reusableIds.add(i);
            }
        }
    }
}