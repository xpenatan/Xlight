package xlight.engine.list;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.pool.XPool;

public class XIntSetDataList<NODE_TYPE extends XIntDataMapListNode<Object, NODE_TYPE>> {

    private IntMap<NODE_TYPE> map;

    private XLinkedDataList<?, NODE_TYPE> linkedList;

    public XIntSetDataList(XPool<NODE_TYPE> pool) {
        init(pool);
    }

    protected void init(XPool<NODE_TYPE> pool) {
        map = new IntMap<>();
        linkedList = new XLinkedDataList<>(pool);
    }

    public void put(int key) {
        if(map.containsKey(key)) {
            return;
        }
        NODE_TYPE node = linkedList.addTail(null);
        node.key = key;
        NODE_TYPE oldNode = map.put(key, node);
        if(oldNode != null) {
            linkedList.removeNode(oldNode);
        }
    }

    public boolean remove(int key) {
        NODE_TYPE oldNode = map.remove(key);
        if(oldNode != null) {
            linkedList.removeNode(oldNode);
            return true;
        }
        return false;
    }

    public void removeNode(XIntSet.XIntSetNode node) {
        NODE_TYPE oldNode = map.remove(node.key);
        if(oldNode != null) {
            linkedList.removeNode(oldNode);
        }
    }

    public NODE_TYPE getNode(int key){
        return map.get(key);
    }

    public int getSize() {
        return map.size;
    }

    public boolean contains(int key) {
        return map.containsKey(key);
    }

    public XList<?> getList() {
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