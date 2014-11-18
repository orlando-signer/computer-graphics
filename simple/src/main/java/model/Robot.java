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
    private TransformGroup arms;
    private TransformGroup head;
    private TransformGroup body;
    private TransformGroup leftLeg;
    private TransformGroup rightLeg;

    public Robot(TransformGroup root, RenderContext ctx) {
        this.root = root;
        this.ctx = ctx;
        init();
    }

    private void init() {
        Matrix4f m = getIdentity();
        // m.m03 = 5;
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

        // Arms
        arms = new TransformGroup("ArmGroup");
        Shape armShape = new Cylinder(10, 0.8F, 2).createShape(ctx);
        Shape lowerArmShape = new Cylinder(10, 0.6F, 1.5F).createShape(ctx);
        Matrix4f mLeftArm = getIdentity();
        mLeftArm.m03 = 2.4F;
        Matrix4f mLowerLeftArm = getIdentity();
        mLowerLeftArm.rotX((float) (30F / 180F * Math.PI));
        mLowerLeftArm.m03 = 2.5F;
        mLowerLeftArm.m13 = 2F;
        // mLowerLeftArm.m23 = 0.6F;
        arms.addChild(new ShapeNode(armShape, mLeftArm, "LeftArmNode"));
        arms.addChild(new ShapeNode(lowerArmShape, mLowerLeftArm, "LowerLeftArmNode"));

        Matrix4f mRightArm = new Matrix4f(mLeftArm);
        mRightArm.m03 = -2.5F;
        Matrix4f mLowerRightArm = new Matrix4f(mLowerLeftArm);
        mLowerRightArm.m03 = -2.4F;
        arms.addChild(new ShapeNode(armShape, mRightArm, "RightArmNode"));
        arms.addChild(new ShapeNode(lowerArmShape, mLowerRightArm, "LowerRightArmNode"));

        arms.getTransformation().m13 = 3.5F;
        body.addChild(arms);

        // Legs
        leftLeg = new TransformGroup("LeftLegGroup");
        Shape legShape = new Cylinder(10, 0.8F, 4).createShape(ctx);
        Shape footShape = new Cylinder(10, 0.5F, 1.5F).createShape(ctx);
        Matrix4f mLeftLeg = getIdentity();
        mLeftLeg.m03 = -0.9F;
        Matrix4f mLeftFoot = getIdentity();
        mLeftFoot.rotX((float) (90F / 180F * Math.PI));
        mLeftFoot.m03 = -0.8F;
        mLeftFoot.m13 = 0.25F;
        mLeftFoot.m23 = -2F;
        leftLeg.addChild(new ShapeNode(legShape, mLeftLeg, "LeftLegNode"));
        leftLeg.addChild(new ShapeNode(footShape, mLeftFoot, "LeftFootNode"));
        leftLeg.getTransformation().m13 = -4F;
        body.addChild(leftLeg);

        rightLeg = new TransformGroup("RightLegGroup");
        Matrix4f mRightLeg = getIdentity();
        mRightLeg.m03 = 0.9F;
        Matrix4f mRightFoot = new Matrix4f(mLeftFoot);
        mRightFoot.m03 = 0.8F;
        rightLeg.addChild(new ShapeNode(legShape, mRightLeg, "RightLegNode"));
        rightLeg.addChild(new ShapeNode(footShape, mRightFoot, "RightFootNode"));
        rightLeg.getTransformation().m13 = -4F;
        body.addChild(rightLeg);
    }

    public Node getTransformGroup() {
        return root;
    }

    public void animate(float currentstep) {
        Matrix4f m = root.getTransformation();
        rotate += (currentstep / 10);
        // m.rotY(rotate);

        m = arms.getTransformation();
        m.m13 -= 3.5;
        m.rotX(rotate * 10);
        m.m13 += 3.5;

        // m = rightLeg.getTransformation();
        // // m.m13 += 4;
        // m.rotX(rotate * 5);
        // m.m13 -= 4;
    }

    private Matrix4f getIdentity() {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return m;
    }
}
