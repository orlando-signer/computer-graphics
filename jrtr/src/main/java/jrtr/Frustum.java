package jrtr;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

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
        planes = new HashSet<>();
        setFov(60);
        aspect = 1F;
        near = 1F;
        far = 100F;
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

    public Set<Plane> getPlanes() {
        return planes;
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
        calculatePlanes();
    }

    private void calculatePlanes() {
        planes.clear();

        float top = (float) (Math.tan(fov / 2) * near);
        float bottom = -top;
        float right = aspect * top;
        float left = -right;

        // bottom plane:
        Vector4f p = new Vector4f(0, bottom, near, 0);
        Vector4f n = new Vector4f();
        n.x = 0;
        n.y = (float) (-p.z / Math.sqrt(p.y * p.y + p.z * p.z));
        n.z = (float) (p.y / Math.sqrt(p.y * p.y + p.z * p.z));
        n.normalize();
        float d = p.dot(n);
        planes.add(new Plane(d, n, "bottom"));

        // top plane
        p = new Vector4f(0, top, near, 0);
        n = new Vector4f();
        n.x = 0;
        n.y = (float) (p.z / Math.sqrt(p.y * p.y + p.z * p.z));
        n.z = (float) (-p.y / Math.sqrt(p.y * p.y + p.z * p.z));
        n.normalize();
        d = p.dot(n);
        planes.add(new Plane(d, n, "top"));

        // left plane
        p = new Vector4f(left, 0, near, 0);
        n = new Vector4f();
        n.x = (float) (-p.z / Math.sqrt(p.x * p.x + p.z * p.z));
        n.y = 0;
        n.z = (float) (p.x / Math.sqrt(p.x * p.x + p.z * p.z));
        n.normalize();
        d = p.dot(n);
        planes.add(new Plane(d, n, "left"));

        // right plane
        p = new Vector4f(right, 0, near, 0);
        n = new Vector4f();
        n.x = (float) (p.z / Math.sqrt(p.x * p.x + p.z * p.z));
        n.y = 0;
        n.z = (float) (-p.x / Math.sqrt(p.x * p.x + p.z * p.z));
        n.normalize();
        d = p.dot(n);
        planes.add(new Plane(d, n, "right"));

        // front plane
        p = new Vector4f(0, 0, near, 0);
        n = new Vector4f(0, 0, -near, 0);
        n.normalize();
        d = p.dot(n);
        planes.add(new Plane(d, n, "front"));

        // back plane
        p = new Vector4f(0, 0, far, 0);
        n = new Vector4f(0, 0, far, 0);
        n.normalize();
        d = p.dot(n);
        planes.add(new Plane(d, n, "back"));

    }
}
