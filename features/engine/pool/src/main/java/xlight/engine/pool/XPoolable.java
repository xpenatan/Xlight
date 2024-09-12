package xlight.engine.pool;

public interface XPoolable {
    void onReset();

    /**
     * Use this type if not null instead of object.getClass().
     */
    default Class<?> getPoolType() { return null; }
}