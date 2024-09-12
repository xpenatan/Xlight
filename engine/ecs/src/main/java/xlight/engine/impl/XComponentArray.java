package xlight.engine.impl;


import xlight.engine.ecs.component.XComponent;

class XComponentArray {
    public XComponent[] items;

    public XComponentArray() {
        items = new XComponent[1];
    }

    public void set(int index, XComponent value) {
        items[index] = value;
    }

    public XComponent get(int index) {
        return items[index];
    }

    public void ensureCapacity(int newSize) {
        if(newSize > items.length) {
            int max1 = Math.max(8, newSize);
            int max2 = Math.max(max1, (int)(newSize * 1.75f));
            resize(max2);
        }
    }

    public void resize(int newSize) {
        XComponent[] items = this.items;
        XComponent[] newItems = new XComponent[newSize];
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
}