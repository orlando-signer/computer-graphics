package jrtr;

import static org.junit.Assert.assertEquals;

import javax.vecmath.Vector4f;

import org.junit.Test;

public class PlaneTest {

    private final double DELTA = 0.00001;

    @Test
    public void testDistances() {
        Plane p = new Plane(0, new Vector4f(1, 0, 0, 0), "test");
        assertEquals(1, p.getDistance(new Vector4f(1, 0, 0, 0)), DELTA);
        assertEquals(-1, p.getDistance(new Vector4f(-1, 0, 0, 0)), DELTA);
        assertEquals(0, p.getDistance(new Vector4f(0, 0, 0, 0)), DELTA);
        assertEquals(0, p.getDistance(new Vector4f(0, 10, 10, 0)), DELTA);
    }
}
