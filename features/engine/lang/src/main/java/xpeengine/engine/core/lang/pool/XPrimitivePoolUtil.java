package xpeengine.engine.core.lang.pool;

import xlight.engine.pool.XPool;
import xpeengine.engine.core.lang.XBoolean;
import xpeengine.engine.core.lang.XByte;
import xpeengine.engine.core.lang.XChar;
import xpeengine.engine.core.lang.XDouble;
import xpeengine.engine.core.lang.XFloat;
import xpeengine.engine.core.lang.XInt;
import xpeengine.engine.core.lang.XLong;
import xpeengine.engine.core.lang.XShort;

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

    public static XPool<XInt> newInttPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XInt newObject() { return new XInt(); } };
    }

    public static XPool<XLong> newLongPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XLong newObject() { return new XLong(); } };
    }

    public static XPool<XShort> newShortPool(int initialSize, int capacity) {
        return new XPool<>(initialSize, capacity) { @Override  public XShort newObject() { return new XShort(); } };
    }
}