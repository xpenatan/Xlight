package xlight.engine.list;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.pool.XPool;

public class XIntMapListRaw<NODE_VALUE, NODE_TYPE extends XIntDataMapListNode<NODE_VALUE, NODE_TYPE>> {

    private IntMap<NODE_TYPE> map;

    private XLinkedDataList<NODE_VALUE, NODE_TYPE> linkedList;

    public XIntMapListRaw(XPool<NODE_TYPE> pool) {
        init(pool);
    }

    protected void init(XPool<NODE_TYPE> pool) {
        map = new IntMap<>();
        linkedList = new XLinkedDataList<>(pool);
    }

    public NODE_VALUE put(int key, NODE_VALUE value) {
        NODE_TYPE node = linkedList.addTail(value);
        node.value = value;
        node.key = key;
        NODE_TYPE oldNode = map.put(key, node);
        if(oldNode != null) {
            NODE_VALUE oldValue = oldNode.value;
            linkedList.removeNode(oldNode);
            return oldValue;
        }
        return null;
    }

    public NODE_VALUE remove(int key) {
        NODE_TYPE oldNode = map.remove(key);
        if(oldNode != null) {
            NODE_VALUE oldValue = oldNode.value;
            linkedList.removeNode(oldNode);
            return oldValue;
        }
        return null;
    }

    public NODE_VALUE get(int key){
        NODE_TYPE node = map.get(key);
        if(node != null) {
            return node.value;
        }
        return null;
    }

    public NODE_TYPE getNode(int key){
        return map.get(key);
    }

    public int getSize() {
        return map.size;
    }

    public XList<NODE_VALUE> getList() {
        return linkedList.getList();
    }

    public NODE_TYPE getHead() {
        return linkedList.getHead();
    }

    public NODE_TYPE getTail() {
        return linkedList.getTail();
    }

    public XList<NODE_TYPE> getNodeList() {
        return linkedList.getNodeList();
    }

    public void clear() {
        map.clear();
        linkedList.clear();
    }

    public boolean containsKey(int key) {
        return map.containsKey(key);
    }
}