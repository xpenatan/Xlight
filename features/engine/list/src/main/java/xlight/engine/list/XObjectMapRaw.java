package xlight.engine.list;

import com.badlogic.gdx.utils.ObjectMap;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolable;

public class XObjectMapRaw<NODE_KEY, NODE_VALUE, NODE_TYPE extends XObjectMapRaw.XObjectDataMapNode<NODE_KEY, NODE_VALUE, NODE_TYPE>> {

    private ObjectMap<NODE_KEY, NODE_TYPE> map;
    private XLinkedDataList<NODE_VALUE, NODE_TYPE> linkedList;

    public XObjectMapRaw(XPool<NODE_TYPE> pool) {
        init(pool);
    }

    protected void init(XPool<NODE_TYPE> pool) {
        map = new ObjectMap<>();
        linkedList = new XLinkedDataList<>(pool);
    }

    public NODE_VALUE put(NODE_KEY key, NODE_VALUE value) {
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

    public NODE_VALUE remove(NODE_KEY key) {
        NODE_TYPE oldNode = map.remove(key);
        if(oldNode != null) {
            NODE_VALUE oldValue = oldNode.value;
            linkedList.removeNode(oldNode);
            return oldValue;
        }
        return null;
    }

    public NODE_VALUE get(NODE_KEY key){
        NODE_TYPE node = map.get(key);
        if(node != null) {
            return node.value;
        }
        return null;
    }

    public NODE_TYPE getNode(NODE_KEY key){
        return map.get(key);
    }

    public int getSize() {
        return map.size;
    }

    public boolean contains(NODE_KEY key) {
        return map.containsKey(key);
    }

    public NODE_TYPE getHead() {
        return linkedList.getHead();
    }

    public NODE_TYPE getTail() {
        return linkedList.getTail();
    }

    public XList<NODE_VALUE> getList() {
        return linkedList.getList();
    }

    public XList<NODE_TYPE> getNodeList() {
        return linkedList.getNodeList();
    }

    public void clear() {
        map.clear();
        linkedList.clear();
    }

    public static class XObjectDataMapNode<NODE_KEY, NODE_VALUE, TYPE extends XObjectDataMapNode<NODE_KEY, NODE_VALUE, TYPE>> extends XLinkedDataListNode<NODE_VALUE, TYPE> implements XPoolable {
        NODE_KEY key;

        public NODE_KEY getKey() {
            return key;
        }

        @Override
        public void onReset() {
            key = null;
            super.onReset();
        }
    }
}