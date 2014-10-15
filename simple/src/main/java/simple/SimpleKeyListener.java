package simple;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.vecmath.Vector3f;

/**
 * A key listener for the main window. Use this to process key events. Currently
 * this provides the following controls: 's': stop animation 'p': play animation
 * '+': accelerate rotation '-': slow down rotation 'd': default shader 'n':
 * shader using surface normals 'm': use a material for shading
 */
class SimpleKeyListener extends KeyAdapter {

    private final Simple simple;

    SimpleKeyListener(Simple simple) {
        this.simple = simple;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 'w': {
            Vector3f cop = simple.sceneManager.getCamera().getCenterOfProjection();
            Vector3f lap = simple.sceneManager.getCamera().getLookAtPoint();
            Vector3f d = new Vector3f();
            d.sub(cop, lap);
            d.scale(1 / d.length());
            cop.sub(d);
            simple.sceneManager.getCamera().setCenterOfProjection(cop);

            break;
        }
        case 'h': {
            // Stop animation
            Simple.currentstep = 0;
            break;
        }
        case 'p': {
            // Resume animation
            Simple.currentstep = Simple.basicstep;
            break;
        }
        case '+': {
            // Accelerate roation
            Simple.currentstep += Simple.basicstep;
            break;
        }
        case '-': {
            // Slow down rotation
            Simple.currentstep -= Simple.basicstep;
            break;
        }
        case 'n': {
            // Remove material from shape, and set "normal" shader
            simple.shapes.forEach(s -> s.setMaterial(null));
            simple.renderContext.useShader(simple.normalShader);
            break;
        }
        case 'k': {
            // Remove material from shape, and set "default" shader
            simple.shapes.forEach(s -> s.setMaterial(null));
            simple.renderContext.useDefaultShader();
            break;
        }
        case 'm': {
            // Set a material for more complex shading of the shape
            simple.shapes.forEach(s -> {
                if (s.getMaterial() == null)
                    s.setMaterial(simple.material);
                else
                    s.setMaterial(null);
                simple.renderContext.useDefaultShader();
            });
            break;
        }
        case 'r': {
            simple.reload();
            break;
        }
        }

        // Trigger redrawing
        simple.renderPanel.getCanvas().repaint();
    }
}
