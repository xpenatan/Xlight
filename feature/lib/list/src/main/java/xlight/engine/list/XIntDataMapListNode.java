package xlight.engine.list;

public class XIntDataMapListNode<T, TYPE extends XIntDataMapListNode<T, TYPE>> extends XLinkedDataListNode<T, TYPE> {
    int key;

    public int getKey() {
        return key;
    }

    @Override
    public void onReset() {
        super.onReset();
        key = -1;
    }
}