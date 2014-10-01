package simple;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import jrtr.RenderContext;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

/**
 * @author Orlando Signer
 *
 */
public class Cylinder {
    private final int segments;
    private final float radius;
    private final float height;

    public Cylinder(int segments, float radius, float height) {
        this.segments = segments;
        this.radius = radius;
        this.height = height;
    }

    public VertexData createVertexData(RenderContext ctx) {
        double angle = (Math.PI * 2) / segments;
        double tmpX = 0;
        double tmpZ = radius;

        int segmentsTimesTwo = segments * 2;
        List<Point3d> vertices = new ArrayList<>(segmentsTimesTwo + 2);
        for (int i = 0; i < segments; i++) {
            tmpX = (float) Math.sin(i * angle) * radius;
            tmpZ = (float) Math.cos(i * angle) * radius;
            vertices.add(new Point3d(tmpX, height / -2, tmpZ));
            vertices.add(new Point3d(tmpX, height / 2, tmpZ));
        }
        // Add bottom and top
        vertices.add(new Point3d(0, height / -2, 0));
        vertices.add(new Point3d(0, height / 2, 0));

        List<Integer> indices = new ArrayList<>(segments * 3 * 3);
        for (int i = 0; i < segmentsTimesTwo; i++) {
            indices.add(i);
            indices.add((i + 1) % segmentsTimesTwo);
            indices.add((i + 2) % segmentsTimesTwo);
        }

        // indices for top and bottom
        for (int i = 0; i < segmentsTimesTwo; i += 2) {
            indices.add(i);
            indices.add((i + 2) % segmentsTimesTwo);
            indices.add(vertices.size() - 2);

            indices.add(i + 1);
            indices.add((i + 3) % segmentsTimesTwo);
            indices.add(vertices.size() - 1);
        }

        List<Color> colors = new ArrayList<>(segmentsTimesTwo + 2);
        for (int i = 0; i < segments; i++) {
            if (i % 2 == 0) {
                colors.add(Color.BLUE);
                colors.add(Color.BLUE);
            } else {
                colors.add(Color.WHITE);
                colors.add(Color.WHITE);

            }
        }
        colors.add(Color.WHITE);
        colors.add(Color.WHITE);

        // The vertex normals
        // TODO vertex normals and texture coordinates don't matter for
        // assignement 1
        float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
                -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
                0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
                0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
                0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

        // Texture coordinates
        float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1, //
                0, 0, 1, 0, 1, 1, 0, 1 };

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = ctx.makeVertexData(segmentsTimesTwo + 2);
        vertexData.addElement(Utils.pointsToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        // vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
        // vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));

        return vertexData;
    }
}
