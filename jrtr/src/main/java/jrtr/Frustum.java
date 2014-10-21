package jrtr;

import javax.vecmath.Matrix4f;

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
}
