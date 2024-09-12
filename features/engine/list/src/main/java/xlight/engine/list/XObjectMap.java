package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XObjectMap<NODE_KEY, NODE_VALUE> extends XObjectMapRaw<NODE_KEY, NODE_VALUE, XObjectMap.XObjectMapNode<NODE_KEY, NODE_VALUE>> {

    public XObjectMap() {
        this(new XPool<>() {
            @Override
            protected XObjectMapNode<NODE_KEY, NODE_VALUE> newObject() {
                return new XObjectMapNode<>();
            }
        });
    }

    public XObjectMap(XPool<XObjectMapNode<NODE_KEY, NODE_VALUE>> pool) {
        super(pool);
    }

    public static class XObjectMapNode<NODE_KEY, NODE_VALUE> extends XObjectDataMapNode<NODE_KEY, NODE_VALUE, XObjectMapNode<NODE_KEY, NODE_VALUE>> {}
}