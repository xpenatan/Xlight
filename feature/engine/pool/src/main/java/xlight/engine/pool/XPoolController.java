package xlight.engine.pool;

public interface XPoolController {
    <T1, T2> T2 obtainPoolObject(Class<T1> type);

    /**
     * Create or obtain from pool
     **/
    <T1, T2> T2 obtainObject(Class<T1> type);

    /**
     * add object back to pool
     **/
    void releaseObject(Object poolObject);

    /**
     * add object back to pool
     **/
    void releaseObject(Class<?> type, Object poolObject);

    /**
     * Do not obtain objects from this pool because the engine may show incorrect sync data
     */
    <T0 extends XPool<T2>, T1, T2> T0 getPool(Class<T1> type);

    /**
     * Register a pool list. Will replace if its already added
     **/
    <T1, T2> void registerPool(Class<T1> type, XPool<T2> pool);

    <T> void removePool(Class<T> type);

    boolean containsPool(Class<?> type);

    void clear();
}