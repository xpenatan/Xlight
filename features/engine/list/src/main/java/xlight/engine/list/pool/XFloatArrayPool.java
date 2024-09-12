package xlight.engine.list.pool;

import xlight.engine.list.XFloatArray;
import xlight.engine.pool.XPool;

public class XFloatArrayPool extends XPool<XFloatArray> {

    public XFloatArrayPool() {
        super();
    }

    public XFloatArrayPool(int initialCapacity) {
        super(initialCapacity);
    }

    public XFloatArrayPool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    @Override
    protected XFloatArray newObject() {
        return new XFloatArray();
    }
}