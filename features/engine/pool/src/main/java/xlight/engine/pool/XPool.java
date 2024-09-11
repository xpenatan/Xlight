package xlight.engine.pool;

import com.badlogic.gdx.utils.Array;

public abstract class XPool<T extends XPoolable> {

    private Array<T> freeObjects;

    public XPool(int initialSize, int capacity) {
        freeObjects = new Array<>(false, capacity);
        createNewObjects(initialSize);
    }

    private void createNewObjects(int size) {
        for(int i = 0; i < size; i++) {
            T t = newObject();
            freeObjects.add(t);
        }
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
        object.onReset();
        freeObjects.add(object);
    }

    /**
     * Remove all objects from pool
     */
    public void clear() {
        freeObjects.clear();
    }

    public abstract T newObject();
}