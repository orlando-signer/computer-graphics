package model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

/**
 * @author Orlando Signer
 *
 */
public class Torus implements Model {

    private final int radiusSegments;
    private final int ringRadiusSegments;

    private final float radius;
    private final float ringRadius;

    public Torus(int radiusSegments, int ringRadiusSegments, float radius, float ringRadius) {
        this.radiusSegments = radiusSegments;
        this.ringRadiusSegments = ringRadiusSegments;
        this.radius = radius;
        this.ringRadius = ringRadius;
    }

    @Override
    public Shape createShape(RenderContext ctx) {
        // Ignoring the center, this can later be done with translation
        float angle = (float) ((Math.PI * 2) / radiusSegments);
        float ringAngle = (float) ((Math.PI * 2) / ringRadiusSegments);

        float x = 0;
        float y = 0;
        float z = 0;
        float tmpU = 0;
        float tmpV = 0;
        int verticesCount = radiusSegments * ringRadiusSegments;
        List<Vector3d> vertices = new ArrayList<>(verticesCount);
        List<Point2f> textures = new ArrayList<>(verticesCount);
        List<Vector3d> normals = new ArrayList<>(verticesCount);
        for (int u = 0; u < radiusSegments; u++) {
            tmpU = angle * u;
            Vector3d center = new Vector3d(radius * Math.cos(u), radius * Math.sin(u), 0);
            Vector3d normal = new Vector3d();
            for (int v = 0; v < ringRadiusSegments; v++) {
                tmpV = ringAngle * v;
                x = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.cos(tmpU));
                y = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.sin(tmpU));
                z = (float) (ringRadius * Math.sin(tmpV));
                Vector3d pos = new Vector3d(x, y, z);
                vertices.add(pos);
                normal.sub(pos, center);
                normal.normalize();
                normals.add(normal);
                textures.add(new Point2f(((float) u) / radiusSegments, ((float) v) / ringRadiusSegments));
            }
        }

        List<Integer> indices = new ArrayList<>(verticesCount);
        for (int i = 0; i < radiusSegments; i++) {
            int ringIndex = (i * ringRadiusSegments);
            for (int j = 0; j < ringRadiusSegments; j++) {
                indices.add(ringIndex + j);
                indices.add((ringIndex + j + 1) % ringRadiusSegments + ringIndex);
                indices.add((ringIndex + j + ringRadiusSegments) % verticesCount);

                indices.add(ringIndex + j);
                indices.add((ringIndex + j + 1) % ringRadiusSegments + ringIndex);
                int tmp = ringIndex + j - ringRadiusSegments + 1;
                tmp = tmp <= 0 ? tmp + verticesCount : tmp;
                // Ugly, rethink logic for the last index point for each ring.
                if (j == ringRadiusSegments - 1)
                    tmp -= ringRadiusSegments;

                indices.add(tmp);
            }
        }
        // printIndices(vertices, indices);

        List<Color> colors = new ArrayList<>(vertices.size());
        for (int i = 0; i < vertices.size(); i++) {
            if (i % 4 == 0)
                colors.add(Color.RED);
            else
                colors.add(Color.WHITE);
        }

        VertexData vertexData = ctx.makeVertexData(verticesCount);
        vertexData.addElement(Utils.tuple3dToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.points2fToArray(textures), VertexData.Semantic.TEXCOORD, 2);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        vertexData.addElement(Utils.tuple3dToArray(normals), VertexData.Semantic.NORMAL, 3);
        vertexData.addIndices(Ints.toArray(indices));

        return new Shape(vertexData);
    }

    private void printIndices(List<Point3d> vertices, List<Integer> indices) {
        System.out.println(vertices.size());
        for (int i = 0; i < indices.size(); i++) {
            if (i % 6 == 0)
                System.out.print("[");
            System.out.print(indices.get(i) + ",");
            if (i % 6 == 2)
                System.out.print("  ");
            if (i % 6 == 5)
                System.out.println("]");
        }
    }
}
