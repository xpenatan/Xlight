package xlight.engine.aabb;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.Queue;
import java.util.Comparator;
import xlight.engine.transform.XTransform;

public class XAABBTreeDefault implements XAABBTree {

    public static final float DEFAULT_FAT_AABB_MARGIN = 0.2f;

    private final float fatAABBMargin = DEFAULT_FAT_AABB_MARGIN;

    private IntArray tempArray = new IntArray();

    private Array<XAABBTreeNodeDefault> nodes = new Array<>();
    private final Array<XAABBTreeNodeDefault> tempNodeList = new Array<>();
    private final Queue<XAABBTreeNodeDefault> q = new Queue<>();
    private final Array<XAABBTreeNodeDefault> toReInsert = new Array<>();

    private int rootIndex = XAABBTreeNodeDefault.NULL_NODE;
    private IntIntMap idIndexMap = new IntIntMap();

    public IntArray freeNodes = new IntArray();

    private final Vector3 rayOrigin = new Vector3();

    Array<XAABBTreeNodeDefault> toUpdate = new Array<>();

    XAABBDefault combinedAAB = new XAABBDefault();

    public XAABBTreeDefault() {
    }

    private Comparator compareRayCast = new Comparator<XAABBTreeNodeDefault>() {
        @Override
        public int compare(XAABBTreeNodeDefault o1, XAABBTreeNodeDefault o2) {
            XAABBDefault aabb1 = o1.aabb;
            XAABBDefault aabb2 = o2.aabb;
            float dst1 = rayOrigin.dst(aabb1.center);
            float dst2 = rayOrigin.dst(aabb2.center);
            if(dst1 < dst2)
                return -1;
            return 1;
        }
    };

    public Array<XAABBTreeNodeDefault> getNodes() {
        return nodes;
    }

    @Override
    public int getSize() {
        return nodes.size;
    }

    @Override
    public XAABBTreeNodeDefault getNodeID(int id) {
        int index = idIndexMap.get(id, XAABBTreeNodeDefault.NULL_NODE);
        if(index == XAABBTreeNodeDefault.NULL_NODE)
            return null;
        return nodes.get(index);
    }

    @Override
    public XAABBTreeNodeDefault getNodeIndex(int index) {
        if(index == XAABBTreeNodeDefault.NULL_NODE)
            return null;
        return nodes.get(index);
    }

    @Override
    public XAABBTreeNode getRoot() {
        if(rootIndex == XAABBTreeNodeDefault.NULL_NODE)
            return null;
        return getNodeIndex(rootIndex);
    }

    @Override
    public boolean addAABB(int id, XTransform transform) {
        XAABBTreeNodeDefault leaf = getNodeID(id);
        if(leaf == null) {
            XAABBTreeNodeDefault newNode = obtainNode();
            idIndexMap.put(id, newNode.index);
            newNode.dataId = id;
            newNode.data = transform;
            newNode.updatePosition();
            newNode.updateFatAABB(fatAABBMargin);
            insertLeaf(newNode);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAABB(int id) {
        XAABBTreeNodeDefault leaf = getNodeID(id);
        if(leaf != null) {
            idIndexMap.remove(id, XAABBTreeNodeDefault.NULL_NODE);
            removeNode(leaf, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAABB(int id) {
        return idIndexMap.containsKey(id);
    }

    @Override
    public void update() {
        q.clear();
        toReInsert.clear();
        if(rootIndex != XAABBTreeNodeDefault.NULL_NODE) {
            XAABBTreeNodeDefault root = nodes.get(rootIndex);
            q.addLast(root);
        }

        while(!q.isEmpty()) {
            XAABBTreeNodeDefault cur = q.removeFirst();

            if(cur.leftNodeIndex != XAABBTreeNodeDefault.NULL_NODE) {
                XAABBTreeNodeDefault leftNode = nodes.get(cur.leftNodeIndex);
                q.addLast(leftNode);
            }
            if(cur.rightNodeIndex != XAABBTreeNodeDefault.NULL_NODE) {
                XAABBTreeNodeDefault rightNode = nodes.get(cur.rightNodeIndex);
                q.addLast(rightNode);
            }

            if(cur.isLeaf()) {
                cur.updatePosition();
                if(!cur.IsInFatAABB()) {
                    toReInsert.add(cur);
                }
            }
        }

        for(int i = 0; i < toReInsert.size; i++) {
            XAABBTreeNodeDefault node = toReInsert.get(i);
            removeNode(node, false);
        }

        for(int i = 0; i < toReInsert.size; i++) {
            XAABBTreeNodeDefault node = toReInsert.get(i);
            node.updateFatAABB(fatAABBMargin);
            insertLeaf(node);
        }
    }

    @Override
    public void rayCast(Ray ray, Array<XAABBTreeNode> out) {
        rayCast(rootIndex, ray, out, true, 0, 0, true);
    }

    @Override
    public void rayCast(Ray ray, Array<XAABBTreeNode> out, boolean sort, float maxDistance, int total) {
        rayCast(rootIndex, ray, out, sort, maxDistance, total, true);
    }

    private void rayCast(int startNodeIndex, Ray ray, Array<XAABBTreeNode> out, boolean sort, float maxDistance, int total, boolean debug) {
        out.clear();
        tempNodeList.clear();
        if(rootIndex == XAABBTreeNodeDefault.NULL_NODE)
            return;

        if(debug) {
            for(int i = 0; i < nodes.size; i++) {
                nodes.get(i).debugHighlight = false;
            }
        }
        tempArray.add(startNodeIndex);
        while(tempArray.size > 0) {
            int nodeIndex = tempArray.removeIndex(0);
            if(nodeIndex == XAABBTreeNodeDefault.NULL_NODE)
                continue;

            XAABBTreeNodeDefault nodeNode = getNodeIndex(nodeIndex);
            XAABBDefault curAABB = nodeNode.fatAABB;
            XAABBDefault aabb = nodeNode.aabb;

            if(nodeNode.isLeaf())
                curAABB = aabb;
            // Test for overlap between the AABBs.

            boolean flag = Intersector.intersectRayBoundsFast(ray, curAABB.center, curAABB.size);
            if(flag) {
                if(debug) {
                    nodeNode.debugHighlight = true;
                }
                if(nodeNode.isLeaf()) {
                    if(maxDistance <= 0) {
                        if(sort)
                            tempNodeList.add(nodeNode);
                        else {
                            out.add(nodeNode);
                        }
                    }
                    else {
                        Vector3 origin = ray.origin;
                        float dst = origin.dst(curAABB.center);
                        if(dst < maxDistance) {
                            if(sort)
                                tempNodeList.add(nodeNode);
                            else {
                                out.add(nodeNode);
                            }
                        }
                    }
                }
                else {
                    int leftChild = nodeNode.leftNodeIndex;
                    int rightChild = nodeNode.rightNodeIndex;
                    tempArray.add(leftChild);
                    tempArray.add(rightChild);
                }
            }
        }

        if(tempNodeList.size > 0) {
            if(tempNodeList.size > 1) {
                rayOrigin.set(ray.origin);
                tempNodeList.sort(compareRayCast);
                while(tempNodeList.size > 0) {
                    XAABBTreeNodeDefault node = tempNodeList.removeIndex(0);
                    if(total <= 0 || out.size < total) {
                        if(node.data != null) {
                            out.add(node);
                        }
                        else {
                            out.add(node);
                        }
                    }
                }
            }
            else {
                XAABBTreeNodeDefault node = tempNodeList.removeIndex(0);
                if(node.data != null) {
                    out.add(node);
                }
                else {
                    out.add(node);
                }
            }
        }
    }

    private void insertLeaf(XAABBTreeNodeDefault newNode) {
        newNode.isActive = true;
        assert (newNode.parentNodeIndex == XAABBTreeNodeDefault.NULL_NODE);
        assert (newNode.leftNodeIndex == XAABBTreeNodeDefault.NULL_NODE);
        assert (newNode.rightNodeIndex == XAABBTreeNodeDefault.NULL_NODE);

        // if the tree is empty then we make the root the leaf
        if(rootIndex == XAABBTreeNodeDefault.NULL_NODE) {
            rootIndex = newNode.index;
            newNode.parentNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
        }
        else {
            XAABBDefault newAABB = newNode.fatAABB;
            XAABBTreeNodeDefault cur = findBestSibling(newNode, rootIndex);

            if(cur.index == rootIndex) {
                XAABBTreeNodeDefault newRootNode = obtainNode();
                rootIndex = newRootNode.index;
                newRootNode.fatAABB.encapsulate(cur.fatAABB, newAABB);
                cur.parentNodeIndex = rootIndex;
                newNode.parentNodeIndex = rootIndex;
                newRootNode.leftNodeIndex = cur.index;
                newRootNode.rightNodeIndex = newNode.index;
            }
            else {
                // cur is actual leaf, convert cur to branch
                XAABBTreeNodeDefault newBranch = obtainNode();
                newBranch.fatAABB.encapsulate(cur.fatAABB, newAABB);
                newBranch.parentNodeIndex = cur.parentNodeIndex;
                XAABBTreeNodeDefault parentNode = getNodeIndex(cur.parentNodeIndex);
                swapOutChild(parentNode, cur, newBranch);
                cur.parentNodeIndex = newBranch.index;
                newNode.parentNodeIndex = newBranch.index;
                newBranch.leftNodeIndex = cur.index;
                newBranch.rightNodeIndex = newNode.index;

                XAABBTreeNodeDefault parent = getNodeIndex(newBranch.parentNodeIndex);
                while(parent != null) {
                    updateBranchAABB(parent);
                    parent = getNodeIndex(parent.parentNodeIndex);
                }
            }
        }
    }

    private XAABBTreeNodeDefault findBestSibling(XAABBTreeNodeDefault leafNode, int startIndex) {
        // https://github.com/erincatto/box2d/blob/master/src/collision/b2_dynamic_tree.cpp
        int index = startIndex;

        XAABBDefault leafAABB = leafNode.fatAABB;
        XAABBTreeNodeDefault cur = getNodeIndex(index);
        while(!cur.isLeaf()) {
            XAABBTreeNodeDefault leftChild = getNodeIndex(cur.leftNodeIndex);
            XAABBTreeNodeDefault rightChild = getNodeIndex(cur.rightNodeIndex);
            XAABBDefault leftAABB = leftChild.fatAABB;
            XAABBDefault rightAABB = rightChild.fatAABB;

            combinedAAB.reset();
            float leftIncrease = combinedAAB.encapsulate(leftAABB, leafAABB).surfaceArea();
            combinedAAB.reset();
            float rightIncrease = combinedAAB.encapsulate(rightAABB, leafAABB).surfaceArea();

            if(leftIncrease > rightIncrease) {
                cur = rightChild;
            }
            else {
                cur = leftChild;
            }
        }
        return cur;
    }

    private void removeNode(XAABBTreeNodeDefault node, boolean deleteNode) {
        node.isActive = false;
        if(node.index == rootIndex) {
            rootIndex = XAABBTreeNodeDefault.NULL_NODE;
        }
        else if(node.parentNodeIndex == rootIndex) {
            XAABBTreeNodeDefault root = getNodeIndex(rootIndex);
            int newRoot;

            if(node.index == root.leftNodeIndex) {
                newRoot = root.rightNodeIndex;
            }
            else {
                newRoot = root.leftNodeIndex;
            }

            releaseNode(root);
            rootIndex = newRoot;
            root = getNodeIndex(rootIndex);
            root.parentNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
        }
        else {
            XAABBTreeNodeDefault parent = getNodeIndex(node.parentNodeIndex);
            XAABBTreeNodeDefault grandParent = getNodeIndex(parent.parentNodeIndex);

            if(node.index == parent.leftNodeIndex) {
                XAABBTreeNodeDefault parentRightNode = getNodeIndex(parent.rightNodeIndex);
                swapOutChild(grandParent, parent, parentRightNode);
            }
            else {
                XAABBTreeNodeDefault parentLeftNode = getNodeIndex(parent.leftNodeIndex);
                swapOutChild(grandParent, parent, parentLeftNode);
            }

            releaseNode(parent);

            XAABBTreeNodeDefault cur = grandParent;
            while(cur != null) {
                updateBranchAABB(cur);
                cur = getNodeIndex(cur.parentNodeIndex);
            }
        }

        if(deleteNode) {
            releaseNode(node);
        }
    }

    private void updateBranchAABB(XAABBTreeNodeDefault cur) {
        assert (!cur.isLeaf());
        XAABBTreeNodeDefault left = getNodeIndex(cur.leftNodeIndex);
        XAABBTreeNodeDefault right = getNodeIndex(cur.rightNodeIndex);
        cur.fatAABB.encapsulate(left.fatAABB, right.fatAABB);
    }

    private void swapOutChild(XAABBTreeNodeDefault cur, XAABBTreeNodeDefault oldChild, XAABBTreeNodeDefault newChild) {
        assert (oldChild.index == cur.leftNodeIndex || oldChild.index == cur.rightNodeIndex);
        if(oldChild.index == cur.leftNodeIndex) {
            cur.leftNodeIndex = newChild.index;
            XAABBTreeNodeDefault left = getNodeIndex(cur.leftNodeIndex);
            left.parentNodeIndex = cur.index;
        }
        else {
            cur.rightNodeIndex = newChild.index;
            XAABBTreeNodeDefault right = getNodeIndex(cur.rightNodeIndex);
            right.parentNodeIndex = cur.index;
        }
    }

    private XAABBTreeNodeDefault createNode() {
        XAABBTreeNodeDefault node;

        if(freeNodes.isEmpty()) {
            node = new XAABBTreeNodeDefault();
        }
        else {
            int index = freeNodes.removeIndex(0);
            node = getNodeIndex(index);
            node.onReset();
        }
        return node;
    }

    private void freeNode(XAABBTreeNodeDefault node) {
        freeNodes.add(node.index);
    }

    private XAABBTreeNodeDefault obtainNode() {
        XAABBTreeNodeDefault node = createNode();
        node.isActive = true;
        if(node.index == XAABBTreeNodeDefault.NULL_NODE) {
            nodes.add(node);
            node.index = nodes.size - 1;
        }
        return node;
    }

    private boolean releaseNode(XAABBTreeNodeDefault node) {
        int lastIndex = nodes.size - 1;
        int index = node.index;
        int parentIndex = node.parentNodeIndex;
        int leftIndex = node.leftNodeIndex;
        int rightIndex = node.rightNodeIndex;

        updateNewIndexNode(XAABBTreeNodeDefault.NULL_NODE, node, false);

        if(node.data != null) {
            int nodeID = node.dataId;
            idIndexMap.remove(nodeID, XAABBTreeNodeDefault.NULL_NODE);
        }
        node.onReset();

        freeNode(node);
        // send to pool

        return true;
    }

    private void updateNewIndexNode(int newIndex, XAABBTreeNodeDefault node, boolean updateNodeMapIndex) {
        int prevNodeIndex = node.index;

        boolean toNULLNodes = newIndex == XAABBTreeNodeDefault.NULL_NODE;

        if(node.parentNodeIndex != XAABBTreeNodeDefault.NULL_NODE) {
            // update parent node to the correct index
            XAABBTreeNodeDefault parentNode = getNodeIndex(node.parentNodeIndex);

            if(parentNode.leftNodeIndex == node.index) {
//                if (toNULLNodes)
                node.parentNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
                parentNode.leftNodeIndex = newIndex;
            }
            else if(parentNode.rightNodeIndex == node.index) {
//                if (toNULLNodes)
                node.parentNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
                parentNode.rightNodeIndex = newIndex;
            }
//            else {
//                throw new GdxRuntimeException("Wrong parent child index");
//            }
        }
        if(node.leftNodeIndex != XAABBTreeNodeDefault.NULL_NODE) {
            XAABBTreeNodeDefault leftNode = getNodeIndex(node.leftNodeIndex);
            if(leftNode.parentNodeIndex == node.index) {
                leftNode.parentNodeIndex = newIndex;
//                if (toNULLNodes)
                node.leftNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
            }
        }
        if(node.rightNodeIndex != XAABBTreeNodeDefault.NULL_NODE) {
            XAABBTreeNodeDefault rightNode = getNodeIndex(node.rightNodeIndex);
            if(rightNode.parentNodeIndex == node.index) {
                rightNode.parentNodeIndex = newIndex;
//                if (toNULLNodes)
                node.rightNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
            }
        }
        if(updateNodeMapIndex) {
            if(!toNULLNodes) {
                node.index = newIndex;
            }

            if(node.data != null) {
                int nodeID = node.dataId;
                idIndexMap.put(nodeID, newIndex);
            }
        }

        if(rootIndex == prevNodeIndex) {
            rootIndex = newIndex;
        }
    }
}