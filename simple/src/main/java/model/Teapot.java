package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jrtr.ObjReader;
import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;
import util.Color;
import util.Utils;

/**
 * @author Orlando Signer
 *
 */
public class Teapot implements Model {

    @Override
    public Shape createShape(RenderContext ctx) {
        try {
            VertexData data = ObjReader.read("../obj/teapot.obj", 5F, ctx);

            List<Color> colors = new ArrayList<>(data.getNumberOfVertices());
            for (int i = 0; i < data.getNumberOfVertices(); i++)
                // colors.add(new Color(0.05F, 0.05F, 0.05F));
                colors.add(Color.WHITE);
            data.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);

            return new Shape(data);
        } catch (IOException e) {
            throw new IllegalStateException("could not load teapot from obj-file", e);
        }
    }
}
