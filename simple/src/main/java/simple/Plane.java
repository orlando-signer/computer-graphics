package simple;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Matrix4f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class Plane {

    private Shape body;
    private Shape wings;
    private Shape propeller;

    public Plane(RenderContext ctx) {
        VertexData b = new Cube().createVertexData(ctx);
        body = new Shape(b);
        Matrix4f m = body.getTransformation();
        m.m00 = 1;
        m.m11 = 0.3F;
        m.m22 = 0.3F;
        m.m13 = -3F;

        VertexData w = new Cube().createVertexData(ctx);
        wings = new Shape(w);
        Matrix4f m2 = wings.getTransformation();
        m2.m00 = 0.3F;
        m2.m11 = 0.03F;
        m2.m22 = 1.2F;
        m2.m13 = -2.7F;

        VertexData p = new Cylinder(5, 0.07F, 1F).createVertexData(ctx);
        propeller = new Shape(p);
        Matrix4f m3 = propeller.getTransformation();
        m3.m03 = -1F;
        m3.m13 = -3.5F;
    }

    public List<Shape> getShapes() {
        return Arrays.asList(body, wings, propeller);
    }
}
