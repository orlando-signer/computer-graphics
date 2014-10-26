package model;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;

public class Square implements Model {

    @Override
    public Shape createShape(RenderContext ctx) {
        float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1 };
        float t[] = { 0, 0, 1, 0, 1, 1, 0, 1 };
        float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0 };
        VertexData vertexData = ctx.makeVertexData(4);
        vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
        vertexData.addElement(t, VertexData.Semantic.TEXCOORD, 2);
        int indices[] = { 0, 2, 3, 0, 1, 2 };
        vertexData.addIndices(indices);
        return new Shape(vertexData);
    }
}
