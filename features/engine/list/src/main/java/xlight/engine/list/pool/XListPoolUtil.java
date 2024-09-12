package xlight.engine.list.pool;

import xlight.engine.list.XIntArray;
import xlight.engine.list.XBooleanArray;
import xlight.engine.list.XFloatArray;
import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPoolController;

public class XListPoolUtil {

    public static void registerPool(XPoolController poolController) {
        if(!poolController.containsPool(XStringArray.class)) {
            XStringArrayPool stringArrayPool = new XStringArrayPool();
            poolController.registerPool(XStringArray.class, stringArrayPool);
        }
        if(!poolController.containsPool(XIntArray.class)) {
            XIntArrayPool intArrayPool = new XIntArrayPool();
            poolController.registerPool(XIntArray.class, intArrayPool);
        }
        if(!poolController.containsPool(XFloatArray.class)) {
            XFloatArrayPool floatArrayPool = new XFloatArrayPool();
            poolController.registerPool(XFloatArray.class, floatArrayPool);
        }
        if(!poolController.containsPool(XBooleanArray.class)) {
            XBooleanArrayPool booleanArrayPool = new XBooleanArrayPool();
            poolController.registerPool(XBooleanArray.class, booleanArrayPool);
        }
    }
}