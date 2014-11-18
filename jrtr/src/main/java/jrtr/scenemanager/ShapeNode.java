package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.Shape;

public class ShapeNode extends Leaf {

    // The position relative to its parentb
    private Matrix4f position;
    private final Shape item;
    private String name;

    public ShapeNode(Shape shape, Matrix4f position, String name) {
        item = shape;
        this.position = position;
        this.name = name;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
