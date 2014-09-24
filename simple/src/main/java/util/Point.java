package util;

/**
 * Represents a single, immutable Point with 3 coordinates.
 *
 * @author Orlando Signer
 *
 */
public class Point {
    private final float x;
    private final float y;
    private final float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float[] asArray() {
        return new float[] { x, y, z };
    }

    @Override
    public String toString() {
        return String.format("[%f,%f,%f]", x, y, z);
    }
}
