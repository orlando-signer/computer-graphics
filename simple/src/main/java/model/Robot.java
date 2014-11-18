package model;

import javax.vecmath.Matrix4f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.scenemanager.Node;
import jrtr.scenemanager.ShapeNode;
import jrtr.scenemanager.TransformGroup;

public class Robot {
    private final RenderContext ctx;
    private Matrix4f mHead;
    private Matrix4f mLeftArm;
    private TransformGroup root;
    private Matrix4f mRightArm;

    public Robot(RenderContext ctx) {
        this.ctx = ctx;
        init();
    }

    private void init() {
        root = new TransformGroup();
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        root.addChild(new ShapeNode(new Cylinder(10, 2, 4).createShape(ctx), m));

        mHead = new Matrix4f();
        mHead.setIdentity();
        mHead.m13 = 3;
        root.addChild(new ShapeNode(new Cube().createShape(ctx), mHead));

        TransformGroup arm = new TransformGroup();
        Shape armShape = new Cylinder(10, 1, 2).createShape(ctx);
        mLeftArm = new Matrix4f();
        mLeftArm.setIdentity();
        mLeftArm.m03 = 2;
        mLeftArm.m13 = 1F;
        mRightArm = new Matrix4f(mLeftArm);
        mRightArm.m03 = -2;
        arm.addChild(new ShapeNode(armShape, mLeftArm));
        arm.addChild(new ShapeNode(armShape, mRightArm));

        root.addChild(arm);
    }

    public Node getTransformGroup() {
        return root;
    }

    public void animate(float currentstep) {
        Matrix4f m = root.getTransformation();
        m.m23 += currentstep;

    }
}
