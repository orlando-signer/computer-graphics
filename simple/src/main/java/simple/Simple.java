package simple;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import jrtr.GLRenderPanel;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shader;
import jrtr.Shape;
import jrtr.SimpleSceneManager;
import model.Cube;
import model.Model;

/**
 * @author Orlando Signer
 *
 *         Implements a simple application that opens a 3D rendering window and
 *         shows a rotating cube.
 */
public class Simple {
    int frameHeight = 500;
    int frameWidth = 500;
    RenderPanel renderPanel;
    RenderContext renderContext;
    Shader normalShader;
    private Shader diffuseShader;
    Material material;
    SimpleSceneManager sceneManager;
    List<Shape> shapes;
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

            Model m = new Cube();
            shapes.add(m.createShape(renderContext));

            shapes.forEach(s -> sceneManager.addShape(s));

            // Add the scene to the renderer
            renderContext.setSceneManager(sceneManager);

            initShaders();
            renderContext.useShader(normalShader);

            initMaterial();

            basicstep = 0.05f;
            currentstep = basicstep;
        }

        private void initMaterial() {
            // Make a material that can be used for shading
            material = new Material();
            material.shader = diffuseShader;
            material.texture = renderContext.makeTexture();
            try {
                material.texture.load("../textures/donut.jpg");
            } catch (Exception e) {
                System.out.print("Could not load texture.\n");
                System.out.print(e.getMessage());
            }
        }

        private void initShaders() {
            // Load some more shaders
            normalShader = renderContext.makeShader();
            try {
                normalShader.load("../jrtr/shaders/normal_col.vert", "../jrtr/shaders/normal_col.frag");
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

    void reload() {
        shapes = new ArrayList<>();
        jFrame.getContentPane().remove(renderPanel.getCanvas());
        renderPanel = createRenderPanel();
        jFrame.getContentPane().add(renderPanel.getCanvas());
        jFrame.validate(); // show window
    }

    private RenderPanel createRenderPanel() {
        SimpleRenderPanel panel = new SimpleRenderPanel();
        // Add a mouse and key listener
        SimpleMouseListener mouseListener = new SimpleMouseListener(this, true);
        panel.getCanvas().addMouseListener(mouseListener);
        panel.getCanvas().addMouseMotionListener(mouseListener);
        panel.getCanvas().addKeyListener(new SimpleKeyListener(this));
        panel.getCanvas().setFocusable(true);
        return panel;
    }
}
