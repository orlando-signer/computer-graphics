package jrtr.scenemanager;

import javax.vecmath.Matrix4f;

import jrtr.Light;
import jrtr.Shape;

public class LightNode extends Leaf {

    private Light light;
    private String name;
    private Matrix4f trafo;

    public LightNode(Light light, String name) {
        this.light = light;
        this.name = name;
    }

    public Light getLight() {
        return light;
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
    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public NodeType getType() {
        return NodeType.LIGHT;
    }

}
