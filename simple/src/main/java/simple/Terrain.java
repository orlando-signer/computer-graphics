package simple;

import java.util.Random;

import jrtr.RenderContext;
import jrtr.Shape;

public class Terrain implements Model {

    private final int size;
    private final float[][] terrain;
    private final Random rand;

    /**
     * @param size
     *            should be a power of two plus one
     */
    public Terrain(int size) {
        this.size = size;
        this.terrain = new float[size][size];
        this.rand = new Random();
    }

    private void square(int x0, int x1, int y0, int y1) {
        float val = (terrain[x0][y0] + terrain[x0][y1] + terrain[x1][y0] + terrain[x1][y1]) / 4;
        val += rand.nextFloat() * 2 - 1;
        terrain[(x0+x1)/2][(y0+y1)/2] = val;
        
    }

    @Override
    public Shape createShape(RenderContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

}
