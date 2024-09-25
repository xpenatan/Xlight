package xlight.engine.g3d.ecs.component;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.XIDAttribute;
import xlight.engine.g3d.XRender3D;
import xlight.engine.list.XIntSet;
import xlight.engine.math.XMath;
import xlight.engine.pool.XPoolable;
import xlight.engine.transform.XTransform;

public abstract class XRender3DComponent implements XComponent, XRender3D, XPoolable {

    final public static int FLAG_CALCULATE_BOUNDING_BOX = 2;

    public final XIntSet flags;

    private int entityId;

    public XRender3DComponent() {
        flags = new XIntSet();
        onReset();
    }

    @Override
    public final void onAttach(XWorld world, XEntity entity) {
        entityId = entity.getId();
        onComponentAttach(world, entity);
    }

    @Override
    public final void onDetach(XWorld world, XEntity entity) {
        onComponentDetach(world, entity);
    }

    protected void onComponentAttach(XWorld world, XEntity entity) {}
    protected void onComponentDetach(XWorld world, XEntity entity) {}

    public abstract void calculateBoundingBox(BoundingBox boundingBox);
    public abstract void onRender(XBatch3D batch);
    public abstract void onUpdate(Matrix4 transform);

    @Override
    public final void onRender(int engineType, XBatch3D batch) {
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

    protected final void updateModelInstance(ModelInstance instance) {
        for(int i = 0; i < instance.materials.size; i++) {
            Material attributes = instance.materials.get(i);
            XIDAttribute selectIDAttribute = new XIDAttribute();
            selectIDAttribute.id = entityId;
            attributes.set(selectIDAttribute);
        }
    }

    @Override
    public final Class<XRender3DComponent> getClassType() {
        return XRender3DComponent.class;
    }
}