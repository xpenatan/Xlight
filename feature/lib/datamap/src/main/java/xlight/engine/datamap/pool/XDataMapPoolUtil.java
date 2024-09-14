package xlight.engine.datamap.pool;

import xlight.engine.pool.XPoolController;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.json.XJson;

public class XDataMapPoolUtil {

    public static void registerPool(XJson json, XPoolController poolController) {
        if(!poolController.containsPool(XDataMap.class)) {
            int initialCapacity = 100;
            XDataMapPool dataMapPool = new XDataMapPool(json, poolController, initialCapacity);
            XDataMapArrayPool dataMapArrayPool = new XDataMapArrayPool(poolController, initialCapacity);

            poolController.registerPool(XDataMap.class, dataMapPool);
            poolController.registerPool(XDataMapArray.class, dataMapArrayPool);
        }
    }
}
