package jrtr;

import static org.junit.Assert.assertTrue;

import javax.vecmath.Point3f;

import jrtr.VertexData.Semantic;
import jrtr.swrenderer.SWVertexData;

import org.junit.Test;

public class ShapeTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void boundingSpherePos() {
        Shape shape = createShape(new float[] { 0, 0, 0, 1, 1, 1 });
        assertEquals(new Point3f(0.5F, 0.5F, 0.5F), shape.getCenter());
        assertEquals(0.8660254F, shape.getRadius());
    }

    @Test
    public void boundingSphereNeg() {
        Shape shape = createShape(new float[] { 0, 0, 0, -1, -1, -1 });
        assertEquals(new Point3f(-0.5F, -0.5F, -0.5F), shape.getCenter());
        assertEquals(0.8660254F, shape.getRadius());
    }

    @Test
    public void boundingSpherePosNeg() {
        Shape shape = createShape(new float[] { 1, 1, 1, -1, -1, -1 });
        assertEquals(new Point3f(0.0F, 0.0F, 0.0F), shape.getCenter());
        assertEquals(1.7320508F, shape.getRadius());
    }

    private Shape createShape(float[] positions) {
        SWVertexData vertexData = new SWVertexData(3);
        vertexData.addElement(positions, Semantic.POSITION, positions.length / 3);
        Shape shape = new Shape(vertexData);
        return shape;
    }

    private void assertEquals(Point3f expected, Point3f actual) {
        assertTrue("x-Value out of range: " + expected.x + "; was " + actual.x, expected.x - actual.x < EPSILON);
        assertTrue("y-Value out of range: " + expected.y + "; was " + actual.y, expected.y - actual.y < 0.0001F);
        assertTrue("z-Value out of range: " + expected.z + "; was " + actual.z, expected.z - actual.z < 0.0001F);
    }

    private void assertEquals(float expected, float actual) {
        assertTrue("radiuis out of range: " + expected + "; was " + actual, Math.abs(expected - actual) < EPSILON);
    }
}
