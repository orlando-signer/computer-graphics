package util;

import java.util.Collection;

public class Utils {
    public static float[] pointsToArray(Collection<Point> points) {
        float[] f = new float[points.size() * 3];
        int i = 0;
        for (Point p : points) {
            f[i++] = p.getX();
            f[i++] = p.getY();
            f[i++] = p.getZ();
        }
        return f;
    }

    public static float[] colorToArray(Collection<Color> points) {
        float[] f = new float[points.size() * 3];
        int i = 0;
        for (Color p : points) {
            f[i++] = p.getR();
            f[i++] = p.getG();
            f[i++] = p.getB();
        }
        return f;
    }
}
