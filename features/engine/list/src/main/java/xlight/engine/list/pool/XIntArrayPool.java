package xlight.engine.list.pool;

import xlight.engine.list.XIntArray;
import xlight.engine.pool.XPool;

public class XIntArrayPool extends XPool<XIntArray> {

    public XIntArrayPool() {
        super();
    }

    public XIntArrayPool(int initialCapacity) {
        super(initialCapacity);
    }

    public XIntArrayPool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    @Override
    protected XIntArray newObject() {
        return new XIntArray();
    }
}