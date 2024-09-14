package xlight.engine.g3d.model;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class XMeshData {
    public Matrix4 modelTransform;
    public Node parentNode;
    public NodePart nodePart;
    private final Array<Vector3> vertices = new Array<>();

    public Array<Vector3> getVertices() {
        return vertices;
    }

    public void createVertices() {
        vertices.clear();

        if(nodePart == null) {
            return;
        }
        MeshPart meshPart = nodePart.meshPart;
        Mesh mesh = meshPart.mesh;
        int offset = meshPart.offset;
        int size = meshPart.size;

        int numIndices = mesh.getNumIndices();

        boolean isIndexOffset = numIndices > 0;

        int numVertices = mesh.getNumVertices();
        int vertexSize = mesh.getVertexSize();
        int vertexSize2 = vertexSize / 4;
        int vertsTotalSize = numVertices * vertexSize;
        float[] verts = new float[vertsTotalSize];

        short[] inds = null;

        inds = new short[numIndices];
        mesh.getIndices(inds);
        mesh.getVertices(verts);

        int newSize = offset + size;

        for(int i = offset; i < newSize; i++) {
            int i1 = inds[i] * vertexSize2;
            float x = verts[i1];
            float y = verts[i1 + 1];
            float z = verts[i1 + 2];
            Vector3 v = new Vector3(x, y, z);
            vertices.add(v);
        }
    }

    public void getWorldTransform(final Matrix4 out) {
        out.idt();
        Matrix4 transform = modelTransform;
        if(nodePart.bones == null && transform != null)
            out.set(transform).mul(parentNode.globalTransform);
        else if(transform != null)
            out.set(transform);
        else
            out.idt();
    }
}