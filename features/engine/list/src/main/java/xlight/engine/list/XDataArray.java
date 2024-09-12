package xlight.engine.list;

import java.util.Iterator;
import xlight.engine.pool.XPool;

public class XDataArray<T, NODE extends XDataArray.XDataArrayNode<T>> {

    private XArray<NODE> array;
    private XPool<NODE> pool;

    private XList<T> objList;

    public XDataArray() {
        array = new XArray<>();
        XPool<XDataArrayNode<T>> xpePool = new XPool<>() {
            @Override
            protected XDataArrayNode<T> newObject() {
                return new XDataArrayNode<>();
            }
        };
        pool = (XPool<NODE>)xpePool;
    }

    public XDataArray(XPool<NODE> pool) {
        array = new XArray<>();
        this.pool = pool;

        objList = new XList<>() {
            Iterator<NODE> objIterator;

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
                    return objIterator.next().value;
                }
            };

            @Override
            public int getSize() {
                return array.getSize();
            }

            @Override
            public Iterator<T> iterator() {
                objIterator = array.getList().iterator();
                return iterator;
            }
        };
    }

    public int getSize() {
        return array.getSize();
    }

    public void add(T value) {
        NODE node = pool.obtain();
        node.value = value;
        array.add(node);
    }

    public void insert(int index, T value) {
        NODE node = pool.obtain();
        node.value = value;
        array.insert(index, node);
    }

    public void removeIndex(int index) {
        NODE node = array.removeIndex(index);
        pool.free(node);
    }

    public void removeValue(T value, boolean identity) {
        int index = indexOf(value, identity);
        if(index >= 0) {
            removeIndex(index);
        }
    }

    public int indexOf(T value, boolean identity) {
        int size = array.getSize();
        if(identity) {
            for(int i = 0; i < size; i++) {
                if(array.get(i).value == value) {
                    return i;
                }
            }
        }
        else {
            for(int i = 0; i < size; i++) {
                if(array.get(i).value.equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void swap(int first, int second) {
        array.swap(first, second);
    }

    public boolean contains(T value, boolean identity) {
        return indexOf(value, identity) != -1;
    }

    public T get(int index) {
        XDataArrayNode<T> node = array.get(index);
        return node.value;
    }

    public NODE getNode(int index) {
        return array.get(index);
    }

    public XList<T> getList() {
        return objList;
    }

    public XList<NODE> getNodeList() {
        return array.getList();
    }

    public static class XDataArrayNode<T> {
        T value;
        public T getValue() {
            return value;
        }
    }
}