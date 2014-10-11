package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3d;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

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
        
        for(int i = 0;i<size;i++)
            for(int j = 0;j<size;j++)
                this.terrain[i][j] = this.rand.nextFloat();
    }

    private void square(int x0, int x1, int y0, int y1) {
        float val = (terrain[x0][y0] + terrain[x0][y1] + terrain[x1][y0] + terrain[x1][y1]) / 4;
        val += rand.nextFloat() * 2 - 1;
        terrain[(x0 + x1) / 2][(y0 + y1) / 2] = val;

    }

    @Override
    public Shape createShape(RenderContext ctx) {
        List<Point3d> vertices = new ArrayList<>(size * size);
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                vertices.add(new Point3d(x, this.terrain[x][z], z));
            }
        }
        
        List<Integer> indices = new ArrayList<>((size - 1) * (size - 1) * 6);
        for (int z = 0; z < size - 1; z++) {
            int index = z * size;
            for (int x = 0; x < size - 1; x++) {
                indices.add(index + x);
                indices.add(index + x + 1);
                indices.add(index + size + x);

                indices.add(index + size + x);
                indices.add(index + size + x + 1);
                indices.add(index + x + 1);
            }
        }

        List<Color> colors = new ArrayList<>(size * size);
        for (int i = 0; i < size * size; i++)
            colors.add(Color.GREEN);

        VertexData vertexData = ctx.makeVertexData(size * size);
        vertexData.addElement(Utils.pointsToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        // vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
        // vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));
        return new Shape(vertexData);
    }

}
