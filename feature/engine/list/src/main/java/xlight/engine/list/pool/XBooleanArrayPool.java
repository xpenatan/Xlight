package xlight.engine.list.pool;

import xlight.engine.list.XBooleanArray;
import xlight.engine.pool.XPool;

public class XBooleanArrayPool extends XPool<XBooleanArray> {

    public XBooleanArrayPool() {
        super();
    }

    public XBooleanArrayPool(int initialCapacity) {
        super(initialCapacity);
    }

    public XBooleanArrayPool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    @Override
    protected XBooleanArray newObject() {
        return new XBooleanArray();
    }
}