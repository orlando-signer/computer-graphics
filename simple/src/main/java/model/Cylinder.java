package model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import jrtr.RenderContext;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

/**
 * @author Orlando Signer
 *
 */
public class Cylinder implements Model {
    private final int segments;
    private final float radius;
    private final float height;

    public Cylinder(int segments, float radius, float height) {
        this.segments = segments;
        this.radius = radius;
        this.height = height;
    }

    @Override
    public jrtr.Shape createShape(RenderContext ctx) {
        double angle = (Math.PI * 2) / segments;
        double tmpX = 0;
        double tmpZ = radius;

        int segmentsTimesTwo = segments * 2;
        List<Point3d> vertices = new ArrayList<>(segmentsTimesTwo + 2);
        List<Vector3d> normals = new ArrayList<>(segmentsTimesTwo + 2);
        List<Point2f> textures = new ArrayList<>(segmentsTimesTwo);

        for (int i = 0; i < segments; i++) {
            tmpX = (float) Math.sin(i * angle) * radius;
            tmpZ = (float) Math.cos(i * angle) * radius;
            vertices.add(new Point3d(tmpX, 0, tmpZ));
            vertices.add(new Point3d(tmpX, height, tmpZ));
            Vector3d normal = new Vector3d(tmpX, 0, tmpZ);
            normal.normalize();
            normals.add(normal);
            normals.add(normal);
            textures.add(new Point2f((float) i / segments, 0));
            textures.add(new Point2f((float) i / segments, -1));

        }
        // Add bottom and top
        vertices.add(new Point3d(0, 0, 0));
        vertices.add(new Point3d(0, height, 0));
        normals.add(new Vector3d(0, -1, 0));
        normals.add(new Vector3d(0, 1, 0));
        textures.add(new Point2f(0.5F, 0.5F));
        textures.add(new Point2f(0.5F, 0.5F));

        List<Integer> indices = new ArrayList<>(segments * 3 * 3);
        for (int i = 0; i < segmentsTimesTwo; i++) {
            indices.add(i);
            indices.add((i + 1) % segmentsTimesTwo);
            indices.add((i + 2) % segmentsTimesTwo);
        }

        // indices for top and bottobm
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
                colors.add(Color.GRAY);
                colors.add(Color.GRAY);
            } else {
                colors.add(Color.GRAY);
                colors.add(Color.GRAY);

            }
        }
        colors.add(Color.GRAY);
        colors.add(Color.GRAY);

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = ctx.makeVertexData(segmentsTimesTwo + 2);
        vertexData.addElement(Utils.tuple3dToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        vertexData.addElement(Utils.tuple3dToArray(normals), VertexData.Semantic.NORMAL, 3);
        vertexData.addElement(Utils.points2fToArray(textures), VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));

        return new jrtr.Shape(vertexData);
    }
}
