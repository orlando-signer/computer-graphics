package util;

import java.util.Collection;

import javax.vecmath.Point2f;
import javax.vecmath.Point3d;

/**
 * @author Orlando Signer
 *
 */
public class Utils {
    public static float[] pointsToArray(Collection<Point3d> points) {
        float[] f = new float[points.size() * 3];
        int i = 0;

        for (Point3d p : points) {
            f[i++] = (float) p.x;
            f[i++] = (float) p.y;
            f[i++] = (float) p.z;
        }
        return f;
    }

    public static float[] points2fToArray(Collection<Point2f> points) {
        float[] f = new float[points.size() * 2];
        int i = 0;

        for (Point2f p : points) {
            f[i++] = p.x;
            f[i++] = p.y;
        }
        return f;
    }

    public static float[] colorToArray(Collection<Color> colors) {
        float[] f = new float[colors.size() * 3];
        int i = 0;
        for (Color c : colors) {
            f[i++] = c.getR();
            f[i++] = c.getG();
            f[i++] = c.getB();
        }
        return f;
    }
}
