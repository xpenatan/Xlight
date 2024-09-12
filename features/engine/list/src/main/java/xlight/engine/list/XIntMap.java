package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XIntMap<T> extends XIntDataMap<T, XIntMap.XIntMapNode<T>> {

    public XIntMap() {
        this(new XPool<>() {
            @Override
            protected XIntMapNode<T> newObject() {
                return new XIntMapNode<>();
            }
        });
    }

    public XIntMap(XPool<XIntMapNode<T>> pool) {
        super(pool);
    }

    public static class XIntMapNode<T> extends XIntDataMapNode<T> {}
}