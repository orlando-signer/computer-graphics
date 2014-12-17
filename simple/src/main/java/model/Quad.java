package model;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class Quad implements Model {
    @Override
    public Shape createShape(RenderContext ctx) {
        float v[] = { 1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top
                -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom

        float c[] = { 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0 };

        int indices[] = { 0, 2, 3, 0, 1, 2, // top
                7, 3, 4, 7, 0, 3, // front
                6, 0, 7, 6, 1, 0, // right
                5, 1, 6, 5, 2, 1, // back
                4, 2, 5, 4, 3, 2, // left
                6, 4, 5, 6, 7, 4 // bottom
        };

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = ctx.makeVertexData(8);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
        vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
        vertexData.addIndices(indices);

        return new Shape(vertexData);
    }

}
