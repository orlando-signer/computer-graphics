package simple;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import jrtr.Light;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.Shader;
import jrtr.Shape;
import jrtr.glrenderer.GLRenderPanel;
import jrtr.scenemanager.GraphSceneManager;
import jrtr.scenemanager.SceneManagerInterface;
import jrtr.scenemanager.SceneManagerIterator;
import jrtr.scenemanager.ShapeNode;
import jrtr.scenemanager.SimpleSceneManager;
import jrtr.scenemanager.TransformGroup;
import jrtr.swrenderer.SWRenderPanel;
import model.Robot;
import model.Teapot;

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
    Shader diffuseShader;
    Shader toonShader;
    Material material;
    SceneManagerInterface sceneManager;
    List<Shape> shapes;
    public static float currentstep, basicstep;
    private Robot robot;

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
         * Initialization call-back. We inibtialize our renderer here.
         *
         * @param r
         *            the render context that is associated with this render
         *            panel
         */
        @Override
        public void init(RenderContext r) {
            renderContext = r;
            createScene1();

            // Add the scene to the renderer
            renderContext.setSceneManager(sceneManager);

            initShaders();

            initMaterial();
            // shapes.forEach(s -> s.setMaterial(material));
            renderContext.useShader(diffuseShader);

            basicstep = 0.05f;
            currentstep = basicstep;
        }

        private void createScene1() {
            sceneManager = new GraphSceneManager();
            TransformGroup root = ((GraphSceneManager) sceneManager).getSceneRoot();
            Matrix4f m = new Matrix4f();
            m.setIdentity();
            Shape teapot = new Teapot().createShape(renderContext);
            ShapeNode node = new ShapeNode(teapot, m, "teapot");
            root.addChild(node);

            SceneManagerIterator it = sceneManager.iterator();
            while (it.hasNext())
                shapes.add(it.next().getShape());

            while (shapes.remove(null))
                ;
        }

        private void createScene2() {
            sceneManager = new GraphSceneManager();
            TransformGroup root = ((GraphSceneManager) sceneManager).getSceneRoot();
            robot = new Robot(root, renderContext);

            SceneManagerIterator it = sceneManager.iterator();
            while (it.hasNext())
                shapes.add(it.next().getShape());

            while (shapes.remove(null))
                ;
            shapes.forEach(s -> s.setMaterial(material));
        }

        private void addLights() {
            // White light from right
            Light l = new Light();
            l.type = Light.Type.POINT;
            l.position = new Vector3f(5, 5, 0);
            l.diffuse = new Vector3f(1, 1, 1);
            ((SimpleSceneManager) sceneManager).addLight(l);

            // Blue light from top
            l = new Light();
            l.type = Light.Type.POINT;
            l.position = new Vector3f(5, 0, 5);
            l.diffuse = new Vector3f(1, 1, 1);
            ((SimpleSceneManager) sceneManager).addLight(l);
        }

        private void initMaterial() {
            // Make a material that can be used for shading
            material = new Material();
            material.shader = toonShader;
            material.texture = renderContext.makeTexture();
            // material.specular = new Vector3f(0.1F, 0.1F, 0.1F);
            material.shininess = 1;
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

            toonShader = renderContext.makeShader();
            try {
                toonShader.load("../jrtr/shaders/toon.vert", "../jrtr/shaders/toon.frag");
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

            if (robot != null)
                robot.animate(currentstep);
            // Trigger redrawing of the render window
            renderPanel.getCanvas().repaint();
        }
    }

    void reload() {
        shapes = new ArrayList<>();
        jFrame.getContentPane().remove(renderPanel.getCanvas());
        jFrame.revalidate(); // show window
        renderPanel = createRenderPanel();
        jFrame.getContentPane().add(renderPanel.getCanvas());
        // for some reasons, its neccessary to change the size to achieve a
        // repaint
        Dimension size = jFrame.getSize();
        size.height += 1;
        size.width += 1;
        jFrame.setSize(size);
        jFrame.revalidate();
        jFrame.requestFocus();
        renderPanel.getCanvas().requestFocus();
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
