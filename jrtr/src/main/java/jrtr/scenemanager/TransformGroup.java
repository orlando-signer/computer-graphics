package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.Shape;

public class TransformGroup extends Group {

    private Matrix4f transformation;

    public TransformGroup() {
        transformation = new Matrix4f();
        transformation.setIdentity();
    }

    @Override
    public Matrix4f getTransformation() {
        return transformation;
    }

    @Override
    public void setTransformation(Matrix4f t) {
        transformation = t;
    }

    @Override
    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

}
