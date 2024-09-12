package xlight.engine.datamap.pool;

import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.datamap.XDataMapArray;

public class XDataMapArrayPool extends XPool<XDataMapArray> {

    private final XPoolController poolController;

    public XDataMapArrayPool(XPoolController poolController) {
        super();
        this.poolController = poolController;
    }

    public XDataMapArrayPool(XPoolController poolController, int initialCapacity) {
        super(initialCapacity);
        this.poolController = poolController;
    }

    public XDataMapArrayPool(XPoolController poolController, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.poolController = poolController;
    }

    @Override
    protected XDataMapArray newObject() {
        return new XDataMapArray(poolController);
    }
}