package simple;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import jrtr.GLRenderPanel;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shader;
import jrtr.Shape;
import jrtr.SimpleSceneManager;

/**
 * Implements a simple application that opens a 3D rendering window and shows a
 * rotating cube.
 */
public class Simple {
    private RenderPanel renderPanel;
    private RenderContext renderContext;
    private Shader normalShader;
    private Shader diffuseShader;
    private Material material;
    private SimpleSceneManager sceneManager;
    private List<Shape> shapes;
    public static float currentstep, basicstep;

    private Plane plane;
    private Shape torus;

    private final boolean isDebug;

    public static void main(String[] args) {
        Simple s = new Simple();
        s.start();
    }

    public Simple() {
        shapes = new ArrayList<>();
        isDebug = false;
    }

    /**
     * The main function opens a 3D rendering window, implemented by the class
     * {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then called
     * backed for initialization automatically. It then constructs a simple 3D
     * scene, and starts a timer task to generate an animation.
     */
    private void start() {
        renderPanel = createRenderPanel();
        // Make the main window of this application and add the renderer to it
        final JFrame jframe = new JFrame("simple");
        jframe.setSize(500, 500);
        jframe.setLocationRelativeTo(null); // center of screen
        jframe.getContentPane().add(renderPanel.getCanvas());// put the
        // canvas into
        // a JFrame
        // window

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true); // show window

        // Register a timer task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);

        if (isDebug) {
            // Add a task that regularly reloads the RenderPanel. Only works
            // when started in debug mode.
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    shapes = new ArrayList<>();
                    jframe.getContentPane().remove(renderPanel.getCanvas());
                    renderPanel = createRenderPanel();
                    jframe.getContentPane().add(renderPanel.getCanvas());
                    jframe.validate(); // show window
                }
            };
            int reloadDely = 2000;
            timer.scheduleAtFixedRate(task, reloadDely, reloadDely);
        }
    }

    /**
     * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to provide
     * a call-back function for initialization. Here we construct a simple 3D
     * scene and start a timer task to generate an animation.
     */
    public final class SimpleRenderPanel extends GLRenderPanel {

        /**
         * Initialization call-back. We initialize our renderer here.
         *
         * @param r
         *            the render context that is associated with this render
         *            panel
         */
        @Override
        public void init(RenderContext r) {
            renderContext = r;
            sceneManager = new SimpleSceneManager();

            // Create plane and translate it
            plane = new Plane(renderContext);
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            m.m13 = -3F;
            plane.getShapes().stream().forEach(s -> s.getTransformation().mul(m, s.getTransformation()));
            shapes.addAll(plane.getShapes());

            // Create a torus
            Torus t = new Torus(30, 30, 2.5F, 0.2F);
            torus = new Shape(t.createVertexData(renderContext));
            m.setIdentity();
            m.m13 = 3F;
            torus.getTransformation().mul(m, torus.getTransformation());
            shapes.add(torus);

            shapes.stream().forEach(s -> sceneManager.addShape(s));

            // Add the scene to the renderer
            renderContext.setSceneManager(sceneManager);

            initShaders();

            initMaterial();

            basicstep = 0.01f;
            currentstep = basicstep;
        }

        private void initMaterial() {
            // Make a material that can be used for shading
            material = new Material();
            material.shader = diffuseShader;
            material.texture = renderContext.makeTexture();
            try {
                material.texture.load("../textures/plant.jpg");
            } catch (Exception e) {
                System.out.print("Could not load texture.\n");
                System.out.print(e.getMessage());
            }
        }

        private void initShaders() {
            // Load some more shaders
            normalShader = renderContext.makeShader();
            try {
                normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
            } catch (Exception e) {
                System.out.print("Problem with shader:\n");
                System.out.print(e.getMessage());
            }

            diffuseShader = renderContext.makeShader();
            try {
                diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
            } catch (Exception e) {
                System.out.print("Problem with shader:\n");
                System.out.print(e.getMessage());
            }
        }
    }

    /**
     * A timer task that generates an animation. This task triggers the
     * redrawing of the 3D scene every time it is executed.
     */
    public class AnimationTask extends TimerTask {
        @Override
        public void run() {
            if (shapes == null || shapes.isEmpty())
                return;
            plane.animate();
            animateTorus();
            // Trigger redrawing of the render window
            renderPanel.getCanvas().repaint();
        }

        private void animateTorus() {
            Matrix4f m = torus.getTransformation();
            Vector4f translation = new Vector4f();
            m.getColumn(3, translation);
            translation.negate();
            Matrix4f transInv = new Matrix4f();
            Matrix4f trans = new Matrix4f();
            trans.setIdentity();
            trans.setColumn(3, translation);
            transInv.invert(trans);

            Matrix4f rot = new Matrix4f();
            rot.setIdentity();
            rot.rotY(currentstep / 2);
            transInv.mul(rot);

            transInv.mul(trans);
            m.mul(transInv, m);
        }
    }

    /**
     * A mouse listener for the main window of this application. This can be
     * used to process mouse events.
     */
    public class SimpleMouseListener implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }
    }

    /**
     * A key listener for the main window. Use this to process key events.
     * Currently this provides the following controls: 's': stop animation 'p':
     * play animation '+': accelerate rotation '-': slow down rotation 'd':
     * default shader 'n': shader using surface normals 'm': use a material for
     * shading
     */
    public class SimpleKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
            case 's': {
                // Stop animation
                currentstep = 0;
                break;
            }
            case 'p': {
                // Resume animation
                currentstep = basicstep;
                break;
            }
            case '+': {
                // Accelerate roation
                currentstep += basicstep;
                break;
            }
            case '-': {
                // Slow down rotation
                currentstep -= basicstep;
                break;
            }
            case 'n': {
                // Remove material from shape, and set "normal" shader
                shapes.stream().forEach(s -> s.setMaterial(null));
                renderContext.useShader(normalShader);
                break;
            }
            case 'd': {
                // Remove material from shape, and set "default" shader
                shapes.stream().forEach(s -> s.setMaterial(null));
                renderContext.useDefaultShader();
                break;
            }
            case 'm': {
                // Set a material for more complex shading of the shape
                shapes.stream().forEach(s -> {
                    if (s.getMaterial() == null)
                        s.setMaterial(material);
                    else
                        s.setMaterial(null);
                    renderContext.useDefaultShader();
                });
                break;
            }
            }

            // Trigger redrawing
            renderPanel.getCanvas().repaint();
        }
    }

    private RenderPanel createRenderPanel() {
        SimpleRenderPanel panel = new SimpleRenderPanel();
        // Add a mouse and key listener
        panel.getCanvas().addMouseListener(new SimpleMouseListener());
        panel.getCanvas().addKeyListener(new SimpleKeyListener());
        panel.getCanvas().setFocusable(true);
        return panel;
    }
}
