package xlight.engine.properties.pool;

import xlight.engine.json.XJson;
import xlight.engine.pool.XPoolController;
import xlight.engine.properties.XProperties;

public class XPropertiesPoolUtil {

    public static void registerPool(XJson json, XPoolController poolController) {
        if(!poolController.containsPool(XProperties.class)) {
            int initialCapacity = 100;
            XPropertiesPool dataMapPool = new XPropertiesPool(json, poolController, initialCapacity);

            poolController.registerPool(XProperties.class, dataMapPool);
        }
    }
}