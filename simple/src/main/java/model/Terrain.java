package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import jrtr.RenderContext;
import jrtr.Shape;
import jrtr.VertexData;
import util.Color;
import util.Utils;

import com.google.common.primitives.Ints;

public class Terrain implements Model {

    private final int size;
    private final float scale;
    private final float[][] terrain;
    private final Random rand;

    /**
     * @param size
     *            should be a power of two plus one
     */
    public Terrain(int size, float scale) {
        this.size = size;
        this.scale = scale;
        terrain = new float[size][size];
        rand = new Random();

        terrain[0][0] = 2;
        terrain[0][size - 1] = 1.2F;
        terrain[size - 1][0] = 1.2F;
        terrain[size - 1][size - 1] = 1.5F;

        int squareLength = size - 1;
        while (squareLength > 1) {
            // square step
            for (int i = 0; i < size - 1; i += squareLength) {
                for (int j = 0; j < size - 1; j += squareLength)
                    square(i, j, squareLength);
            }

            // diamond step
            for (int i = 0; i < size - 1; i += squareLength) {
                for (int j = 0; j < size - 1; j += squareLength) {
                    diamond(i, j + squareLength / 2, squareLength);
                    diamond(i + squareLength / 2, j, squareLength);
                    diamond(i + squareLength / 2, j + squareLength, squareLength);
                    diamond(i + squareLength, j + squareLength / 2, squareLength);
                }
            }

            squareLength /= 2;
        }
        // for (float[] fs : terrain) {
        // System.out.println(Arrays.toString(fs));
        // }
    }

    private void square(int x, int y, int squareSize) {
        float a = terrain[x][y];
        float b = (y + squareSize <= size) ? terrain[x][y + squareSize] : 0;
        float c = (x + squareSize <= size) ? terrain[x + squareSize][y] : 0;
        float d = (x + squareSize <= size && y + squareSize <= size) ? terrain[x + squareSize][y + squareSize] : 0;
        terrain[x + squareSize / 2][y + squareSize / 2] = (a + b + c + d) / 4 + getRandom(squareSize);
    }

    private void diamond(int x, int y, int squareSize) {
        int s = squareSize / 2;
        float a = (x - s) >= 0 ? terrain[x - s][y] : 0;
        float b = (x + s < size) ? terrain[x + s][y] : 0;
        float c = (y - s >= 0) ? terrain[x][y - s] : 0;
        float d = (y + s < size) ? terrain[x][y + s] : 0;
        terrain[x][y] = (a + b + c + d) / 4 + getRandom(squareSize);
    }

    private float getRandom(int squareSize) {
        return (squareSize) * (rand.nextFloat() - (0.5F)) / 2;
    }

    @Override
    public Shape createShape(RenderContext ctx) {
        List<Point3d> vertices = new ArrayList<>(size * size);
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                vertices.add(new Point3d(x, terrain[x][z], z));
            }
        }
        vertices.forEach(p -> p.scale(scale / size));

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

        List<Point3d> normals = calculateNormals();

        List<Color> colors = colorize();

        VertexData vertexData = ctx.makeVertexData(size * size);
        vertexData.addElement(Utils.tuple3dToArray(vertices), VertexData.Semantic.POSITION, 3);
        vertexData.addElement(Utils.colorToArray(colors), VertexData.Semantic.COLOR, 3);
        vertexData.addElement(Utils.tuple3dToArray(normals), VertexData.Semantic.NORMAL, 3);
        // vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
        vertexData.addIndices(Ints.toArray(indices));
        return new Shape(vertexData);
    }

    private List<Color> colorize() {
        float maxHeight = (float) (size * 0.25);
        List<Color> colors = new ArrayList<>(size * size);
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                if (terrain[x][z] < 0.2 * maxHeight)
                    colors.add(Color.GREEN);
                else if (terrain[x][z] < 0.5 * maxHeight)
                    colors.add(Color.GRAY);
                else
                    colors.add(Color.WHITE);
            }
        }
        return colors;
    }

    private List<Point3d> calculateNormals() {
        List<Point3d> normals = new ArrayList<>(size * size);
        Vector3f a;
        Vector3f b;
        Vector3f tmp = new Vector3f();
        // TODO Pretty sure there is an easier way to do this...
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                float height = terrain[x][z];
                Vector3f normal = new Vector3f();
                if (x != 0) {
                    if (z != 0) {
                        a = new Vector3f(0, terrain[x][z - 1] - height, -1);
                        b = new Vector3f(-1, terrain[x - 1][z - 1] - height, -1);
                        crossAndNormalize(a, b, tmp, normal);

                        a = new Vector3f(-1, terrain[x - 1][z] - height, 0);
                        crossAndNormalize(a, b, tmp, normal);
                    }
                    if (z != size - 1) {
                        a = new Vector3f(-1, terrain[x - 1][z] - height, 0);
                        b = new Vector3f(0, terrain[x][z + 1] - height, 1);
                        crossAndNormalize(a, b, tmp, normal);
                    }
                }

                if (x != size - 1) {
                    if (z != 0) {
                        a = new Vector3f(0, terrain[x][z - 1] - height, -1);
                        b = new Vector3f(1, terrain[x + 1][z] - height, 0);
                        crossAndNormalize(a, b, tmp, normal);
                    }
                    if (z != size - 1) {
                        a = new Vector3f(1, terrain[x + 1][z] - height, 0);
                        b = new Vector3f(1, terrain[x + 1][z + 1] - height, 1);
                        crossAndNormalize(a, b, tmp, normal);

                        a = new Vector3f(0, terrain[x][z + 1] - height, 1);
                        crossAndNormalize(a, b, tmp, normal);
                    }
                }
                normal.normalize();
                normals.add(new Point3d(normal));
            }
        }
        return normals;
    }

    private void crossAndNormalize(Vector3f a, Vector3f b, Vector3f tmp, Vector3f normal) {
        tmp.cross(b, a);
        tmp.normalize();
        if (tmp.y < 0)
            tmp.negate();
        normal.add(tmp);
    }
}
