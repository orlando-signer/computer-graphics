package model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2f;
import javax.vecmath.Point3d;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;
import jrtr.VertexData.Semantic;
import util.Utils;

import com.google.common.primitives.Ints;

public class BezierBody implements Model {

    private final int segments;
    private final List<Point3d> controlPoints;
    private final int evaluatePoints;
    private final int rotationSegments;
    private int pointsOnCurve;

    /**
     * Creates a new Bezier model.
     * <p>
     * The control points have to be on the xy plane (--> the z-value has to be
     * 0). The Bezier curve is then rotated around the y-axis.
     *
     * @param controlPoints
     * @param evaluatePoints
     * @param rotationSegments
     */
    public BezierBody(List<Point3d> controlPoints, int evaluatePoints, int rotationSegments) {
        this.controlPoints = controlPoints;
        this.evaluatePoints = evaluatePoints;
        this.rotationSegments = rotationSegments;
        segments = (controlPoints.size() - 1) / 3;
    }

    @Override
    public Shape createShape(RenderContext ctx) {
        List<Point3d> points = getBezierCurve();
        points = rotate(points);

        VertexData data = ctx.makeVertexData(segments * evaluatePoints * rotationSegments);
        data.addElement(Utils.tuple3dToArray(points), Semantic.POSITION, 3);
        data.addElement(getTexturCoords(), Semantic.TEXCOORD, 2);
        data.addIndices(getIndices());
        return new Shape(data);
    }

    private float[] getTexturCoords() {
        List<Point2f> coords = new ArrayList<>(pointsOnCurve * rotationSegments);
        for (int i = 1; i <= rotationSegments; i++) {
            for (int j = 1; j <= pointsOnCurve; j++) {
                coords.add(new Point2f(1F / i, 1F / j));
            }
        }
        return Utils.points2fToArray(coords);
    }

    private List<Point3d> rotate(List<Point3d> points) {
        float angle = (float) ((Math.PI * 2) / rotationSegments);
        List<Point3d> result = new ArrayList<>(points.size() * rotationSegments);
        float tmpU;
        for (int u = 0; u < rotationSegments; u++) {
            tmpU = angle * u;
            for (Point3d p : points)
                result.add(new Point3d(p.x * Math.cos(tmpU), p.y, p.x * Math.sin(tmpU)));
        }
        return result;
    }

    private int[] getIndices() {
        List<Integer> indices = new ArrayList<>();
        int total = rotationSegments * pointsOnCurve;
        for (int rot = 0; rot < rotationSegments; rot++) {
            int rotIndex = rot * pointsOnCurve;
            for (int p = 0; p < pointsOnCurve - 1; p++) {
                int i = rotIndex + p;
                indices.add(i);
                indices.add(i + 1);
                indices.add((i + pointsOnCurve) % total);

                indices.add(i);
                indices.add(i + 1);
                if (rot == 0)
                    indices.add(total - pointsOnCurve + p + 1);
                else
                    indices.add(i - pointsOnCurve + 1);
            }
        }
        return Ints.toArray(indices);
    }

    private List<Point3d> getBezierCurve() {
        List<Point3d> points = new ArrayList<>(evaluatePoints * segments);

        for (int i = 0; i < segments * 3; i += 3) {
            for (int evalPoint = 0; evalPoint < evaluatePoints; evalPoint++) {
                double t = ((double) evalPoint) / evaluatePoints;
                List<Point3d> ps = makeCopy(controlPoints.subList(i, i + 4));
                points.add(makeDeCasteljau(ps, t));
            }
        }
        pointsOnCurve = points.size();
        return points;
    }

    private List<Point3d> makeCopy(List<Point3d> points) {
        List<Point3d> copy = new ArrayList<>(points.size());
        for (Point3d p : points) {
            copy.add(new Point3d(p));
        }
        return copy;
    }

    private Point3d makeDeCasteljau(List<Point3d> points, double t) {
        if (points.size() == 1)
            return points.get(0);
        for (int i = 0; i < points.size() - 1; i++)
            points.get(i).interpolate(points.get(i + 1), t);
        points.remove(points.size() - 1);
        return makeDeCasteljau(points, t);
    }

}
