package jrtr;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.scenemanager.SceneManagerInterface;
import jrtr.scenemanager.SimpleSceneManager;

/**
 * Stores the specification of a viewing frustum, or a viewing volume. The
 * viewing frustum is represented by a 4x4 projection matrix. You will extend
 * this class to construct the projection matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a frustum.
 */
public class Frustum {

    private Matrix4f projectionMatrix;
    private float fov;
    private float aspect;
    private float near;
    private float far;

    private final Set<Plane> planes;

    /**
     * Construct a default viewing frustum. The frustum is given by a default
     * 4x4 projection matrix.
     */
    public Frustum() {
        projectionMatrix = new Matrix4f();
        setFov(60);
        aspect = 1F;
        near = 1F;
        far = 100F;
        planes = new HashSet<>();
        update();
    }

    /**
     * Return the 4x4 projection matrix, which is used for example by the
     * renderer.
     *
     * @return the 4x4 projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return new Matrix4f(projectionMatrix);
    }

    public float getFov() {
        return fov;
    }

    /**
     * Set the FOV in degree.
     *
     * @param fov
     */
    public void setFov(float fov) {
        this.fov = (float) (Math.PI / 180 * fov);
        update();
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
        update();
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
        update();
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
        update();
    }

    private void update() {
        // lecture 3 page 52
        projectionMatrix.setIdentity();
        projectionMatrix.m00 = (float) (1 / (aspect * Math.tan(fov / 2)));
        projectionMatrix.m11 = (float) (1 / Math.tan(fov / 2));
        projectionMatrix.m22 = (near + far) / (near - far);
        projectionMatrix.m23 = (2 * near * far) / (near - far);
        projectionMatrix.m32 = -1;
        projectionMatrix.m33 = 0;
    }

    private void calculatePlanes() {
        planes.clear();

        float top = (float) (Math.tan(fov / 2) * near);
        float bottom = -top;
        float right = aspect * top;
        float left = -right;

        // bottom plane:
        Vector3f p = new Vector3f(0, bottom, near);
        Vector3f n = new Vector3f();
        n.x = 0;
        n.y = (float) ((Math.sqrt(p.y * p.y + 4 * p.z * p.z) + p.y) / (2 * p.z));
        n.z = -p.y / p.z * n.y;
        float d = p.dot(n);
        planes.add(new Plane(d, n));

        // top plane
        p = new Vector3f(0, top, near);
        n = new Vector3f();
        n.x = 0;
        n.y = (float) ((-Math.sqrt(p.y * p.y + 4 * p.z * p.z) + p.y) / (2 * p.z));
        n.z = -p.y / p.z * n.y;
        d = p.dot(n);
        planes.add(new Plane(d, n));

        // left plane
        p = new Vector3f(left, 0, near);
        n = new Vector3f();
        n.x = (float) ((Math.sqrt(p.x * p.x + 4 * p.z * p.z) + p.x) / (2 * p.z));
        n.y = 0;
        n.z = -p.x / p.z * n.x;
        d = p.dot(n);
        planes.add(new Plane(d, n));

        // right plane
        p = new Vector3f(left, 0, near);
        n = new Vector3f();
        n.x = (float) ((-Math.sqrt(p.x * p.x + 4 * p.z * p.z) + p.x) / (2 * p.z));
        n.y = 0;
        n.z = -p.x / p.z * n.x;
        d = p.dot(n);
        planes.add(new Plane(d, n));

        // front plane
        p = new Vector3f(0, 0, near);
        n = new Vector3f(0, 0, -near);
        d = p.dot(n);
        planes.add(new Plane(d, n));

        // back plane
        p = new Vector3f(0, 0, far);
        n = new Vector3f(0, 0, far);
        d = p.dot(n);
        planes.add(new Plane(d, n));

    }
}
