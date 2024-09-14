package xlight.engine.impl;

import com.badlogic.gdx.utils.GdxRuntimeException;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;
import xlight.engine.pool.XPools;

public class XPoolControllerImpl implements XPoolController {

    private XPools pools;

    public XPoolControllerImpl() {
        pools = new XPools();
    }

    public <T1, T2> T2 obtainPoolObject(Class<T1> type) {
        if(type == null)
            return null;
        return pools.obtain(type);
    }

    public void clear() {
        pools.clear();
    }

    @Override
    public <T1, T2> T2 obtainObject(Class<T1> type) {
        if(type == null)
            return null;
        return obtainPoolObject(type);
    }

    @Override
    public void releaseObject(Object poolObject) {
        if(poolObject instanceof XPoolable) {
            pools.free(poolObject);
        }
        else {
            throw new GdxRuntimeException("Pool controller can only release a XPoolable object");
        }
    }

    @Override
    public void releaseObject(Class<?> type, Object poolObject) {
        if(poolObject instanceof XPoolable) {
            pools.free(type, poolObject);
        }
        else {
            throw new GdxRuntimeException("Pool controller can only release a XPoolable object");
        }
    }

    @Override
    public <T0 extends XPool<T2>, T1, T2> T0 getPool(Class<T1> type) {
        return pools.get(type);
    }

    @Override
    public <T1, T2> void registerPool(Class<T1> type, XPool<T2> pool) {
        pools.put(type, pool);
    }

    @Override
    public <T> void removePool(Class<T> type) {
        pools.remove(type);
    }

    @Override
    public boolean containsPool(Class<?> type) {
        return pools.contains(type);
    }

    public int getTotalFreeObjects() {
        return pools.getTotalFreeObject();
    }

    public int getTotalRegisteredClasses() {
        return pools.getTotalRegisteredClasses();
    }
}