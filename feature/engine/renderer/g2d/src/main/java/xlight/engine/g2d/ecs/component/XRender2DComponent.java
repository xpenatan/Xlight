package xlight.engine.g2d.ecs.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.g2d.XBatch2D;
import xlight.engine.g2d.XRender2D;
import xlight.engine.list.XIntSet;
import xlight.engine.math.XMath;
import xlight.engine.pool.XPoolable;
import xlight.engine.transform.XTransform;

public abstract class XRender2DComponent implements XComponent, XRender2D, XPoolable {

    final public static int FLAG_CALCULATE_BOUNDING_BOX = 2;

    public final XIntSet flags;

    public XRender2DComponent() {
        flags = new XIntSet();
        onReset();
    }

    public abstract void calculateBoundingBox(BoundingBox boundingBox);
    public abstract void onRender(XBatch2D batch);
    public abstract void onUpdate(Matrix4 transform);

    @Override
    public final void onRender(int engineType, XBatch2D batch) {
        onRender(batch);
    }

    @Override
    public final void onUpdate(XTransform transform) {
        Matrix4 transformMatrix4 = transform.getMatrix4();
        if(flags.remove(FLAG_CALCULATE_BOUNDING_BOX)) {
            XMath.BOUNDING_BOX_1.clr();
            calculateBoundingBox(XMath.BOUNDING_BOX_1);
            transform.setLocalBoundingBox(XMath.BOUNDING_BOX_1);
        }
        onUpdate(transformMatrix4);
    }

    @Override
    public void onReset() {
        flags.clear();
    }
}