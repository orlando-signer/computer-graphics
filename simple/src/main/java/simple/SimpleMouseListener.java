package simple;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * A mouse listener for the main window of this application. This can be used to
 * process mouse events.
 */
public class SimpleMouseListener extends MouseAdapter {
    private final Simple simple;

    private Vector3f v0;

    private float prevX = 0;
    private float prevY = 0;

    SimpleMouseListener(Simple simple) {
        this.simple = simple;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int div = Math.min(simple.frameHeight, simple.frameWidth);
        float x = ((float) e.getX()) / (div / 2);
        float y = ((float) e.getY()) / (div / 2);
        x = x - 1;
        y = 1 - y;
        float z = 1 - x * x - y * y;
        z = (float) (z < 0 ? 0 : Math.sqrt(z));

        Vector3f v1 = new Vector3f(x, y, z);
        v1.normalize();

        if (v0 == null) {
            v0 = v1;
            return;
        }

        Vector3f a = new Vector3f();
        a.cross(v0, v1);
        float angle = v0.angle(v1);
        if (angle < 0.05)
            return;

        Matrix4f rot = new Matrix4f();
        rot.setIdentity();
        rot.setRotation(new AxisAngle4f(a, (angle)));
        simple.shapes.forEach(shape -> shape.getTransformation().mul(rot));
        v0 = v1;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        v0 = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int div = Math.min(simple.frameHeight, simple.frameWidth) / 100;
        float x = e.getX() / div;
        float y = e.getY() / div;

        float moveX = prevX - x;
        float moveY = prevY - y;

        Vector3f lap = simple.sceneManager.getCamera().getLookAtPoint();
        lap.x -= moveX;
        lap.y -= moveY;

        simple.sceneManager.getCamera().setLookAtPoint(lap);

        prevX = x;
        prevY = y;

        // Trigger redrawing
        simple.renderPanel.getCanvas().repaint();
    }
}