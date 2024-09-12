package xlight.engine.list;

import java.util.Iterator;
import xlight.engine.pool.XPool;

public class XLinkedDataList<NODE_VALUE, NODE_TYPE extends XLinkedDataListNode<NODE_VALUE, NODE_TYPE>> {

    private final Object SYNC = new Object();
    private NODE_TYPE head;
    private NODE_TYPE tail;
    private int size = 0;
    private XPool<NODE_TYPE> pool;

    private XList<NODE_VALUE> list;
    private XList<NODE_TYPE> nodeList;

    public XLinkedDataList(XPool<NODE_TYPE> pool) {
        init(pool);
    }

    protected void init(XPool<NODE_TYPE> pool) {
        this.pool = pool;

        this.list = new XList<>() {
            NODE_TYPE cur;

            private final Iterator<NODE_VALUE> iterator = new Iterator<>() {

                @Override
                public boolean hasNext() {
                    return cur != null;
                }

                @Override
                public NODE_VALUE next() {
                    NODE_TYPE retCur = cur;
                    cur = cur.next;
                    return retCur.value;
                }
            };

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public Iterator<NODE_VALUE> iterator() {
                cur = getHead();
                return iterator;
            }
        };

        this.nodeList = new XList<>() {
            NODE_TYPE cur;

            private final Iterator<NODE_TYPE> iterator = new Iterator<>() {

                @Override
                public boolean hasNext() {
                    return cur != null;
                }

                @Override
                public NODE_TYPE next() {
                    NODE_TYPE retCur = cur;
                    cur = cur.next;
                    return retCur;
                }
            };

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public Iterator<NODE_TYPE> iterator() {
                cur = getHead();
                return iterator;
            }
        };
    }

    public boolean insertBefore(NODE_TYPE cur, NODE_VALUE object) {
        synchronized(SYNC) {
            if(toInsertBefore(cur, object) == null)
                return false;

            return true;
        }
    }

    public boolean insertAfter(NODE_TYPE cur, NODE_VALUE object) {
        synchronized(SYNC) {
            if(toInsertAfter(cur, object) == null)
                return false;
            return true;
        }
    }

    public NODE_TYPE addHead(NODE_VALUE object) {
        synchronized(SYNC) {
            return toAddHead(object);
        }
    }

    public NODE_TYPE getHead() {
        synchronized(SYNC) {
            return head;
        }
    }

    public NODE_TYPE addTail(NODE_VALUE object) {
        synchronized(SYNC) {
            return toAddTail(object);
        }
    }

    public NODE_TYPE getTail() {
        synchronized(SYNC) {
            return tail;
        }
    }

    public boolean changeOrder(NODE_TYPE node, float newOrder) {
        NODE_TYPE del = toRemove(node);
        if(del != null) {
            NODE_VALUE obj = node.value;
            pool.free(del);
            return addOrder(newOrder, obj);
        }
        return false;
    }

    public boolean addOrder(float order, NODE_VALUE object) {
        return toAddOrder(order, object) != null;
    }

    public boolean removeNode(NODE_TYPE node) {
        synchronized(SYNC) {
            NODE_TYPE c = toRemove(node);
            if(c != null) {
                pool.free(c);
                return true;
            }
            return false;
        }
    }

    public boolean contains(NODE_VALUE value) {
        synchronized(SYNC) {
            return indexOf(value, true) != -1;
        }
    }

    public int indexOf(Object value, boolean identity) {
        synchronized(SYNC) {
            int i = 0;
            NODE_TYPE cur = getHead();
            if(identity) {
                while(cur != null) {
                    if(value == cur.value)
                        return i;
                    i++;
                    cur = cur.next;
                }
            } else {
                while(cur != null) {
                    if(value.equals(cur.value))
                        return i;
                    i++;
                    cur = cur.next;
                }
            }
            return -1;
        }
    }

    public NODE_VALUE getObjectByIndex(int index) {
        synchronized(SYNC) {
            int i = 0;
            NODE_TYPE cur = getHead();
            while(cur != null) {
                if(index == i) {
                    return cur.value;
                }

                i++;
                cur = cur.next;
            }

            return null;
        }
    }

    public NODE_VALUE removeIndex(int index) {
        synchronized(SYNC) {
            int i = 0;
            NODE_TYPE cur = getHead();
            while(cur != null) {
                if(index == i) {
                    NODE_VALUE obj = cur.value;
                    removeNode(cur);
                    return obj;
                }

                i++;
                cur = cur.next;
            }
            return null;
        }
    }

    public boolean remove(NODE_VALUE value) {
        synchronized(SYNC) {
            NODE_TYPE cur = getHead();
            while(cur != null) {
                if(value == cur.value) {
                    removeNode(cur);
                    return true;
                }
                cur = cur.next;
            }
            return false;
        }
    }

    public void clear() {
        synchronized(SYNC) {
            NODE_TYPE curNode = head;
            while(curNode != null) {
                NODE_TYPE nextNode = curNode.next;
                removeNode(curNode);
                curNode = nextNode;
            }

            size = 0;

            head = null;
            tail = null;
        }
    }

    public int getSize() {
        synchronized(SYNC) {
            return size;
        }
    }

    public boolean isEmpty() {
        synchronized(SYNC) {
            return size == 0;
        }
    }

    public void addAll(XLinkedDataList<NODE_VALUE, NODE_TYPE> otherList) {
        NODE_TYPE cur = otherList.getHead();
        while(cur != null) {
            NODE_VALUE value = cur.getValue();
            addTail(value);
            cur = cur.getNext();
        }
    }

    public XList<NODE_VALUE> getList() {
        return list;
    }

    public XList<NODE_TYPE> getNodeList() {
        return nodeList;
    }

    protected NODE_TYPE obtainNode() {
        NODE_TYPE poolObject = pool.obtain();
        return poolObject;
    }

    NODE_TYPE toAddHead(NODE_VALUE object) {
        synchronized(SYNC) {
            NODE_TYPE item =  obtainNode();
            item.value = object;

            if(head == null) {
                head = item;
                tail = item;

                size++;
                return item;
            }

            item.next = head;
            head.prev = item;
            head = item;
            size++;

            return item;
        }
    }

    NODE_TYPE toAddTail(NODE_VALUE object) {
        synchronized(SYNC) {
            NODE_TYPE item =  obtainNode();
            item.value = object;

            if(head == null) {
                head = item;
                tail = item;

                size++;
                return item;
            }

            item.prev = tail;
            tail.next = item;
            tail = item;
            size++;

            return item;
        }
    }

    NODE_TYPE toInsertAfter(NODE_TYPE cur, NODE_VALUE object) {
        synchronized(SYNC) {
            if(cur == null)
                return null;

            NODE_TYPE item =  obtainNode();
            item.value = object;

            if(cur == tail) {
                item.prev = tail;
                tail.next = item;
                tail = item;
            } else {
                item.prev = cur;
                item.next = cur.next;
                cur.next.prev = item;
                cur.next = item;
            }

            size++;
            return item;
        }
    }

    /**
     * Add object to a order value. <br>
     * It may not be fast in inserting because it needs to scroll through the list
     */
    NODE_TYPE toAddOrder(float order, NODE_VALUE object) {
        NODE_TYPE node = null;
        if(size > 0) {
            NODE_TYPE curr = head;
            while(curr.next != null && curr.next.order <= order) {
                curr = curr.next;
            }
            if(order < curr.order) {
                node = toInsertBefore(curr, object);
                node.order = order;
            } else {
                node = toInsertAfter(curr, object);
                node.order = order;
            }
        } else { // first in the list
            node = toAddTail(object);
            node.order = order;
        }
        return node;
    }

    NODE_TYPE toInsertBefore(NODE_TYPE cur, NODE_VALUE object) {
        synchronized(SYNC) {
            if(cur == null)
                return null;
            NODE_TYPE item =  obtainNode();
            item.value = object;

            if(cur == head) {
                item.next = head;
                head.prev = item;
                head = item;
            } else {
                item.next = cur;
                item.prev = cur.prev;
                cur.prev.next = item;
                cur.prev = item;
            }
            size++;
            return item;
        }
    }

    NODE_TYPE toRemove(NODE_TYPE node) {
        synchronized(SYNC) {
            if(node == null)
                return null;

            NODE_TYPE c = node;
            NODE_TYPE n = node.next;
            NODE_TYPE p = node.prev;
            size--;

            if(size == 0) {
                head = null;
                tail = null;

                if(node.prev != null || node.next != null) {
                    throw new RuntimeException("Node cannot contains next or prev: " + node);
                }
                return c;
            }

            if(c == head) {
                n.prev = null; // TODO n ficou null (error)
                head = n;
                c.next = null;
                c.prev = null;
                return c;
            }
            if(c == tail) {
                p.next = null;
                tail = p;
                c.next = null;
                c.prev = null;
                return c;
            }

            p.next = n;
            n.prev = p;
            c.next = null;
            c.prev = null;
            return node;
        }
    }

}