package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XLinkedList<NODE_VALUE> extends XLinkedDataList<NODE_VALUE, XLinkedListNode<NODE_VALUE>> {
    public XLinkedList() {
        super(new XPool<>() {
            @Override
            protected XLinkedListNode<NODE_VALUE> newObject() {
                return new XLinkedListNode<>();
            }
        });
    }
}