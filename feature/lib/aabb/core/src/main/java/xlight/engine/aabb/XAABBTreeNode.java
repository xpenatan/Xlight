package xlight.engine.aabb;

import com.badlogic.gdx.math.Vector3;

public interface XAABBTreeNode {
    int getId();

    boolean isLeaf();

    /**
     * Read only min data.
     */
    Vector3 getMin();
    /**
     * Read only max data.
     */
    Vector3 getMax();

    /**
     * Read only min data.
     */
    Vector3 getFatMin();
    /**
     * Read only max data.
     */
    Vector3 getFatMax();

}
