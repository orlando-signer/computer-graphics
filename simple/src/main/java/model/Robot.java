package model;

import javax.vecmath.Matrix4f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.scenemanager.Node;
import jrtr.scenemanager.ShapeNode;
import jrtr.scenemanager.TransformGroup;

public class Robot {
    private final RenderContext ctx;
    private TransformGroup root;

    private float rotate;
    private TransformGroup arm;
    private TransformGroup head;
    private TransformGroup body;

    public Robot(TransformGroup root, RenderContext ctx) {
        this.root = root;
        this.ctx = ctx;
        init();
    }

    private void init() {
        Matrix4f m = getIdentity();
        m.m03 = 5;
        body = new TransformGroup("BodyGroup");
        body.setTransformation(m);
        root.addChild(body);
        body.addChild(new ShapeNode(new Cylinder(10, 2F, 4).createShape(ctx), getIdentity(), "BodyNode"));

        Matrix4f mHead = getIdentity();
        mHead.m13 = 5;
        head = new TransformGroup("HeadGroup");
        head.addChild(new ShapeNode(new Cube().createShape(ctx), mHead, "HeadNode"));

        Matrix4f mAntenna = getIdentity();
        mAntenna.m13 = 6F;
        head.addChild(new ShapeNode(new Cylinder(10, 0.1F, 1F).createShape(ctx), mAntenna, "AntennaNode"));
        body.addChild(head);

        arm = new TransformGroup("ArmGroup");
        Shape armShape = new Cylinder(10, 0.8F, 2).createShape(ctx);
        Shape lowerArmShape = new Cylinder(10, 0.6F, 1.5F).createShape(ctx);
        Matrix4f mLeftArm = getIdentity();
        mLeftArm.m03 = 2.4F;
        Matrix4f mLowerLeftArm = getIdentity();
        mLowerLeftArm.rotX((float) (30 * 180 / Math.PI));
        mLowerLeftArm.m03 = 2.5F;
        mLowerLeftArm.m13 = 3.3F;
        mLowerLeftArm.m23 = 0.6F;
        arm.addChild(new ShapeNode(armShape, mLeftArm, "LeftArmNode"));
        arm.addChild(new ShapeNode(lowerArmShape, mLowerLeftArm, "LowerLeftArmNode"));
        arm.getTransformation().m13 = 3.5F;

        Matrix4f mRightArm = new Matrix4f(mLeftArm);
        mRightArm.m03 = -2.5F;
        Matrix4f mLowerRightArm = new Matrix4f(mLowerLeftArm);
        mLowerRightArm.m03 = -2.4F;
        arm.addChild(new ShapeNode(armShape, mRightArm, "RightArmNode"));
        arm.addChild(new ShapeNode(lowerArmShape, mLowerRightArm, "LowerRightArmNode"));

        body.addChild(arm);
    }

    public Node getTransformGroup() {
        return root;
    }

    public void animate(float currentstep) {
        Matrix4f m = root.getTransformation();
        rotate += (currentstep / 10);
        m.rotY(rotate);

        m = arm.getTransformation();
        arm.getTransformation().m13 -= 3.5;
        m.rotX(rotate * 10);
        arm.getTransformation().m13 += 3.5;
    }

    private Matrix4f getIdentity() {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return m;
    }
}
