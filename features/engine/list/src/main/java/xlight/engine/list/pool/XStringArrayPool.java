package xlight.engine.list.pool;

import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPool;

public class XStringArrayPool extends XPool<XStringArray> {

    public XStringArrayPool() {
        super();
    }

    public XStringArrayPool(int initialCapacity) {
        super(initialCapacity);
    }

    public XStringArrayPool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    @Override
    protected XStringArray newObject() {
        return new XStringArray();
    }
}