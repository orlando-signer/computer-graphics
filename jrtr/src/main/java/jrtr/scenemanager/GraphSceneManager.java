package jrtr.scenemanager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javax.vecmath.Matrix4f;

import jrtr.Camera;
import jrtr.Frustum;
import jrtr.Light;
import jrtr.RenderItem;

public class GraphSceneManager implements SceneManagerInterface {

    private Camera camera;
    private Frustum frustum;
    private TransformGroup root;

    public GraphSceneManager() {
        camera = new Camera();
        frustum = new Frustum();
        root = new TransformGroup();
    }

    @Override
    public SceneManagerIterator iterator() {
        return new GraphIterator();
    }

    @Override
    public Iterator<Light> lightIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public TransformGroup getSceneRoot() {
        return root;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public Frustum getFrustum() {
        return frustum;
    }

    private class GraphIterator implements SceneManagerIterator {

        private final Deque<Node> stack;
        private Matrix4f trafo;
        private Matrix4f dummy;

        public GraphIterator() {
            stack = new ArrayDeque<>();
            stack.add(root);
            trafo = new Matrix4f();
            trafo.setIdentity();
        }

        @Override
        public boolean hasNext() {
            return stack.isEmpty();
        }

        @Override
        public RenderItem next() {
            Node e = stack.pop();
            stack.addAll(e.getChildren());
            if (e instanceof Group) {
                trafo.mul(trafo, e.getTransformation());
                dummy.set(trafo);
            } else {
                dummy.mul(trafo, e.getTransformation());
            }

            return new RenderItem(e.getShape(), dummy);
        }
    }
}
