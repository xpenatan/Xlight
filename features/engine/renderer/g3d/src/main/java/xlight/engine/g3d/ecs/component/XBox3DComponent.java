package xlight.engine.g3d.ecs.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import xlight.engine.g3d.XBatch3D;
import xlight.engine.g3d.model.XModelInstance;
import xlight.engine.math.XMath;

public class XBox3DComponent extends XRender3DComponent {

    private Model model;
    private XModelInstance modelInstance;
    private final Vector3 size = new Vector3(1, 1, 1);

    public XBox3DComponent() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        Material material = new Material("material",
                PBRColorAttribute.createBaseColorFactor(XMath.COLOR_1.set(1f, 1f, 1f, 1.0f)),
                PBRColorAttribute.createDiffuse(1f, 0f, 0f, 1.0f),
                PBRColorAttribute.createSpecular(Color.WHITE));
        MeshPartBuilder meshBuilder = mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        BoxShapeBuilder.build(meshBuilder, size.x, size.y, size.z);
        model = mb.end();
        modelInstance = new XModelInstance(model);
    }


    @Override
    public void calculateBoundingBox(BoundingBox boundingBox) {
        modelInstance.calculateBoundingBox(boundingBox);
    }

    @Override
    public void onRender(XBatch3D batch) {
        batch.drawModel(modelInstance);
    }

    @Override
    public void onUpdate(Matrix4 transform) {
        modelInstance.transform.set(transform);
    }

    @Override
    public void onReset() {
        super.onReset();
        if(model != null) {
            model.dispose();
            model = null;
        }
    }
}