package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XIntMap<T> extends XIntMapListRaw<T, XIntMapListNode<T>> {

    public XIntMap() {
        this(new XPool<>() {
            @Override
            protected XIntMapListNode<T> newObject() {
                return new XIntMapListNode<>();
            }
        });
    }

    public XIntMap(XPool<XIntMapListNode<T>> pool) {
        super(pool);
    }

}