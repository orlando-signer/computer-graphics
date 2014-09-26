package util;

/**
 * Represents a single, immutable Point with 3 coordinates.
 *
 * @author Orlando Signer
 *
 */
public class Point {
	public static final Point ZERO = new Point(0, 0, 0);

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

	public Point add(Point p) {
		return add(p.x, p.y, p.z);
	}

	public Point add(float x, float y, float z) {
		return new Point(this.x + x, this.y + y, this.z + z);
	}

	public float[] asArray() {
		return new float[] { x, y, z };
	}

	@Override
	public String toString() {
		return String.format("[%f,%f,%f]", x, y, z);
	}
}
