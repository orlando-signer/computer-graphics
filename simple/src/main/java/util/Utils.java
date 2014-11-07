package util;

import java.util.Collection;

import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;

/**
 * @author Orlando Signer
 *
 */
public class Utils {
    public static float[] tuple3dToArray(Collection<? extends Tuple3d> tuples) {
        float[] f = new float[tuples.size() * 3];
        int i = 0;

        for (Tuple3d t : tuples) {
            f[i++] = (float) t.x;
            f[i++] = (float) t.y;
            f[i++] = (float) t.z;
        }
        return f;
    }

    public static float[] points2fToArray(Collection<? extends Tuple2f> tuples) {
        float[] f = new float[tuples.size() * 2];
        int i = 0;

        for (Tuple2f t : tuples) {
            f[i++] = t.x;
            f[i++] = t.y;
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
