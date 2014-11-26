package jrtr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.vecmath.Matrix4f;

import jrtr.VertexData.Semantic;
import jrtr.scenemanager.ShapeNode;
import jrtr.swrenderer.SWVertexData;

import org.junit.Before;
import org.junit.Test;

public class ShapeNodeTest {

    private Frustum frustum;
    private Camera camera;
    private Matrix4f trafo;

    @Before
    public void setUp() {
        frustum = new Frustum();
        camera = new Camera();
        trafo = new Matrix4f();
        trafo.setIdentity();

    }

    @Test
    public void boundingSphereFront() {
        ShapeNode shape = createShapeNode(new float[] { 0, 0, 0, 0, 0, 1 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { 0, 0, 0, 0, 0, -1 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    @Test
    public void boundingSphereBack() {
        ShapeNode shape = createShapeNode(new float[] { 0, 0, 100, 0, 0, 101 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { 0, 0, 101, 0, 0, 102 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    @Test
    public void boundingSphereTop() {
        ShapeNode shape = createShapeNode(new float[] { 0, 6.7735F, 10, 0, 5.7735F, 10 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { 0, 6.7735F, 10, 0, 6F, 10 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    @Test
    public void boundingSphereBottom() {
        ShapeNode shape = createShapeNode(new float[] { 0, -6.7735F, 10, 0, -5.7735F, 10 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { 0, -6.7735F, 10, 0, -6F, 10 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    @Test
    public void boundingSphereLeft() {
        ShapeNode shape = createShapeNode(new float[] { -6.7735F, 0, 10, -5.7735F, 0, 10 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { -6.7735F, 0, 10, -6F, 0, 10 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    @Test
    public void boundingSphereRight() {
        ShapeNode shape = createShapeNode(new float[] { 6.7735F, 0, 10, 5.7735F, 0, 10 });
        assertTrue(shape.getShape().checkBoundingSphere(frustum, camera, trafo));

        shape = createShapeNode(new float[] { 6.7735F, 0, 10, 6F, 0, 10 });
        assertFalse(shape.getShape().checkBoundingSphere(frustum, camera, trafo));
    }

    private ShapeNode createShapeNode(float[] positions) {
        SWVertexData vertexData = new SWVertexData(3);
        vertexData.addElement(positions, Semantic.POSITION, positions.length / 3);
        Shape shape = new Shape(vertexData);
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return new ShapeNode(shape, m, "test");
    }
}
