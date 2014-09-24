package simple;

import java.util.ArrayList;
import java.util.List;

import jrtr.RenderContext;
import jrtr.VertexData;

import com.google.common.primitives.Floats;

public class Cylinder {
    private final int segments;

    public Cylinder(int segments) {
        this.segments = segments;
    }

    public VertexData createVertexData(RenderContext ctx) {
        // Make a simple geometric object: a cylinder
        float radius = 1;
        float height = 1;
        float x = 0;
        float z = radius;
        double angle = (2 * Math.PI) / this.segments;

        List<Float> v = new ArrayList<Float>(this.segments * 2 * 3 + 2 * 3);
        for (int i = 0; i < this.segments; i++) {
            v.add(x);
            v.add(0F);
            v.add(z);
            v.add(x);
            v.add(height);
            v.add(z);
            x = (float) (Math.cos(angle) * x - Math.sin(angle) * z);
            z = (float) (Math.sin(angle) * x + Math.cos(angle) * z);
        }

        v.add(0F);
        v.add(0F);
        v.add(0F);
        v.add(0F);
        v.add(height);
        v.add(0F);

        // // The vertex positions of the cylinder
        // float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, // front
        // face
        // -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
        // 1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
        // 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
        // 1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
        // -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face

        // The vertex normals
        float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
                -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
                0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
                0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
                0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

        // The vertex colors
        float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // <
                0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
                0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, //
                0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

        // Texture coordinates
        float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1 };

        // The triangles (three vertex indices for each triangle)
        int indices[] = { 0, 2, 3, 0, 1, 2, // front face
                4, 6, 7, 4, 5, 6, // left face
                8, 10, 11, 8, 9, 10, // back face
                12, 14, 15, 12, 13, 14, // right face
                16, 18, 19, 16, 17, 18, // top face
                20, 22, 23, 20, 21, 22 }; // bottom face

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = ctx.makeVertexData(this.segments * 2 + 2);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
        vertexData.addElement(Floats.toArray(v), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
        vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(indices);

        return vertexData;
    }
}
