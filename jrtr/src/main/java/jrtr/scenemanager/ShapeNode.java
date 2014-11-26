package jrtr.scenemanager;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import jrtr.Camera;
import jrtr.Frustum;
import jrtr.Plane;
import jrtr.Shape;

public class ShapeNode extends Leaf {

    // The position relative to its parentb
    private Matrix4f position;
    private final Shape item;
    private String name;

    private static Map<String, Integer> counts = new HashMap<>();

    public ShapeNode(Shape shape, Matrix4f position, String name) {
        item = shape;
        this.position = position;
        this.name = name;
        counts.put(this.name, 0);
    }

    @Override
    public Matrix4f getTransformation() {
        return position;
    }

    @Override
    public void setTransformation(Matrix4f t) {
        position = t;
    }

    @Override
    public Shape getShape() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public NodeType getType() {
        return NodeType.LEAF;
    }

    /**
     * Checks if the Bounding sphere of this Shape is inside the frustum.
     *
     * @param frustum
     * @param camera
     * @param trafo
     * @return true if bounding sphere is inside (--> draw Shape)
     */
    public boolean checkBoundingSphere(Frustum frustum, Camera camera, Matrix4f trafo) {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        m.mul(camera.getCameraMatrix());
        m.mul(trafo);
        m.mul(position);
        // m.mul(camera.getCameraMatrix(), trafo);
        // m.mul(m, item.getTransformation());
        Vector4f center = new Vector4f(item.getCenter());
        m.transform(center);
        // boolean b = !frustum.getPlanes().stream().anyMatch(p ->
        // p.getDistance(center) > item.getRadius());
        boolean b = true;
        for (Plane p : frustum.getPlanes()) {
            if (p.getDistance(center) > item.getRadius()) {
                b = false;
                System.out.println("failing at : " + p);
            }
        }
        System.out.println((b ? "" : "!") + "Draw " + name);
        if (b)
            counts.put(name, counts.get(name) + 1);

        return b;
    }
}
