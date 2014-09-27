package simple;

import java.util.ArrayList;
import java.util.List;

import jrtr.RenderContext;
import jrtr.VertexData;
import util.Color;
import util.Point;
import util.Utils;

import com.google.common.primitives.Ints;

public class Torus implements Shape {

    private final int radiusSegments;
    private final int ringRadiusSegments;

    private final Point center;
    private final float radius;
    private final float ringRadius;

    public Torus(int radiusSegments, int ringRadiusSegments, float radius, float ringRadius) {
        this(radiusSegments, ringRadiusSegments, radius, ringRadius, Point.ZERO);
    }

    public Torus(int radiusSegments, int ringRadiusSegments, float radius, float ringRadius, Point center) {
        this.radiusSegments = radiusSegments;
        this.ringRadiusSegments = ringRadiusSegments;
        this.center = center;
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
        List<Point> vertices = new ArrayList<>(radiusSegments * ringRadiusSegments);
        for (int u = 0; u < radiusSegments; u++) {
            for (int v = 0; v < ringRadiusSegments; v++) {
                tmpU = angle * u;
                tmpV = ringAngle * v;
                x = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.cos(tmpU));
                y = (float) ((radius + ringRadius * Math.cos(tmpV)) * Math.sin(tmpU));
                z = (float) (ringRadius * Math.sin(tmpV));
                vertices.add(new Point(x, y, z));
            }

        }
        // Indices
        // 0,1,5
        System.out.println(vertices);
        List<Integer> indices = new ArrayList<>(radiusSegments * ringRadiusSegments);
        for (int i = 0; i < radiusSegments; i++) {
            // TODO add some modulo to go from last ring to first
            int ringIndex = (i * ringRadiusSegments);
            for (int j = 0; j < ringRadiusSegments; j++) {
                // TODO add vertex-positions from the other half of triangles
                indices.add(ringIndex + j);
                indices.add(ringIndex + j + 1);
                indices.add(ringIndex + j + ringRadiusSegments);
            }
        }
        
        List<Color> colors= new ArrayList<>();
        for(int i = 0;i<vertices.size();i++)
            colors.add(Color.RED);

        VertexData vertexData = ctx.makeVertexData(radiusSegments* ringRadiusSegments);
        vertexData.addElement(Utils.pointsToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        // vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
        // vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));
        
        return vertexData;
    }
}
