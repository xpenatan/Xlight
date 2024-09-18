package xlight.engine.g3d.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;

public class XShapeModelHelper {

    public static Model createArrowUpAxis(float r, float g, float b, float axisWidth) {
        ModelBuilder mb = new ModelBuilder();
        Material material = null;

        float offset = 0.1f;

        mb.begin();
        Node nodeConeAxisX = mb.node();
        nodeConeAxisX.id = "coneAxisX";
        nodeConeAxisX.translation.y = offset + axisWidth + 0.15f;

        nodeConeAxisX.calculateLocalTransform();
        nodeConeAxisX.calculateWorldTransform();
        material = new Material("coneAxisX",
                ColorAttribute.createDiffuse(r, g, b, 0.8f),
                PBRColorAttribute.createBaseColorFactor(new Color(r, g, b, 1.0f))
        );
        BlendingAttribute blending = new BlendingAttribute();
        blending.blended = false;
        material.set(blending);
        MeshPartBuilder mesBuilderConeX = mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        ConeShapeBuilder.build(mesBuilderConeX, 0.2f, 0.3f, 0.2f, 10);

        Node cylinderX = mb.node();
        cylinderX.id = "cylinderX";
        cylinderX.translation.y = offset + (axisWidth / 2f + 0.011f);
        cylinderX.calculateLocalTransform();
        cylinderX.calculateWorldTransform();
        MeshPartBuilder meshBuilderCylinderX = mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                material);

        CylinderShapeBuilder.build(meshBuilderCylinderX, 0.05f, axisWidth - 0.022f, 0.05f, 5);

        return mb.end();
    }

    public static Model createPositionGizmo(float axisWidth, boolean createBox) {
        Model axisXModel = XShapeModelHelper.createArrowUpAxis(1f, 0f, 0f, axisWidth);
        Model axisYModel = XShapeModelHelper.createArrowUpAxis(0f, 1f, 0f, axisWidth);
        Model axisZModel = XShapeModelHelper.createArrowUpAxis(0f, 0f, 1f, axisWidth);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        Node axisX = mb.node("axisX", axisXModel);
        Node axisY = mb.node("axisY", axisYModel);
        Node axisZ = mb.node("axisZ", axisZModel);
        axisX.rotation.set(Vector3.Z, -90);
        axisZ.rotation.set(Vector3.X, 90);

        if(createBox) {
            Model modelYZ = createBoxXY(1f, 0f, 0f);
            Node axisYZ = mb.node("axisYZ", modelYZ);
            axisYZ.rotation.set(Vector3.Y, -90);

            Model modelXZ = createBoxXY(0f, 1f, 0f);
            Node axisXZ = mb.node("axisXZ", modelXZ);
            axisXZ.rotation.set(Vector3.X, 90);

            Model modelXY = createBoxXY(0f, 0f, 1f);
            Node axisXY = mb.node("axisXY", modelXY);
        }

        return mb.end();
    }

    public static Model createBoxXY(float r, float g, float b) {
        ModelBuilder mb = new ModelBuilder();
        Material material = null;

        mb.begin();

        float size = 0.3f;
        float width = 0.02f;
        float offset = 0.1f;

        Node node = mb.node();
        node.id = "axisXY";
        material = new Material("axisXY", ColorAttribute.createDiffuse(r, g, b, 0.8f));
        BlendingAttribute blending = new BlendingAttribute();
        material.set(blending);
        MeshPartBuilder meshBuilderXY = mb.part("axisXY", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        BoxShapeBuilder.build(meshBuilderXY, new Vector3(0, 0, 0), new Vector3(0, size, 0), new Vector3(size, 0, 0),
                new Vector3(size, size, 0), new Vector3(0, 0, width), new Vector3(0, size, width),
                new Vector3(size, 0, width), new Vector3(size, size, width));
        node.translation.x = offset;
        node.translation.y = offset;
        return mb.end();
    }
}