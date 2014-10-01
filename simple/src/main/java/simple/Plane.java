package simple;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

/**
 * @author Orlando Signer
 *
 */
public class Plane {

    private Shape body;
    private Shape wings;
    private Shape propeller;

    private final List<Shape> shapes;

    public Plane(RenderContext ctx) {
        VertexData b = new Cube().createVertexData(ctx);
        body = new Shape(b);
        Matrix4f m = body.getTransformation();
        m.m00 = 1;
        m.m11 = 0.3F;
        m.m22 = 0.3F;

        VertexData w = new Cube().createVertexData(ctx);
        wings = new Shape(w);
        Matrix4f m2 = wings.getTransformation();
        m2.m00 = 0.6F;
        m2.m11 = 0.06F;
        m2.m22 = 2.4F;
        m2.m13 = 0.3F;

        VertexData p = new Cylinder(5, 0.1F, 1.5F).createVertexData(ctx);
        propeller = new Shape(p);
        Matrix4f m3 = propeller.getTransformation();
        m3.m03 = -1.1F;

        shapes = Arrays.asList(body, wings, propeller);
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void animate() {
        animatePropeller();
        animatePlane();
    }

    private void animatePlane() {
        Matrix4f rotZ = new Matrix4f();
        rotZ.rotZ(0 - Simple.currentstep);

        getShapes().stream().forEach(s -> s.getTransformation().mul(rotZ, s.getTransformation()));
    }

    private void animatePropeller() {
        Matrix4f m = propeller.getTransformation();
        Vector4f translation = new Vector4f();
        m.getColumn(3, translation);
        translation.negate();
        Matrix4f transInv = new Matrix4f();
        Matrix4f trans = new Matrix4f();
        trans.setIdentity();
        trans.setColumn(3, translation);
        transInv.invert(trans);

        Matrix4f rot = new Matrix4f();
        rot.setIdentity();
        rot.rotX(0.2F);
        transInv.mul(rot);

        transInv.mul(trans);
        m.mul(transInv, m);
    }
}
