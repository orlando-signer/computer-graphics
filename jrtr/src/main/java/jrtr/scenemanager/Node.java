package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.RenderItem;

public interface Node {
    Matrix4f getTransformation();

    void setTransformation(Matrix4f t);

    RenderItem getItem();

}
