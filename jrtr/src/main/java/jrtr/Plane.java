package jrtr;

import javax.vecmath.Vector4f;

public final class Plane {
    private final float d;
    private final Vector4f n;
    private final String name;

    public Plane(float d, Vector4f n, String name) {
        this.d = d;
        this.n = n;
        this.name = name;
    }

    public float getDistance(Vector4f p) {
        return p.dot(n) - d;
    }

    @Override
    public String toString() {
        return name;
    }
}