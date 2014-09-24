package simple;

import java.util.List;

import jrtr.RenderContext;
import jrtr.VertexData;
import util.Color;
import util.Point;
import util.Utils;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

public class Cylinder {
    private final int segments;

    public Cylinder(int segments) {
        this.segments = segments;
    }

    public VertexData createVertexData(RenderContext ctx) {
        // Make a simple geometric object: a cylinder
        float radius = 1;
        float height = 2;
        float x = 0;
        float z = radius;
        double angle = (Math.PI * 2) / segments;

        float tmpX = x;
        float tmpZ = radius;

        List<Point> vertices = Lists.newArrayList();
        for (int i = 0; i < segments; i++) {
            tmpX = (float) Math.sin(i * angle);
            tmpZ = (float) Math.cos(i * angle);
            vertices.add(new Point(tmpX, 0F, tmpZ));
            vertices.add(new Point(tmpX, height, tmpZ));
        }
        vertices.add(new Point(0, 0, 0));
        vertices.add(new Point(0, height, 0));

        List<Integer> indices = Lists.newArrayList();
        for (int i = 0; i < segments * 2; i++) {
            indices.add(i);
            indices.add((i + 1) % (segments * 2));
            indices.add((i + 2) % (segments * 2));
        }

        List<Color> colors = Lists.newArrayList();
        for (int i = 0; i < segments; i++) {
            colors.add(Color.BLUE);
            colors.add(Color.WHITE);
        }
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);

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
        float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
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
        // int indices[] = { 0, 2, 3, 0, 1, 2, // front face
        // 4, 6, 7, 4, 5, 6, // left face
        // 8, 10, 11, 8, 9, 10, // back face
        // 12, 14, 15, 12, 13, 14, // right face
        // 16, 18, 19, 16, 17, 18, // top face
        // 20, 22, 23, 20, 21, 22 }; // bottom face

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = ctx.makeVertexData(segments * 2 + 2);
        vertexData.addElement(Utils.pointsToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        // vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
        // vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));

        return vertexData;
    }
}
