package simple;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.vecmath.Matrix4f;

import jrtr.GLRenderPanel;
import jrtr.Material;
import jrtr.RenderContext;
import jrtr.RenderPanel;
import jrtr.SWRenderPanel;
import jrtr.Shader;
import jrtr.Shape;
import jrtr.SimpleSceneManager;
import jrtr.VertexData;

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
    private Shape shape;
    private float currentstep, basicstep;

    private final boolean isDebug;

    public Simple() {
        this.isDebug = true;
        init();
    }

    public static void main(String[] args) {
        new Simple();
    }

    /**
     * The main function opens a 3D rendering window, implemented by the class
     * {@link SimpleRenderPanel}. {@link SimpleRenderPanel} is then called
     * backed for initialization automatically. It then constructs a simple 3D
     * scene, and starts a timer task to generate an animation.
     */
    private void init() {
        this.renderPanel = createRenderPanel();
        // Make the main window of this application and add the renderer to it
        final JFrame jframe = new JFrame("simple");
        jframe.setSize(500, 500);
        jframe.setLocationRelativeTo(null); // center of screen
        jframe.getContentPane().add(this.renderPanel.getCanvas());// put the
        // canvas into
        // a JFrame
        // window

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true); // show window

        // Register a timer task
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AnimationTask(), 0, 10);

        if (this.isDebug) {
            // Add a task that regularly reloads the RenderPanel. Only works
            // when started in debug mode.
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    jframe.getContentPane().remove(Simple.this.renderPanel.getCanvas());
                    Simple.this.renderPanel = createRenderPanel();
                    jframe.getContentPane().add(Simple.this.renderPanel.getCanvas());
                    jframe.validate(); // show window
                }
            };
            timer.scheduleAtFixedRate(task, 2000, 2000);
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
            Simple.this.renderContext = r;

            VertexData vertexData = makeCylinder(10);

            // Make a scene manager and add the object
            Simple.this.sceneManager = new SimpleSceneManager();
            Simple.this.shape = new Shape(vertexData);
            Simple.this.sceneManager.addShape(Simple.this.shape);

            // Add the scene to the renderer
            Simple.this.renderContext.setSceneManager(Simple.this.sceneManager);

            // Load some more shaders
            Simple.this.normalShader = Simple.this.renderContext.makeShader();
            try {
                Simple.this.normalShader.load("../jrtr/shaders/normal.vert", "../jrtr/shaders/normal.frag");
            } catch (Exception e) {
                System.out.print("Problem with shader:\n");
                System.out.print(e.getMessage());
            }

            Simple.this.diffuseShader = Simple.this.renderContext.makeShader();
            try {
                Simple.this.diffuseShader.load("../jrtr/shaders/diffuse.vert", "../jrtr/shaders/diffuse.frag");
            } catch (Exception e) {
                System.out.print("Problem with shader:\n");
                System.out.print(e.getMessage());
            }

            // Make a material that can be used for shading
            Simple.this.material = new Material();
            Simple.this.material.shader = Simple.this.diffuseShader;
            Simple.this.material.texture = Simple.this.renderContext.makeTexture();
            try {
                Simple.this.material.texture.load("../textures/plant.jpg");
            } catch (Exception e) {
                System.out.print("Could not load texture.\n");
                System.out.print(e.getMessage());
            }

            Simple.this.basicstep = 0.01f;
            Simple.this.currentstep = Simple.this.basicstep;
        }

        private VertexData makeCylinder(int segments) {
            // Make a simple geometric object: a cylinder
            float radius = 1;
            float height = 1;
            float x = 0;
            float z = radius;
            double angle = (2 * Math.PI) / segments;

            float[] v = new float[segments * 2 * 3 + 2 * 3];
            for (int i = 0; i < segments * 2;) {
                v[i++] = x;
                v[i++] = 0;
                v[i++] = z;
                v[i++] = x;
                v[i++] = height;
                v[i++] = z;
                x = (float) (Math.cos(angle) * x - Math.sin(angle) * z);
                z = (float) (Math.sin(angle) * x + Math.cos(angle) * z);
            }

            v[segments * 2 * 3] = 0;
            v[segments * 2 * 3 + 1] = 0;
            v[segments * 2 * 3 + 2] = 0;
            v[segments * 2 * 3 + 3] = 0;
            v[segments * 2 * 3 + 4] = height;
            v[segments * 2 * 3 + 5] = 0;

            // // The vertex positions of the cylinder
            // float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, // front
            // face
            // -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
            // 1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
            // 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
            // 1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
            // -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face

            // The vertex normals
            float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
                    -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
                    0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
                    1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
                    0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

            // The vertex colors
            float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                    1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                    0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, //
                    0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

            // Texture coordinates
            float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1 };

            // The triangles (three vertex indices for each triangle)
            int indices[] = { 0, 2, 3, 0, 1, 2, // front face
                    4, 6, 7, 4, 5, 6, // left face
                    8, 10, 11, 8, 9, 10, // back face
                    12, 14, 15, 12, 13, 14, // right face
                    16, 18, 19, 16, 17, 18, // top face
                    20, 22, 23, 20, 21, 22 }; // bottom face

            // Construct a data structure that stores the vertices, their
            // attributes, and the triangle mesh connectivity
            VertexData vertexData = Simple.this.renderContext.makeVertexData(24);
            vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
            vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
            vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
            vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
            vertexData.addIndices(indices);

            return vertexData;
        }

        private VertexData makeCube() {
            // Make a simple geometric object: a cube

            // The vertex positions of the cube
            float v[] = { -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, // front face
                    -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, // left face
                    1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, // back face
                    1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, // right face
                    1, 1, 1, 1, 1, -1, -1, 1, -1, -1, 1, 1, // top face
                    -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1 }; // bottom face

            // The vertex normals
            float n[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // front face
                    -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, // left face
                    0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, // back face
                    1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, // right face
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // top face
                    0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0 }; // bottom face

            // The vertex colors
            float c[] = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                    1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
                    0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, //
                    0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, //
                    0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

            // Texture coordinates
            float uv[] = { 0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1, //
                    0, 0, 1, 0, 1, 1, 0, 1 };

            // The triangles (three vertex indices for each triangle)
            int indices[] = { 0, 2, 3, 0, 1, 2, // front face
                    4, 6, 7, 4, 5, 6, // left face
                    8, 10, 11, 8, 9, 10, // back face
                    12, 14, 15, 12, 13, 14, // right face
                    16, 18, 19, 16, 17, 18, // top face
                    20, 22, 23, 20, 21, 22 }; // bottom face

            // Construct a data structure that stores the vertices, their
            // attributes, and the triangle mesh connectivity
            VertexData vertexData = Simple.this.renderContext.makeVertexData(24);
            vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
            vertexData.addElement(v, VertexData.Semantic.POSITION, 3);
            vertexData.addElement(n, VertexData.Semantic.NORMAL, 3);
            vertexData.addElement(uv, VertexData.Semantic.TEXCOORD, 2);
            vertexData.addIndices(indices);

            return vertexData;
        }
    }

    /**
     * A timer task that generates an animation. This task triggers the
     * redrawing of the 3D scene every time it is executed.
     */
    public class AnimationTask extends TimerTask {
        @Override
        public void run() {
            if (Simple.this.shape == null)
                return;
            // Update transformation by rotating with angle "currentstep"
            Matrix4f t = Simple.this.shape.getTransformation();
            Matrix4f rotX = new Matrix4f();
            rotX.rotX(Simple.this.currentstep);
            Matrix4f rotY = new Matrix4f();
            rotY.rotY(Simple.this.currentstep);
            t.mul(rotX);
            t.mul(rotY);
            Simple.this.shape.setTransformation(t);

            // Trigger redrawing of the render window
            Simple.this.renderPanel.getCanvas().repaint();
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
                Simple.this.currentstep = 0;
                break;
            }
            case 'p': {
                // Resume animation
                Simple.this.currentstep = Simple.this.basicstep;
                break;
            }
            case '+': {
                // Accelerate roation
                Simple.this.currentstep += Simple.this.basicstep;
                break;
            }
            case '-': {
                // Slow down rotation
                Simple.this.currentstep -= Simple.this.basicstep;
                break;
            }
            case 'n': {
                // Remove material from shape, and set "normal" shader
                Simple.this.shape.setMaterial(null);
                Simple.this.renderContext.useShader(Simple.this.normalShader);
                break;
            }
            case 'd': {
                // Remove material from shape, and set "default" shader
                Simple.this.shape.setMaterial(null);
                Simple.this.renderContext.useDefaultShader();
                break;
            }
            case 'm': {
                // Set a material for more complex shading of the shape
                if (Simple.this.shape.getMaterial() == null) {
                    Simple.this.shape.setMaterial(Simple.this.material);
                } else {
                    Simple.this.shape.setMaterial(null);
                    Simple.this.renderContext.useDefaultShader();
                }
                break;
            }
            }

            // Trigger redrawing
            Simple.this.renderPanel.getCanvas().repaint();
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
