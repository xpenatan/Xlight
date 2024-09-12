package xlight.engine.json.pool;

import xlight.engine.pool.XPool;
import xlight.engine.json.XJsonValue;
import xlight.engine.json.XJsonValueImpl;

public class XJsonValuePool extends XPool<XJsonValue> {

    public XJsonValuePool(int initialCapacity) {
        super(initialCapacity, initialCapacity);
    }

    public XJsonValuePool(int initialCapacity, int max) {
        super(initialCapacity, max);
    }

    @Override
    protected XJsonValue newObject() {
        return new XJsonValueImpl(this);
    }
}