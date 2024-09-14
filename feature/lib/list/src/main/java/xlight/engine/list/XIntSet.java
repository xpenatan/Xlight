package xlight.engine.list;

import xlight.engine.pool.XPool;

public class XIntSet extends XIntSetDataList<XIntSet.XIntSetNode> {

    public XIntSet() {
        this(new XPool<>() {
            @Override
            protected XIntSetNode newObject() {
                return new XIntSetNode();
            }
        });
    }

    public XIntSet(XPool<XIntSetNode> pool) {
        super(pool);
    }

    public static class XIntSetNode extends XIntDataMapListNode<Object, XIntSetNode> {}
}