package model;

import javax.vecmath.Matrix4f;

import jrtr.Light;
import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.scenemanager.LightNode;
import jrtr.scenemanager.Node;
import jrtr.scenemanager.ShapeNode;
import jrtr.scenemanager.TransformGroup;

public class Robot {
    private final RenderContext ctx;
    private TransformGroup root;

    private float rotate;
    private TransformGroup rightArm;
    private TransformGroup leftArm;
    private TransformGroup head;
    private TransformGroup body;
    private TransformGroup rightWheel;
    private TransformGroup leftWheel;

    public Robot(TransformGroup root, RenderContext ctx) {
        this.root = root;
        this.ctx = ctx;
        init();
    }

    private void init() {
        Matrix4f m = getIdentity();
        m.m03 = 50;
        body = new TransformGroup("BodyGroup");
        body.setTransformation(m);
        root.addChild(body);
        body.addChild(new ShapeNode(new Cylinder(10, 2F, 6).createShape(ctx), getIdentity(), "BodyNode"));

        Matrix4f mHead = getIdentity();
        mHead.m13 = 7;
        head = new TransformGroup("HeadGroup");
        head.addChild(new ShapeNode(new Cube().createShape(ctx), mHead, "HeadNode"));

        Matrix4f mAntenna = getIdentity();
        mAntenna.m13 = 8F;
        head.addChild(new ShapeNode(new Cylinder(10, 0.1F, 1F).createShape(ctx), mAntenna, "AntennaNode"));
        body.addChild(head);

        // Arms
        rightArm = new TransformGroup("LeftArmGroup");
        Shape armShape = new Cylinder(10, 0.8F, 2).createShape(ctx);
        Shape lowerArmShape = new Cylinder(10, 0.6F, 1.5F).createShape(ctx);
        Matrix4f mRightArm = getIdentity();
        mRightArm.m03 = 2.4F;
        Matrix4f mLowerRightArm = getIdentity();
        mLowerRightArm.rotX((float) (30F / 180F * Math.PI));
        mLowerRightArm.m03 = 2.5F;
        mLowerRightArm.m13 = 2F;
        rightArm.addChild(new ShapeNode(armShape, mRightArm, "LeftArmNode"));
        rightArm.addChild(new ShapeNode(lowerArmShape, mLowerRightArm, "LowerLeftArmNode"));
        rightArm.getTransformation().m13 = 5.5F;
        body.addChild(rightArm);

        leftArm = new TransformGroup("RightArmGroup");
        Matrix4f mLeftArm = new Matrix4f(mRightArm);
        mLeftArm.m03 = -2.5F;
        Matrix4f mLowerLeftArm = new Matrix4f(mLowerRightArm);
        mLowerLeftArm.m03 = -2.4F;
        leftArm.addChild(new ShapeNode(armShape, mLeftArm, "RightArmNode"));
        leftArm.addChild(new ShapeNode(lowerArmShape, mLowerLeftArm, "LowerRightArmNode"));

        // Light
        Light l = new Light();
        l.type = Light.Type.POINT;
        LightNode light = new LightNode(l, "LightNode");
        Matrix4f mLight = getIdentity();
        mLight.m03 = -2.4F;
        mLight.m13 = 3.4F;
        light.setTransformation(mLight);
        leftArm.addChild(light);

        leftArm.getTransformation().m13 = 5.5F;
        body.addChild(leftArm);

        // Wheels
        leftWheel = new TransformGroup("LeftWheel");
        Shape wheelShape = new Torus(10, 10, 1.5F, 0.3F).createShape(ctx);
        Shape crossingShasp = new Cylinder(10, 0.2F, 2.5F).createShape(ctx);
        Matrix4f mWheel = getIdentity();
        mWheel.rotY((float) (90F / 180 * Math.PI));
        Matrix4f mVertCross = getIdentity();
        mVertCross.m13 = -1.1F;
        Matrix4f mHorCross = getIdentity();
        mHorCross.rotX((float) (90F / 180 * Math.PI));
        mHorCross.m23 = -1.1F;
        leftWheel.addChild(new ShapeNode(wheelShape, mWheel, "LeftWheelNode"));
        leftWheel.addChild(new ShapeNode(crossingShasp, mVertCross, "LeftVertCrossNode"));
        leftWheel.addChild(new ShapeNode(crossingShasp, mHorCross, "LeftHorCrossNode"));
        leftWheel.getTransformation().m03 = -2.2F;
        body.addChild(leftWheel);

        rightWheel = new TransformGroup("rightWheel");
        rightWheel.addChild(new ShapeNode(wheelShape, mWheel, "RightWheelNode"));
        rightWheel.addChild(new ShapeNode(crossingShasp, mVertCross, "RightVertCrossNode"));
        rightWheel.addChild(new ShapeNode(crossingShasp, mHorCross, "RightHorCrossNode"));
        rightWheel.getTransformation().m03 = +2.2F;
        body.addChild(rightWheel);

    }

    public Node getTransformGroup() {
        return root;
    }

    public void animate(float currentstep) {
        Matrix4f m = root.getTransformation();
        rotate += (currentstep / 10);
        m.rotY(rotate);

        m = rightArm.getTransformation();
        m.m13 -= 5.5;
        m.rotX(rotate * 10);
        m.m13 += 5.5;

        m = leftArm.getTransformation();
        m.m13 -= 5.5;
        m.rotX(-rotate * 10);
        m.m13 += 5.5;

        m = leftWheel.getTransformation();
        m.m03 = +2.2F;
        m.rotX(-rotate * 10);
        m.m03 = -2.2F;

        m = rightWheel.getTransformation();
        m.m03 = -2.2F;
        m.rotX(-rotate * 10);
        m.m03 = +2.2F;
    }

    private Matrix4f getIdentity() {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        return m;
    }
}
