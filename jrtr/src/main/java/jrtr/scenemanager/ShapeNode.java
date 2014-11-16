package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.Shape;

public class ShapeNode extends Leaf {

    // The position relative to its parentb
    private Matrix4f position;
    private final Shape item;

    public ShapeNode(Shape shape, Matrix4f position) {
        item = shape;
        this.position = position;
    }

    @Override
    public Matrix4f getTransformation() {
        return position;
    }

    @Override
    public void setTransformation(Matrix4f t) {
        position = t;
    }

    @Override
    public Shape getShape() {
        return item;
    }
}
