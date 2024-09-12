package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XIntMapList<T> extends XIntMapListRaw<T, XIntMapListNode<T>> {

    public XIntMapList() {
        this(new XPool<>() {
            @Override
            protected XIntMapListNode<T> newObject() {
                return new XIntMapListNode<>();
            }
        });
    }

    public XIntMapList(XPool<XIntMapListNode<T>> pool) {
        super(pool);
    }

}