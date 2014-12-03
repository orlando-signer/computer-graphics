package model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;

import jrtr.RenderContext;
import jrtr.Shape;

public class BezierBody implements Model {

    private final int segments;
    private final List<Point3f> controlPoints;
    private final int evaluatePoints;
    private final int rotationSegments;

    private BezierBody(List<Point3f> controlPoints, int evaluatePoints, int rotationSegments) {
        this.controlPoints = controlPoints;
        this.evaluatePoints = evaluatePoints;
        this.rotationSegments = rotationSegments;
        segments = (controlPoints.size() - 1) / 3;
        init();
    }

    private void init() {
        List<Point3f> points = getBezierPoints();
    }

    private List<Point3f> getBezierPoints() {
        List<Point3f> points = new ArrayList<>(evaluatePoints * segments);
        for (int i = 0; i < segments; i += 3) {
            for (int evalPoint = 1; evalPoint < evaluatePoints; evalPoint++) {
                float t = ((float) evalPoint) / evaluatePoints;
                List<Point3f> ps = new ArrayList<>(controlPoints.subList(i, i + 4));
                makeDeCasteljau(ps, t);
            }
        }
        return points;
    }

    private Point3f makeDeCasteljau(List<Point3f> points, float t) {
        if (points.size() == 1)
            return points.get(0);
        for (int i = 0; i < points.size() - 1; i++)
            points.get(i).interpolate(points.get(i + 1), t);
        points.remove(points.size() - 1);
        return makeDeCasteljau(points, t);
    }

    @Override
    public Shape createShape(RenderContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

}
