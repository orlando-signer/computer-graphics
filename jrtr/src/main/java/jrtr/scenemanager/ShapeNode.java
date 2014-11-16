package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.RenderItem;

public class ShapeNode extends Leaf {

    private Matrix4f trafo;
    // The position relative to its parent
    private Matrix4f position;
    private final RenderItem item;

    public ShapeNode(RenderItem item, Matrix4f position) {
        this.item = item;
        this.position = position;
    }

    @Override
    public Matrix4f getTransformation() {
        return trafo;
    }

    @Override
    public void setTransformation(Matrix4f t) {
        trafo = t;
    }

    @Override
    public RenderItem getItem() {
        return item;
    }

}
