package xlight.engine.aabb;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class XAABBDefault {

    private static final Vector3 VEC3_1 = new Vector3();
    private static final Vector3 VEC3_2 = new Vector3();

    private final static Vector3 tmpVector = new Vector3();

    public final Vector3 min = new Vector3();
    public final Vector3 max = new Vector3();

    public final Vector3 minReadOnly = new Vector3();
    public final Vector3 maxReadOnly = new Vector3();
    public final Vector3 center = new Vector3();
    public final Vector3 size = new Vector3();
    public final Vector3 extents = new Vector3();

    public float surfaceArea;

    public void set(XAABBDefault other) {
        this.min.set(other.min);
        this.max.set(other.max);
        this.center.set(other.center);
        this.size.set(other.size);
        this.extents.set(other.extents);
        this.surfaceArea = other.surfaceArea;
    }

    private void aabbconstruct(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        float centerX = 0.5f * (maxX + minX);
        float centerY = 0.5f * (maxY + minY);
        float centerZ = 0.5f * (maxZ + minZ);
        float sizeX = 2 * (maxX - centerX);
        float sizeY = 2 * (maxY - centerY);
        float sizeZ = 2 * (maxZ - centerZ);
        init(centerX, centerY, centerZ, sizeX, sizeY, sizeZ);
    }

    private void init(float centerX, float centerY, float centerZ, float sizeX, float sizeY, float sizeZ) {
        center.x = centerX;
        center.y = centerY;
        center.z = centerZ;
        size.x = sizeX;
        size.y = sizeY;
        size.z = sizeZ;

        extents.x = 0.5f * size.x;
        extents.y = 0.5f * size.y;
        extents.z = 0.5f * size.z;
        float minx = center.x - extents.x;
        float miny = center.y - extents.y;
        float minz = center.z - extents.z;
        float maxx = center.x + extents.x;
        float maxy = center.y + extents.y;
        float maxz = center.z + extents.z;
        setMinMax(minx, miny, minz, maxx, maxy, maxz);
    }

    Vector3 GetCenter() {
        return center;
    }

    Vector3 GetSize() {
        return size;
    }

    Vector3 GetExtents() {
        return extents;
    }

    Vector3 GetMin() {
        return min;
    }

    Vector3 GetMax() {
        return max;
    }

    public XAABBDefault set(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        aabbconstruct(minX, minY, minZ, maxX, maxY, maxZ);
        return this;
    }

    public void translate(float x, float y, float z) {
        set(min.x + x, min.y + y, min.z + z, max.x + x, max.y + y, max.z + z);
    }

    public XAABBDefault mul(Matrix4 transform) {
        final float x0 = min.x, y0 = min.y, z0 = min.z, x1 = max.x, y1 = max.y, z1 = max.z;
        reset();
        Vector3 min = VEC3_1;
        Vector3 max = VEC3_2;
        min.set(0, 0, 0);
        max.set(0, 0, 0);
        ext(min, max, tmpVector.set(x0, y0, z0).mul(transform));
        ext(min, max, tmpVector.set(x0, y0, z1).mul(transform));
        ext(min, max, tmpVector.set(x0, y1, z0).mul(transform));
        ext(min, max, tmpVector.set(x0, y1, z1).mul(transform));
        ext(min, max, tmpVector.set(x1, y0, z0).mul(transform));
        ext(min, max, tmpVector.set(x1, y0, z1).mul(transform));
        ext(min, max, tmpVector.set(x1, y1, z0).mul(transform));
        ext(min, max, tmpVector.set(x1, y1, z1).mul(transform));
        set(min.x, min.y, min.z, max.x, max.y, max.z);
        return this;
    }

    /**
     * Extends the bounding box to incorporate the given {@link Vector3}.
     *
     * @param point The vector
     * @return This bounding box for chaining.
     */
    public XAABBDefault ext(Vector3 min, Vector3 max, Vector3 point) {
        min.set(min(min.x, point.x), min(min.y, point.y), min(min.z, point.z));
        max.set(Math.max(max.x, point.x), Math.max(max.y, point.y), Math.max(max.z, point.z));
        return this;
    }

    public boolean overlaps(XAABBDefault other) {
        // y is deliberately first in the list of checks below as it is seen as more likely than things
        // collide on x,z but not on y than they do on y thus we drop out sooner on a y fail
        return max.x > other.min.x &&
                min.x < other.max.x &&
                max.y > other.min.y &&
                min.y < other.max.y &&
                max.z > other.min.z &&
                min.z < other.max.z;
    }

    public void reset() {
        set(0, 0, 0, 0, 0, 0);
        surfaceArea = 0;
    }

    public void expand(float amount) {
        size.x += amount * 1f;
        size.y += amount * 1f;
        size.z += amount * 1f;

        extents.x = 0.5f * size.x;
        extents.y = 0.5f * size.y;
        extents.z = 0.5f * size.z;

        float minx = center.x - extents.x;
        float miny = center.y - extents.y;
        float minz = center.z - extents.z;
        float maxx = center.x + extents.x;
        float maxy = center.y + extents.y;
        float maxz = center.z + extents.z;

        setMinMax(minx, miny, minz, maxx, maxy, maxz);
    }

    public void scale(float amount) {
        size.x *= amount;
        size.y *= amount;
        size.z *= amount;

        extents.x = 0.5f * size.x;
        extents.y = 0.5f * size.y;
        extents.z = 0.5f * size.z;

        float minx = center.x - extents.x;
        float miny = center.y - extents.y;
        float minz = center.z - extents.z;
        float maxx = center.x + extents.x;
        float maxy = center.y + extents.y;
        float maxz = center.z + extents.z;

        setMinMax(minx, miny, minz, maxx, maxy, maxz);
    }

    private void setMinMax(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        min.x = minX;
        min.y = minY;
        min.z = minZ;
        max.x = maxX;
        max.y = maxY;
        max.z = maxZ;
    }

    public boolean intersect(XAABBDefault a, XAABBDefault b) {
        return (a.min.x <= b.max.x && a.max.x >= b.min.x) &&
                (a.min.y <= b.max.y && a.max.y >= b.min.y) &&
                (a.min.z <= b.max.z && a.max.z >= b.min.z);
    }

    public boolean intersect(XAABBDefault other) {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
                (min.y <= other.max.y && max.y >= other.min.y) &&
                (min.z <= other.max.z && max.z >= other.min.z);
    }

    public boolean contains(Vector3 point) {
        return (point.x >= min.x && point.x <= max.x) &&
                (point.y >= min.y && point.y <= max.y) &&
                (point.z >= min.z && point.z <= max.z);
    }

    public boolean contains(XAABBDefault aabb) {
        return contains(aabb.min) && contains(aabb.max);
    }

    public float surfaceArea() {
        surfaceArea = 2 * (size.x * size.y + size.y * size.z + size.x * size.z);
        return surfaceArea;
    }

//    public void encapsulate(Vector3 point) {
//        min.x = Math.min(min.x, point.x);
//        min.y = Math.min(min.y, point.y);
//        min.z = Math.min(min.z, point.z);
//        max.x = Math.max(max.x, point.x);
//        max.y = Math.max(max.y, point.y);
//        max.z = Math.max(max.z, point.z);
//    }
//
//    void encapsulate(XpeAABB other) {
//        min.x = Math.min(min.x, other.min.x);
//        min.y = Math.min(min.y, other.min.y);
//        min.z = Math.min(min.z, other.min.z);
//        max.x = Math.max(max.x, other.max.x);
//        max.y = Math.max(max.y, other.max.y);
//        max.z = Math.max(max.z, other.max.z);
//    }

    public XAABBDefault encapsulate(XAABBDefault a, XAABBDefault b) {
        float minX = Math.min(a.min.x, b.min.x);
        float minY = Math.min(a.min.y, b.min.y);
        float minZ = Math.min(a.min.z, b.min.z);
        float maxX = Math.max(a.max.x, b.max.x);
        float maxY = Math.max(a.max.y, b.max.y);
        float maxZ = Math.max(a.max.z, b.max.z);
        aabbconstruct(minX, minY, minZ, maxX, maxY, maxZ);
        return this;
    }

    static final float min(final float a, final float b) {
        return a > b ? b : a;
    }

    static final float max(final float a, final float b) {
        return a > b ? a : b;
    }

    public Vector3 getMin() {
        return minReadOnly.set(min);
    }

    public Vector3 getMax() {
        return maxReadOnly.set(max);
    }
}