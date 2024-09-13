package xlight.engine.pool;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;

public class XPools {

    private final OrderedMap<Class, XPool> typePools = new OrderedMap();

    public XPools() {
    }

    public <T0 extends XPool<T2>, T1, T2> T0 get(Class<T1> type) {
        XPool<?> pool = typePools.get(type);
        if(pool == null) {
            throw new GdxRuntimeException("Pool is not registered for type " + type.getName());
        }
        T0 p = (T0)pool;
        return p;
    }

    public boolean contains(Class<?> type) {
        return typePools.containsKey(type);
    }

    /**
     * Sets an existing pool for the specified type, stored in a Class to {@link XPool} map.
     */
    public void put(Class<?> type, XPool<?> pool) {
        typePools.put(type, pool);
    }

    public <T> boolean remove(Class<T> type) {
        XPool<?> remove = typePools.remove(type);
        if(remove != null) {
            remove.clear();
            return true;
        }
        return false;
    }

    /**
     * Obtains an object from the {@link #get(Class) pool}.
     */
    public <T1, T2> T2 obtain(Class<T1> type) {
        XPool<T2> objectXpePool = get(type);
        return objectXpePool.obtain();
    }

    /**
     * Frees an object from the {@link #get(Class) pool}.
     */
    public void free(Object object) {
        if(object == null) throw new IllegalArgumentException("object cannot be null.");
        Class<?> type = object.getClass();
        if(object instanceof XPoolable) {
            type = ((XPoolable)object).getPoolType();
        }
        free(type, object);
    }

    /**
     * Frees an object from the {@link #get(Class) pool}.
     */
    public void free(Class<?> type, Object object) {
        if(object == null) throw new IllegalArgumentException("object cannot be null.");
        XPool pool = typePools.get(type);
        if(pool == null) return; // Ignore freeing an object that was never retained.
        pool.free(object);
    }

    public void clear() {
        Entries<Class, XPool> iterator = typePools.iterator();

        while(iterator.hasNext) {
            Entry<Class, XPool> entry = iterator.next();
            XPool value = entry.value;

            value.clear();
        }
    }

    public ObjectMap<Class, XPool> getTypePools() {
        return typePools;
    }

    public int getTotalFreeObject() {
        Entries<Class, XPool> iterator = typePools.iterator();
        int count = 0;
        while(iterator.hasNext) {
            Entry<Class, XPool> entry = iterator.next();
            XPool value = entry.value;
            int free = value.getFree();
            count += free;
        }
        return count;
    }

    public int getTotalRegisteredClasses() {
        return typePools.size;
    }
}
