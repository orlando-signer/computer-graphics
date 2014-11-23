package jrtr;

import static org.junit.Assert.assertEquals;

import javax.vecmath.Vector3f;

import org.junit.Test;

public class PlaneTest {

    private final double DELTA = 0.00001;

    @Test
    public void testDistances() {
        Plane p = new Plane(0, new Vector3f(1, 0, 0));
        assertEquals(1, p.getDistance(new Vector3f(1, 0, 0)), DELTA);
        assertEquals(-1, p.getDistance(new Vector3f(-1, 0, 0)), DELTA);
        assertEquals(0, p.getDistance(new Vector3f(0, 0, 0)), DELTA);
        assertEquals(0, p.getDistance(new Vector3f(0, 10, 10)), DELTA);
    }
}
