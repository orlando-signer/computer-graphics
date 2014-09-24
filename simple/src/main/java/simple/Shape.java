package simple;

import jrtr.RenderContext;
import jrtr.VertexData;

public interface Shape {
    VertexData createVertexData(RenderContext ctx);
}
