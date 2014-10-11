package simple;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import model.Model;
import model.Teapot;
import model.Terrain;
import jrtr.GLRenderPanel;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shader;
import jrtr.Shape;
import jrtr.SimpleSceneManager;

/**
 * @author Orlando Signer
 *
 *         Implements a simple application that opens a 3D rendering window and
 *         shows a rotating cube.
 */
public class Simple {
    private int frameHeight = 500;
    private int frameWidth = 500;
    private RenderPanel renderPanel;
    private RenderContext renderContext;
    private Shader normalShader;
    private Shader diffuseShader;
    private Material material;
    private SimpleSceneManager sceneManager;
    private List<Shape> shapes;
    public static float currentstep, basicstep;

    private JFrame jFrame;

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
        jFrame = new JFrame("simple");
        jFrame.setSize(frameWidth, frameHeight);
        jFrame.setLocationRelativeTo(null); // center of screen
        jFrame.getContentPane().add(renderPanel.getCanvas());// put the
        // canvas into
        // a JFrame
        // window
        jFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                frameWidth = jFrame.getWidth();
                frameHeight = jFrame.getHeight();
            }
        });

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true); // show window

        // Register a timer task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);

        if (isDebug) {
            // Add a task that regularly reloads the RenderPanel. Only works
            // when started in debug mode.
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    reload();
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

            Model m = new Terrain(257);
            shapes.add(m.createShape(renderContext));

            shapes.forEach(s -> sceneManager.addShape(s));

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
            // Trigger redrawing of the render window
            renderPanel.getCanvas().repaint();
        }
    }

    /**
     * A mouse listener for the main window of this application. This can be
     * used to process mouse events.
     */
    public class SimpleMouseListener extends MouseAdapter {
        private Vector3f v0;

        @Override
        public void mouseDragged(MouseEvent e) {
            int div = Math.min(frameHeight, frameWidth);
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
            if (angle < 0.1)
                return;

            Matrix4f rot = new Matrix4f();
            rot.setIdentity();
            rot.setRotation(new AxisAngle4f(a, (angle)));
            shapes.forEach(shape -> shape.getTransformation().mul(rot));
            v0 = v1;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            v0 = null;
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
            case 'w': {
                sceneManager.getCamera().getCameraMatrix();
                break;
            }
            case 'h': {
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
                shapes.forEach(s -> s.setMaterial(null));
                renderContext.useShader(normalShader);
                break;
            }
            case 'k': {
                // Remove material from shape, and set "default" shader
                shapes.forEach(s -> s.setMaterial(null));
                renderContext.useDefaultShader();
                break;
            }
            case 'm': {
                // Set a material for more complex shading of the shape
                shapes.forEach(s -> {
                    if (s.getMaterial() == null)
                        s.setMaterial(material);
                    else
                        s.setMaterial(null);
                    renderContext.useDefaultShader();
                });
                break;
            }
            case 'r': {
                reload();
                break;
            }
            }

            // Trigger redrawing
            renderPanel.getCanvas().repaint();
        }
    }

    private void reload() {
        shapes = new ArrayList<>();
        jFrame.getContentPane().remove(renderPanel.getCanvas());
        renderPanel = createRenderPanel();
        jFrame.getContentPane().add(renderPanel.getCanvas());
        jFrame.validate(); // show window
    }

    private RenderPanel createRenderPanel() {
        SimpleRenderPanel panel = new SimpleRenderPanel();
        // Add a mouse and key listener
        SimpleMouseListener mouseListener = new SimpleMouseListener();
        panel.getCanvas().addMouseListener(mouseListener);
        panel.getCanvas().addMouseMotionListener(mouseListener);
        panel.getCanvas().addKeyListener(new SimpleKeyListener());
        panel.getCanvas().setFocusable(true);
        return panel;
    }
}
