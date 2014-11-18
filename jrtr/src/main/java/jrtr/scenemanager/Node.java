package jrtr.scenemanager;

import java.util.List;

import javax.vecmath.Matrix4f;

import jrtr.Shape;

public interface Node {
    Matrix4f getTransformation();

    void setTransformation(Matrix4f t);

    Shape getShape();

    List<Node> getChildren();

    NodeType getType();

    String getName();

    public enum NodeType {
        GROUP, LEAF;
    }
}
