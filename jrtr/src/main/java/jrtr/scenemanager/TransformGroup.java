package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.RenderItem;

public class TransformGroup extends Group {

    private Matrix4f transformation;

    @Override
    public Matrix4f getTransformation() {
        return transformation;
    }

    @Override
    public void setTransformation(Matrix4f t) {
        transformation = t;
    }

    @Override
    public RenderItem getItem() {
        // TODO Auto-generated method stub
        return null;
    }

}
