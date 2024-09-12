package xlight.engine.list;

import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import java.util.Iterator;

public class XArray<T> {

    private Array<T> array;

    private XList<T> list;

    public XArray() {
        this(true);
    }

    public XArray(boolean ordered) {
        this(ordered, 16);
    }

    public XArray(boolean ordered, int capacity) {
        array = new Array<>(ordered, capacity);

        this.list = new XList<>() {
            Array.ArrayIterator<T> objIterator;

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
                    return objIterator.next();
                }
            };

            @Override
            public int getSize() {
                return array.size;
            }

            @Override
            public Iterator<T> iterator() {
                objIterator = array.iterator();
                return iterator;
            }
        };
    }

    public int getSize() {
        return array.size;
    }

    public void add(T value) {
        array.add(value);
    }

    public void insert(int index, T value) {
        array.insert(index, value);
    }

    public T removeIndex(int index) {
        return array.removeIndex(index);
    }

    public T removeValue(T value, boolean identity) {
        int index = indexOf(value, identity);
        if(index >= 0) {
            return removeIndex(index);
        }
        return null;
    }

    public int indexOf(T value, boolean identity) {
        if(identity) {
            for(int i = 0; i < array.size; i++) {
                if(array.get(i) == value) {
                    return i;
                }
            }
        }
        else {
            for(int i = 0; i < array.size; i++) {
                if(array.get(i).equals(value)) {
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

    public void sort(Comparator<? super T> comparator) {
        array.sort(comparator);
    }

    public XList<T> getList() {
        return list;
    }

    public T get(int index) {
        return array.get(index);
    }

    public void clear() {
        array.clear();
    }

    public void addAll(XArray<T> other) {
        int size = other.getSize();
        for(int i = 0; i < size; i++) {
            T t = other.get(i);
            array.add(t);
        }
    }
}