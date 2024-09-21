package xlight.engine.g3d.model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import xlight.engine.math.XMath;
import xlight.engine.glutils.XShapeRenderer;

public class XModelInstance extends ModelInstanceHack {

    private static Vector3 VEC3_1 = new Vector3();
    private static Vector3 VEC3_2 = new Vector3();
    private static Vector3 VEC3_3 = new Vector3();

    public Array<XMeshData> meshDataList = new Array<>();

    public XModelInstance(Model model) {
        super(model);
        invalidateMaterials();
    }

    public void initMeshDataCache() {
        for(Node node : nodes) {
            getRend(node);
        }
    }

    protected void getRend(Node node) {
        if(node.parts.size > 0) {
            for(NodePart nodePart : node.parts) {
                XMeshData meshData = new XMeshData();
                meshData.nodePart = nodePart;
                meshData.parentNode = node;
                meshData.modelTransform = transform;
                meshData.createVertices();
                meshDataList.add(meshData);
            }
        }

        for(Node child : node.getChildren()) {
            getRend(child);
        }
    }

    public XMeshData rayCast(Camera camera, float screenX, float screenY, Vector3 intersection) {
        return rayCast(null, camera, screenX, screenY, intersection);
    }

    public XMeshData rayCast(XShapeRenderer renderer, Camera camera, float screenX, float screenY, Vector3 intersection) {
        Ray ray = camera.getPickRay(screenX, screenY);
        return rayCast(renderer, ray, intersection);
    }

    public XMeshData rayCast(XShapeRenderer renderer, Ray ray, Vector3 intersection) {
        XModelInstance instance = this;
        instance.calculateTransforms();
        XMeshData found = null;
        BoundingBox tmpBoundingBox = XMath.BOUNDING_BOX_1;

        instance.calculateBoundingBox(tmpBoundingBox);
        tmpBoundingBox.mul(instance.transform);

        if(renderer != null) {
            renderer.boundingBox(tmpBoundingBox);
        }
        if(Intersector.intersectRayBoundsFast(ray, tmpBoundingBox)) {
            Array<XMeshData> meshDataList = instance.meshDataList;
            for(int i = 0; i < meshDataList.size; i++) {
                XMeshData meshData = meshDataList.get(i);
                meshData.parentNode.calculateBoundingBox(tmpBoundingBox);

                Matrix4 worldTransform = XMath.MAT4_1;
                meshData.getWorldTransform(worldTransform);
                tmpBoundingBox.mul(instance.transform);

                if(renderer != null) {
                    renderer.boundingBox(tmpBoundingBox);
                }

                if(Intersector.intersectRayBoundsFast(ray, tmpBoundingBox)) {
                    Array<Vector3> vertices = meshData.getVertices();

                    for(int j = 0; j < vertices.size - 3; j += 3) {
                        Vector3 v1 = vertices.get(j);
                        Vector3 v2 = vertices.get(j + 1);
                        Vector3 v3 = vertices.get(j + 2);

                        Vector3 v1Temp = VEC3_1;
                        Vector3 v2Temp = VEC3_2;
                        Vector3 v3Temp = VEC3_3;

                        v1Temp.set(v1);
                        v2Temp.set(v2);
                        v3Temp.set(v3);

                        v1Temp.prj(worldTransform);
                        v2Temp.prj(worldTransform);
                        v3Temp.prj(worldTransform);

                        if(renderer != null) {
                            renderer.triangle(v1Temp, v2Temp, v3Temp);
                        }

                        if(found == null && XMath.intersectRayTriangle(ray, v1Temp, v2Temp, v3Temp) == 1) {
                            //pos now contains the correct coordinates
                            found = meshData;
                            if(renderer == null) {
                                return found;
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    @Override
    public BoundingBox calculateBoundingBox(final BoundingBox out) {
        out.inf();
        extendBoundingBox(out);
        return out;
    }

    @Override
    public BoundingBox extendBoundingBox(final BoundingBox out) {
        final int n = nodes.size;
        for(int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            extendBoundingBox(node, out);
        }
        return out;
    }

    private void extendBoundingBox(Node node, final BoundingBox out) {
        final int partCount = node.parts.size;
        for(int i = 0; i < partCount; i++) {
            final NodePart part = node.parts.get(i);
            // When there is no Armature blender export as a local vertices and when Armature is used blender export as global vertices.
            // We check if bone exists to set transform flag.
            // TODO not sure if it will work in all cases
            boolean transform = part.invBoneBindTransforms == null;
            if(part.enabled) {
                final MeshPart meshPart = part.meshPart;
                if(transform)
                    meshPart.mesh.extendBoundingBox(out, meshPart.offset, meshPart.size, node.globalTransform);
                else
                    meshPart.mesh.extendBoundingBox(out, meshPart.offset, meshPart.size);
            }
        }
        Iterable<Node> children = node.getChildren();

        for(Node child : children) {
            extendBoundingBox(child, out);
        }
    }

    public Renderable getRenderable(final Renderable out, final Node node, final NodePart nodePart) {
        nodePart.setRenderable(out);
        if(nodePart.bones == null && transform != null)
            out.worldTransform.set(transform).mul(node.globalTransform);
        else if(transform != null)
            out.worldTransform.set(transform);
        else
            out.worldTransform.idt();
        out.userData = userData;
        return out;
    }

    // Create XpeMaterial

    /**
     * Makes sure that each {@link NodePart} of the {@link Node} and its sub-nodes, doesn't reference a node outside this node
     * tree and that all materials are listed in the {@link #materials} array.
     */
    private void invalidateMaterials(Node node) {
        for(int i = 0, n = node.parts.size; i < n; ++i) {
            NodePart part = node.parts.get(i);
            if(!materials.contains(part.material, true)) {
                final int midx = materials.indexOf(part.material, false);
                if(midx < 0) {
                    // This will replace libgdx material with XpeMaterial
                    materials.add(part.material = new XMaterial(part.material));
                }
                else {
                    part.material = materials.get(midx);
                }
            }
        }
        for(int i = 0, n = node.getChildCount(); i < n; ++i) {
            invalidateMaterials(node.getChild(i));
        }
    }

    /**
     * Makes sure that each {@link NodePart} of each {@link Node} doesn't reference a node outside this node tree and that all
     * materials are listed in the {@link #materials} array.
     */
    private void invalidateMaterials() {
        materials.clear();
        for(int i = 0, n = nodes.size; i < n; ++i) {
            invalidateMaterials(nodes.get(i));
        }
    }
}