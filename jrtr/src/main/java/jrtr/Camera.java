package jrtr;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.scenemanager.SceneManagerInterface;
import jrtr.scenemanager.SimpleSceneManager;

/**
 * Stores the specification of a virtual camera. You will extend this class to
 * construct a 4x4 camera matrix, i.e., the world-to- camera transform from
 * intuitive parameters.
 *
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a camera.
 */
public class Camera {

    private Matrix4f cameraMatrix;
    private Vector3f centerOfProjection;
    private Vector3f lookAtPoint;
    private Vector3f upVector;

    /**
     * Construct a camera with a default camera matrix. The camera matrix
     * corresponds to the world-to-camera transform. This default matrix places
     * the camera at (0,0,10) in world space, facing towards the origin (0,0,0)
     * of world space, i.e., towards the negative z-axis.
     */
    public Camera() {
        cameraMatrix = new Matrix4f();
        // first image
        centerOfProjection = new Vector3f(0, 0, 10);
        lookAtPoint = new Vector3f(0, 0, 0);
        upVector = new Vector3f(0, 1, 0);
        // second image
        // centerOfProjection = new Vector3f(-10, 40, 40);
        // lookAtPoint = new Vector3f(-5, 0, 0);
        // upVector = new Vector3f(0, 1, 0);

        updateMatrix();
    }

    /**
     * Return the camera matrix, i.e., the world-to-camera transform. For
     * example, this is used by the renderer.
     *
     * @return the 4x4 world-to-camera transform matrix
     */
    public Matrix4f getCameraMatrix() {
        return new Matrix4f(cameraMatrix);
    }

    public Vector3f getCenterOfProjection() {
        return new Vector3f(centerOfProjection);
    }

    public void setCenterOfProjection(Vector3f centerOfProjection) {
        this.centerOfProjection = new Vector3f(centerOfProjection);
        updateMatrix();
    }

    public Vector3f getLookAtPoint() {
        return new Vector3f(lookAtPoint);
    }

    public void setLookAtPoint(Vector3f lookAtPoint) {
        this.lookAtPoint = new Vector3f(lookAtPoint);
        updateMatrix();
    }

    public Vector3f getUpVector() {
        return new Vector3f(upVector);
    }

    public void setUpVector(Vector3f upVector) {
        this.upVector = new Vector3f(upVector);
        updateMatrix();
    }

    private void updateMatrix() {
        // lecture 2, page 77
        Vector3f z = new Vector3f();
        z.sub(centerOfProjection, lookAtPoint);
        z.scale(1 / z.length());

        Vector3f x = new Vector3f();
        x.cross(upVector, z);
        x.scale(1 / x.length());

        Vector3f y = new Vector3f();
        y.cross(z, x);

        cameraMatrix.setIdentity();
        cameraMatrix.setColumn(0, x.x, x.y, x.z, 0);
        cameraMatrix.setColumn(1, y.x, y.y, y.z, 0);
        cameraMatrix.setColumn(2, z.x, z.y, z.z, 0);
        cameraMatrix.setColumn(3, centerOfProjection.x, centerOfProjection.y, centerOfProjection.z, 1);
        cameraMatrix.invert();
    }
}
