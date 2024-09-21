package xlight.engine.aabb;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import xlight.engine.transform.XTransform;

public interface XAABBTree {

    boolean addAABB(int id, XTransform transform);
    boolean removeAABB(int id);
    boolean containsAABB(int id);
    void update();

    void rayCast(Ray ray, Array<XAABBTreeNode> out);
    void rayCast(Ray ray, Array<XAABBTreeNode> out, boolean sort, float maxDistance, int total, boolean debug);
    XAABBTreeNode getRoot();
}