package util;

public class Color {

    public static final Color RED = new Color(1, 0, 0);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(1, 1, 1);

    private final float r;
    private final float g;
    private final float b;

    public Color(float r, float g, float b) {
        isValid(r);
        isValid(g);
        isValid(b);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    private void isValid(float f) {
        if (f < 0 || f > 1.0)
            throw new IllegalStateException("Invalid value for color: " + f);
    }
}
