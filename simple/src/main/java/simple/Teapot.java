package simple;

import java.io.IOException;

import jrtr.ObjReader;
import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

/**
 * @author Orlando Signer
 *
 */
public class Teapot implements Model {

    @Override
    public Shape createShape(RenderContext ctx) {
        try {
            VertexData data = ObjReader.read("../obj/teapot.obj", 5, ctx);
            return new Shape(data);
        } catch (IOException e) {
            throw new IllegalStateException("could not load teapot from obj-file", e);
        }
    }
}
