package xlight.engine.pool;

import com.badlogic.gdx.utils.Array;

public abstract class XPool<T> {

    public static boolean ASSERT = true;

    protected Array<T> freeObjects;

    public XPool() {
        this(100, 100);
    }

    public XPool(int initialCapacity) {
        this(0, initialCapacity);
    }

    public XPool(int initialSize, int capacity) {
        freeObjects = new Array<>(false, capacity);
        createNewObjects(initialSize);
    }

    public T get(int index) {
        return freeObjects.get(index);
    }

    /**
     * Get available pool size
     */
    public int getSize() {
        return freeObjects.size;
    }

    public T obtain() {
        if(freeObjects.size > 0) {
            return freeObjects.removeIndex(0);
        }
        else {
            return newObject();
        }
    }

    public void free(T object) {
        if(ASSERT) {
            if(freeObjects.contains(object, true)) {
                throw new RuntimeException("Pool object already exist in pool" + object);
            }
        }

        if(object instanceof XPoolable) {
            ((XPoolable)object).onReset();
        }
        freeObjects.add(object);
    }

    /**
     * Remove all objects from pool
     */
    public void clear() {
        freeObjects.clear();
    }

    public int getFree() {
        return freeObjects.size;
    }

    public void fill() {
        int newSize = freeObjects.items.length - freeObjects.size;
        createNewObjects(newSize);
    }

    protected abstract T newObject();

    private void createNewObjects(int size) {
        for(int i = 0; i < size; i++) {
            T t = newObject();
            freeObjects.add(t);
        }
    }
}