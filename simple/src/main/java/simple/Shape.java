package simple;

import jrtr.RenderContext;
import jrtr.VertexData;

/**
 * @author Orlando Signer
 *
 */
public interface Shape {
    VertexData createVertexData(RenderContext ctx);
}
