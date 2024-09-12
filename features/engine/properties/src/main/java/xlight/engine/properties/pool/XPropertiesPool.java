package xlight.engine.properties.pool;

import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.impl.properties.XPropertiesImpl;
import xlight.engine.json.XJson;
import xlight.engine.properties.XProperties;

public class XPropertiesPool extends XPool<XProperties> {

    private final XPoolController poolController;
    private XJson json;

    public XPropertiesPool(XJson json, XPoolController poolController, int initialCapacity) {
        super(initialCapacity);
        this.json = json;
        this.poolController = poolController;
    }

    @Override
    protected XProperties newObject() {
        return new XPropertiesImpl(poolController, json);
    }
}