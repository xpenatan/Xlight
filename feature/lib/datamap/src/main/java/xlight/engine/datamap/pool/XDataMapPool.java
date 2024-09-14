package xlight.engine.datamap.pool;

import xlight.engine.datamap.XDataMap;
import xlight.engine.impl.datamap.XDataMapImpl;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.json.XJson;

public class XDataMapPool extends XPool<XDataMap> {

    private final XPoolController poolController;
    private XJson json;

    public XDataMapPool(XJson json, XPoolController poolController, int initialCapacity) {
        super(0, initialCapacity);
        if(json == null || poolController == null) {
            throw new RuntimeException("Parameters cannot be null");
        }
        this.json = json;
        this.poolController = poolController;
        fill();
    }

    @Override
    protected XDataMap newObject() {
        return new XDataMapImpl(poolController, json);
    }
}