package xlight.engine.list;

import xlight.engine.pool.XPoolable;

public class XLinkedDataListNode<NODE_VALUE, NODE_TYPE extends XLinkedDataListNode<NODE_VALUE, NODE_TYPE>> implements XPoolable {
    NODE_VALUE value;
    NODE_TYPE prev;
    NODE_TYPE next;
    float order;

    public XLinkedDataListNode() {
        onReset();
    }

    public NODE_VALUE getValue() {
        return value;
    }

    public NODE_TYPE getPrev() {
        return prev;
    }

    public NODE_TYPE getNext() {
        return next;
    }

    @Override
    public void onReset() {
        value = null;
        prev = null;
        next = null;
        order = 0;
    }
}