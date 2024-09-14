package xlight.engine.g3d.ecs.component.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.list.XIntSet;
import xlight.engine.transform.XTransform;

public abstract class XBaseModel {

    final public static int DIRTY_KEY_MODEL_INIT = 1;

    private int modelType;
    private int id;
    private String modelName;
    public XIntSet isShapeDirty;
    protected XIntSet isParentDirty;
    private final XTransform localTransform;

    public XBaseModel(int id, int modelType, String shapeName, XIntSet isParentDirty) {
        this.id = id;
        this.modelType = modelType;
        this.modelName = shapeName + ": " + id;
        this.isParentDirty = isParentDirty;
        localTransform = XTransform.newInstance();
        isShapeDirty = new XIntSet();
        isShapeDirty.put(DIRTY_KEY_MODEL_INIT);
    }

    public int getModelType() {
        return modelType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return modelName;
    }

    public XTransform getLocalTransform() {
        return localTransform;
    }

    public void dispose() {
        localTransform.reset();
        onDispose();
    }

    public abstract void onInit();
    protected abstract void onDispose();
    public abstract void calculateBoundingBox(final BoundingBox out);
    public abstract void onRender(XBatch3D batch);
    public abstract void onTransform(Matrix4 transform);
}
