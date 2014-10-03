package simple;

import jrtr.RenderContext;
import jrtr.Shape;

/**
 * @author Orlando Signer
 *
 */
public interface Model {
    Shape createShape(RenderContext ctx);
}
