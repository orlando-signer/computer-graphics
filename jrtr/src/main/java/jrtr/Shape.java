package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector4f;

import jrtr.VertexData.Semantic;
import jrtr.VertexData.VertexElement;

/**
 * Represents a 3D object. The shape references its geometry, that is, a
 * triangle mesh stored in a {@link VertexData} object, its {@link Material},
 * and a transformation {@link Matrix4f}.
 */
public class Shape {

    private Material material;
    private VertexData vertexData;
    private Matrix4f t;
    // for bounding sphere
    private Point4f center;
    private float radius;

    /**
     * Make a shape from {@link VertexData}. A shape contains the geometry (the
     * {@link VertexData}), material properties for shading (a refernce to a
     * {@link Material}), and a transformation {@link Matrix4f}.
     *
     *
     * @param vertexData
     *            the vertices of the shape.
     */
    public Shape(VertexData vertexData) {
        this.vertexData = vertexData;
        t = new Matrix4f();
        t.setIdentity();

        calculateBoundingSphere();

        material = null;
    }

    public VertexData getVertexData() {
        return vertexData;
    }

    public void setTransformation(Matrix4f t) {
        this.t = t;
    }

    public Matrix4f getTransformation() {
        return t;
    }

    /**
     * Set a reference to a material for this shape.
     *
     * @param material
     *            the material to be referenced from this shape
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * To be implemented in the "Textures and Shading" project.
     */
    public Material getMaterial() {
        return material;
    }

    public Point4f getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    // TODO perhaps test this implementation?
    // https://github.com/hbf/miniball
    private void calculateBoundingSphere() {
        float[] points = null;
        for (VertexElement v : vertexData.getElements()) {
            if (v.getSemantic() == Semantic.POSITION) {
                points = v.getData();
                break;
            }
        }

        float x = 0;
        float y = 0;
        float z = 0;
        for (int i = 0; i < points.length; i += 3) {
            x += points[i];
            y += points[i + 1];
            z += points[i + 2];
        }
        float div = points.length / 3F;
        x /= div;
        y /= div;
        z /= div;

        float maxRadius = 0;
        float sum;
        for (int i = 0; i < points.length; i += 3) {
            sum = square(points[i] - x) + square(points[i + 1] - y) + square(points[i + 2] - z);
            if (sum > maxRadius)
                maxRadius = sum;
        }

        center = new Point4f(x, y, z, 0);
        radius = (float) Math.sqrt(maxRadius);
    }

    private float square(float x) {
        return x * x;
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
        // TODO OSI: fix bounding sphere
        if (true)
            return true;

        Matrix4f m = new Matrix4f();
        m.setIdentity();
        m.mul(camera.getCameraMatrix());
        m.mul(trafo);
        // m.mul(camera.getCameraMatrix(), trafo);
        // m.mul(m, item.getTransformation());
        Vector4f center = new Vector4f(this.getCenter());
        m.transform(center);
        // boolean b = !frustum.getPlanes().stream().anyMatch(p ->
        // p.getDistance(center) > item.getRadius());
        boolean b = true;
        for (Plane p : frustum.getPlanes()) {
            if (p.getDistance(center) > this.getRadius()) {
                b = false;
                System.out.println("failing at : " + p);
            }
        }
        return b;
    }
}
