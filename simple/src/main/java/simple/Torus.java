package simple;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import jrtr.RenderContext;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

public class Torus implements Shape {

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
    public VertexData createVertexData(RenderContext ctx) {
        // Ignoring the center, this can later be done with translation
        float angle = (float) ((Math.PI * 2) / radiusSegments);
        float ringAngle = (float) ((Math.PI * 2) / ringRadiusSegments);

        float x = 0;
        float y = 0;
        float z = 0;
        float tmpU = 0;
        float tmpV = 0;
        int verticesCount = radiusSegments * ringRadiusSegments;
        List<Point3d> vertices = new ArrayList<>(verticesCount);
        for (int u = 0; u < radiusSegments; u++) {
            for (int v = 0; v < ringRadiusSegments; v++) {
                tmpU = angle * u;
                tmpV = ringAngle * v;
                x = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.cos(tmpU));
                y = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.sin(tmpU));
                z = (float) (ringRadius * Math.sin(tmpV));
                vertices.add(new Point3d(x, y, z));
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
        vertexData.addElement(Utils.pointsToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        vertexData.addIndices(Ints.toArray(indices));

        return vertexData;
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
