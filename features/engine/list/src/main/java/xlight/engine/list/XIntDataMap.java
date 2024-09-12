package xlight.engine.list;

import com.badlogic.gdx.utils.IntMap;
import java.util.Iterator;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolable;


public class XIntDataMap<T, NODE extends XIntDataMap.XIntDataMapNode<T>> {

    private IntMap<NODE> map;
    private XPool<NODE> pool;
    private XList<T> list;

    public XIntDataMap(XPool<NODE> pool) {
        init(pool);
    }

    protected void init(XPool<NODE> pool) {
        map = new IntMap<>();
        this.pool = pool;

        this.list = new XList<>() {
            Iterator<IntMap.Entry<NODE>> objIterator;

            private Iterator<T> iterator = new Iterator<>() {

                @Override
                public boolean hasNext() {
                    if(objIterator == null || !objIterator.hasNext()) {
                        objIterator = null;
                        return false;
                    }
                    return true;
                }

                @Override
                public T next() {
                    if(objIterator == null) {
                        return null;
                    }
                    return objIterator.next().value.value;
                }
            };

            @Override
            public int getSize() {
                return map.size;
            }

            @Override
            public Iterator<T> iterator() {
                objIterator = map.iterator();
                return iterator;
            }
        };
    }

    public T put(int key, T value) {
        NODE node = pool.obtain();
        node.value = value;
        node.key = key;
        NODE oldNode = map.put(key, node);
        if(oldNode != null) {
            T oldValue = oldNode.value;
            pool.free(oldNode);
            return oldValue;
        }
        return null;
    }

    public T remove(int key) {
        NODE oldNode = map.remove(key);
        if(oldNode != null) {
            T oldValue = oldNode.value;
            pool.free(oldNode);
            return oldValue;
        }
        return null;
    }

    public T get(int key){
        NODE node = map.get(key);
        if(node != null) {
            return node.value;
        }
        return null;
    }

    public NODE getNode(int key){
        return map.get(key);
    }

    public int getSize() {
        return map.size;
    }

    public boolean contains(int key) {
        return map.containsKey(key);
    }

    public XList<T> getList() {
        return list;
    }

    public static class XIntDataMapNode<T> implements XPoolable {
        int key;
        T value;

        public XIntDataMapNode() {
            onReset();
        }

        public int getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        @Override
        public void onReset() {
            value = null;
            key = -1;
        }
    }
}