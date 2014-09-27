package simple;

import java.util.ArrayList;
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
    private final float radius;
    private final float height;
    private final Point center;

    public Cylinder(int segments, float radius, float height) {
        this(segments, radius, height, Point.ZERO);
    }

    /**
     * @param segments
     * @param radius
     * @param height
     * @param center
     *            the position of the center of the bottom of the cylinder.
     */
    public Cylinder(int segments, float radius, float height, Point center) {
        this.segments = segments;
        this.radius = radius;
        this.height = height;
        this.center = center;
    }

    public VertexData createVertexData(RenderContext ctx) {
        double angle = (Math.PI * 2) / segments;
        float tmpX = center.getX();
        float tmpZ = center.getZ() + radius;

        int segmentsTimesTwo = segments * 2;
        List<Point> vertices = new ArrayList<>(segmentsTimesTwo + 2);
        for (int i = 0; i < segments; i++) {
            tmpX = center.getX() + (float) Math.sin(i * angle) * radius;
            tmpZ = center.getZ() + (float) Math.cos(i * angle) * radius;
            vertices.add(new Point(tmpX, center.getY(), tmpZ));
            vertices.add(new Point(tmpX, center.getY() + height, tmpZ));
        }
        // Add bottom and top
        vertices.add(center);
        vertices.add(center.add(0, height, 0));

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
