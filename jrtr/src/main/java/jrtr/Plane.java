package jrtr;

import javax.vecmath.Vector3f;

public final class Plane {
    private final float d;
    private final Vector3f n;

    public Plane(float d, Vector3f n) {
        this.d = d;
        this.n = n;
    }

    public float getDistance(Vector3f p) {
        return p.dot(n) - d;
    }
}