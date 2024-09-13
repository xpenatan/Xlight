package xlight.engine.g3d.ecs.component.model;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import xlight.engine.g3d.XIDAttribute;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.g3d.model.XModelInstance;
import xlight.engine.list.XIntSet;

public class XAssetModel extends XBaseModel {

    public final static int MODEL_TYPE = 1;
    public final static String SHAPE_NAME = "Asset";

    private static final int DATAMAP_MODEL_PATH = 20;

    final public static int DIRTY_KEY_MODEL_PATH_CHANGED = 5;
    final public static int DIRTY_KEY_MODEL_IS_LOADING = 6;

    private String modelPath = "";

    private XModelInstance instance;

    public XAssetModel(int id, XIntSet componentDirty) {
        super(id, MODEL_TYPE, SHAPE_NAME, componentDirty);
    }

    @Override
    public void onInit() {
        // Not needed
    }

    public void onAssetLoaded(XEntity entity, Model model) {
        isParentDirty.put(XRender3DComponent.FLAG_CALCULATE_BOUNDING_BOX);

        if(model != null) {
            instance = new XModelInstance(model);
        }
        else {
            instance = null;
        }

        if(instance != null) {
            for(int i = 0; i < instance.materials.size; i++) {
                Material attributes = instance.materials.get(i);
                XIDAttribute selectIDAttribute = new XIDAttribute();
                selectIDAttribute.id = entity.getId();
                attributes.set(selectIDAttribute);
            }
        }
    }

    @Override
    public void calculateBoundingBox(BoundingBox out) {
        if(instance != null) {
            instance.calculateBoundingBox(out);
        }
    }

    @Override
    public void onRender(XBatch3D batch) {
        if(instance != null) {
            batch.drawModel(instance);
        }
    }

    @Override
    public void onTransform(Matrix4 transform) {
        if(instance != null) {
            instance.transform.set(transform);
        }
    }

    @Override
    protected void onDispose() {

    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
        isShapeDirty.put(DIRTY_KEY_MODEL_PATH_CHANGED);
    }
}