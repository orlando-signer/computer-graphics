package jrtr.scenemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.Camera;
import jrtr.Frustum;
import jrtr.Light;
import jrtr.RenderItem;
import jrtr.scenemanager.Node.NodeType;

public class GraphSceneManager implements SceneManagerInterface {

    private Camera camera;
    private Frustum frustum;
    private TransformGroup root;

    public GraphSceneManager() {
        camera = new Camera();
        frustum = new Frustum();
        root = new TransformGroup("RootGroup");
    }

    @Override
    public SceneManagerIterator iterator() {
        return new GraphIterator();
    }

    @Override
    public Iterator<Light> lightIterator() {
        List<Node> children = new ArrayList<>();
        addChildren(root, children);
        return children.stream().filter(c -> c.getType() == NodeType.LIGHT).map(c -> ((LightNode) c).getLight())
                .iterator();
    }

    private void addChildren(Node n, List<Node> children) {
        for (Node child : n.getChildren()) {
            children.add(child);
            addChildren(child, children);
        }
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

        private final Stack<Node> stack;
        private final Stack<Matrix4f> trafos;
        private Matrix4f trafo;
        private final int[] childCount;
        private int depth = 0;

        public GraphIterator() {
            stack = new Stack<>();
            stack.add(root);
            trafos = new Stack<>();
            trafos.add(root.getTransformation());
            trafo = new Matrix4f();
            childCount = new int[16];
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public RenderItem next() {
            Node e = stack.pop();
            childCount[depth] = childCount[depth] - 1;
            if (e.getType() == NodeType.GROUP) {
                depth++;
                childCount[depth] = e.getChildren().size();
                stack.addAll(e.getChildren());
                trafo.mul(trafos.peek(), e.getTransformation());
                trafos.push(new Matrix4f(trafo));
            } else if (e.getType() == NodeType.LEAF) {
                trafo.mul(trafos.peek(), e.getTransformation());
            } else if (e.getType() == NodeType.LIGHT) {
                Light l = ((LightNode) e).getLight();
                trafo.mul(trafos.peek(), e.getTransformation());
                l.position = new Vector3f(trafo.m03, trafo.m13, trafo.m23);

            }
            if (childCount[depth] <= 0) {
                depth--;
                trafos.pop();
            }

            return new RenderItem(e.getShape(), trafo);
        }
    }
}
