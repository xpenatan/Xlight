package xlight.engine.aabb;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.pool.XPoolable;
import xlight.engine.transform.XTransform;

public class XAABBTreeNodeDefault implements XAABBTreeNode, XPoolable {

    public static final int NULL_NODE = -1;

    public int index = XAABBTreeNodeDefault.NULL_NODE;
    public int parentNodeIndex;
    public int leftNodeIndex;
    public int rightNodeIndex;
    public boolean isActive;
    public int dataId;
    public XTransform data;

    public boolean debugHighlight;

    public final XAABBDefault aabb;
    public final XAABBDefault fatAABB;

    public XAABBTreeNodeDefault() {
        aabb = new XAABBDefault();
        fatAABB = new XAABBDefault();
        onReset();
    }

    @Override
    public int getId() {
        return dataId;
    }

    @Override
    public boolean isLeaf() {
        return leftNodeIndex == NULL_NODE;
    }

    @Override
    public Vector3 getMin() {
        return aabb.getMin();
    }

    @Override
    public Vector3 getMax() {
        return aabb.getMax();
    }

    @Override
    public Vector3 getFatMin() {
        return fatAABB.getMin();
    }

    @Override
    public Vector3 getFatMax() {
        return fatAABB.getMax();
    }

    @Override
    public boolean isDebug() {
        return debugHighlight;
    }

    public void updatePosition() {
        if(data != null) {
            BoundingBox boundingBox = data.getBoundingBox();
            aabb.set(boundingBox.min.x, boundingBox.min.y, boundingBox.min.z,
                    boundingBox.max.x, boundingBox.max.y, boundingBox.max.z);
        }
    }

    public void updateFatAABB(float fatFactor) {
        fatAABB.set(aabb);
        fatAABB.expand(1f + fatFactor);
//            //TODO need to improve fatAABB
//            fatAABB.scale(2.0f);
    }

    public boolean IsInFatAABB() {
        return fatAABB.contains(aabb);
    }

    @Override
    public void onReset() {
        parentNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
        leftNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
        rightNodeIndex = XAABBTreeNodeDefault.NULL_NODE;
        aabb.reset();
        fatAABB.reset();
        data = null;
        dataId = -1;
        isActive = false;
    }
}