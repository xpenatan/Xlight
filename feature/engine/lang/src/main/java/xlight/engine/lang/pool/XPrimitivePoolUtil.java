package xlight.engine.lang.pool;

import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.lang.XBoolean;
import xlight.engine.lang.XByte;
import xlight.engine.lang.XChar;
import xlight.engine.lang.XDouble;
import xlight.engine.lang.XFloat;
import xlight.engine.lang.XInt;
import xlight.engine.lang.XLong;
import xlight.engine.lang.XShort;

public class XPrimitivePoolUtil {

    public static XPool<XBoolean> newBooleanPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XBoolean newObject() { return new XBoolean(); } };
    }

    public static XPool<XByte> newBytePool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XByte newObject() { return new XByte(); } };
    }

    public static XPool<XChar> newCharPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XChar newObject() { return new XChar(); } };
    }

    public static XPool<XDouble> newDoublePool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XDouble newObject() { return new XDouble(); } };
    }

    public static XPool<XFloat> newFloatPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XFloat newObject() { return new XFloat(); } };
    }

    public static XPool<XInt> newIntPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XInt newObject() { return new XInt(); } };
    }

    public static XPool<XLong> newLongPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XLong newObject() { return new XLong(); } };
    }

    public static XPool<XShort> newShortPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XShort newObject() { return new XShort(); } };
    }

    public static void registerPool(XPoolController poolController) {
        int initialCapacity = 100;
        if(!poolController.containsPool(XBoolean.class)) {
            poolController.registerPool(XBoolean.class, newBooleanPool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XByte.class)) {
            poolController.registerPool(XByte.class, newBytePool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XChar.class)) {
            poolController.registerPool(XChar.class, newCharPool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XDouble.class)) {
            poolController.registerPool(XDouble.class, newDoublePool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XFloat.class)) {
            poolController.registerPool(XFloat.class, newFloatPool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XLong.class)) {
            poolController.registerPool(XLong.class, newLongPool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XInt.class)) {
            poolController.registerPool(XInt.class, newIntPool(initialCapacity, initialCapacity));
        }
        if(!poolController.containsPool(XShort.class)) {
            poolController.registerPool(XShort.class, newShortPool(initialCapacity, initialCapacity));
        }
    }
}