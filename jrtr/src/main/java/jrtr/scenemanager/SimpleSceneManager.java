package jrtr.scenemanager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import jrtr.Camera;
import jrtr.Frustum;
import jrtr.Light;
import jrtr.RenderItem;
import jrtr.Shape;

/**
 * A simple scene manager that stores objects and lights in linked lists.
 */
public class SimpleSceneManager implements SceneManagerInterface {

    private LinkedList<Shape> shapes;
    private LinkedList<Light> lights;
    private Camera camera;
    private Frustum frustum;

    public SimpleSceneManager() {
        shapes = new LinkedList<Shape>();
        lights = new LinkedList<Light>();
        camera = new Camera();
        frustum = new Frustum();
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    @Override
    public Iterator<Light> lightIterator() {
        return lights.iterator();
    }

    @Override
    public SceneManagerIterator iterator() {
        return new SimpleSceneManagerItr(this);
    }

    private class SimpleSceneManagerItr implements SceneManagerIterator {

        public SimpleSceneManagerItr(SimpleSceneManager sceneManager) {
            itr = sceneManager.shapes.listIterator(0);
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public RenderItem next() {
            Shape shape = itr.next();
            // Here the transformation in the RenderItem is simply the
            // transformation matrix of the shape. More sophisticated
            // scene managers will set the transformation for the
            // RenderItem differently.
            return new RenderItem(shape, shape.getTransformation());
        }

        ListIterator<Shape> itr;
    }

}
