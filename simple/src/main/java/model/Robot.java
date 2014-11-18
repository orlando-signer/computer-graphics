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
    private Matrix4f mAntenna;

    private float rotate;

    public Robot(TransformGroup root, RenderContext ctx) {
        this.root = root;
        this.ctx = ctx;
        init();
    }

    private void init() {
        Matrix4f m = getIdentity();
        m.m03 = 8;
        TransformGroup body = new TransformGroup();
        body.setTransformation(m);
        root.addChild(body);
        body.addChild(new ShapeNode(new Cylinder(10, 2, 4).createShape(ctx), getIdentity()));

        mHead = getIdentity();
        mHead.m13 = 3;
        TransformGroup head = new TransformGroup();
        head.addChild(new ShapeNode(new Cube().createShape(ctx), mHead));
        body.addChild(head);

        mAntenna = getIdentity();
        mAntenna.m13 = 4.5F;
        body.addChild(new ShapeNode(new Cylinder(10, 0.1F, 1F).createShape(ctx), mAntenna));

        TransformGroup arm = new TransformGroup();
        Shape armShape = new Cylinder(10, 0.8F, 2).createShape(ctx);
        mLeftArm = getIdentity();
        mLeftArm.m03 = 2.5F;
        mLeftArm.m13 = 1F;
        mRightArm = new Matrix4f(mLeftArm);
        mRightArm.m03 = -2.5F;
        arm.addChild(new ShapeNode(armShape, mLeftArm));
        arm.addChild(new ShapeNode(armShape, mRightArm));

        body.addChild(arm);
    }

    public Node getTransformGroup() {
        return root;
    }

    public void animate(float currentstep) {
        Matrix4f m = root.getTransformation();
        rotate += currentstep;
        m.rotY(rotate);
    }

    private Matrix4f getIdentity() {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return m;
    }
}
