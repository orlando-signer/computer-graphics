package simple;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.vecmath.Vector3f;

/**
 * A key listener for the main window. Use this to process key events. Currently
 * this provides the following controls: 's': stop animbbbation 'p': play
 * animation '+': accelerate rotation '-': slow down rotation 'd': default
 * shader 'n': shader using surface normals 'm': use a material for shading
 */
class SimpleKeyListener extends KeyAdapter {

    private final Simple simple;

    SimpleKeyListener(Simple simple) {
        this.simple = simple;
    }

    private enum Direction {
        W, A, S, D;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 'w': {
            move(Direction.W);
            break;
        }
        case 'a': {
            move(Direction.A);
            break;
        }
        case 's': {
            move(Direction.S);
            break;
        }
        case 'd': {
            move(Direction.D);
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

    private void move(Direction dir) {
        Vector3f cop = simple.sceneManager.getCamera().getCenterOfProjection();
        Vector3f lap = simple.sceneManager.getCamera().getLookAtPoint();
        Vector3f up = simple.sceneManager.getCamera().getUpVector();
        Vector3f d = new Vector3f();
        d.sub(cop, lap);
        d.scale(1 / d.length());
        if (dir == Direction.W)
            cop.sub(d);
        else if (dir == Direction.S)
            cop.add(d);
        else if(dir==Direction.A){
            d.cross(cop, up);
            d.scale(1/d.length());
            cop.add(d);
        }
        simple.sceneManager.getCamera().setCenterOfProjection(cop);
    }
}
