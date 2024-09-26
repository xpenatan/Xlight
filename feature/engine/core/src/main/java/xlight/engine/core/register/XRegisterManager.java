package xlight.engine.core.register;

import xlight.engine.impl.XMetaClassImpl;
import xlight.engine.list.XList;
import xlight.engine.pool.XPool;

public interface XRegisterManager {
    XList<XMetaClass> getRegisteredClasses();
    boolean isRegisteredClass(Class<?> type);
    boolean isRegisteredClass(int key);
    XMetaClass getRegisteredClass(Class<?> type);
    XMetaClass getRegisteredClass(int key);
    XMetaClass registerClass(int key, Class<?> type);
    XMetaClass registerClass(int key, Class<?> type, XPool<?> pool);
}