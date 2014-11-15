package jrtr.scenemanager;

import java.util.Iterator;

import jrtr.Camera;
import jrtr.Frustum;
import jrtr.Light;

public class GraphSceneManager implements SceneManagerInterface {

    private Camera camera;
    private Frustum frustum;

    public GraphSceneManager() {
        camera = new Camera();
        frustum = new Frustum();
    }

    @Override
    public SceneManagerIterator iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Light> lightIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

}
