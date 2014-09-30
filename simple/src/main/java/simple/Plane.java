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
        m.m00 = 3;
        body.setTransformation(m);

        VertexData w = new Cube().createVertexData(ctx);
        wings = new Shape(w);
        Matrix4f m2 = wings.getTransformation();
        m2.m11 = 0.1F;
        m2.m22 = 5F;
        m2.m13 = 1F;
        wings.setTransformation(m2);

        VertexData p = new Cylinder(5, 0.2F, 3F).createVertexData(ctx);
        propeller = new Shape(p);
        Matrix4f m3 = propeller.getTransformation();
        m3.m03 = -3.5F;
        m3.m13 = -1.5F;
        propeller.setTransformation(m3);
    }

    public List<Shape> getShapes() {
        return Arrays.asList(body, wings, propeller);
    }
}
