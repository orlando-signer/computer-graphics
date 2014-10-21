package jrtr;

import java.awt.image.BufferedImage;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import jrtr.VertexData.Semantic;
import jrtr.VertexData.VertexElement;

/**
 * A skeleton for a software renderer. It works in combination with
 * {@link SWRenderPanel}, which displays the output image. In project 3 you will
 * implement your own rasterizer in this class.
 * <p>
 * To use the software renderer, you will simply replace {@link GLRenderPanel}
 * with {@link SWRenderPanel} in the user application.
 */
public class SWRenderContext implements RenderContext {

    private SceneManagerInterface sceneManager;
    private BufferedImage colorBuffer;
    private Matrix4f viewportTransformation;

    @Override
    public void setSceneManager(SceneManagerInterface sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * This is called by the SWRenderPanel to render the scene to the software
     * frame buffer.
     */
    public void display() {
        if (sceneManager == null)
            return;

        beginFrame();

        SceneManagerIterator iterator = sceneManager.iterator();
        while (iterator.hasNext()) {
            draw(iterator.next());
        }

        endFrame();
    }

    /**
     * This is called by the {@link SWJPanel} to obtain the color buffer that
     * will be displayed.
     */
    public BufferedImage getColorBuffer() {
        return colorBuffer;
    }

    /**
     * Set a new viewport size. The render context will also need to store a
     * viewport matrix, which you need to reset here.
     */
    public void setViewportSize(int width, int height) {
        colorBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        viewportTransformation = getViewpointTransformation(width, height);
    }

    /**
     * Clear the framebuffer here.
     */
    private void beginFrame() {
    }

    private void endFrame() {
    }

    /**
     * The main rendering method. You will need to implement this to draw 3D
     * objects.
     */
    private void draw(RenderItem renderItem) {
        VertexElement positions = renderItem.getShape().getVertexData().getElements().stream()
                .filter(e -> e.getSemantic() == Semantic.POSITION).findFirst().get();
        Matrix4f itemTransformation = new Matrix4f(renderItem.getT());
        Matrix4f camera = sceneManager.getCamera().getCameraMatrix();
        camera.invert();
        Matrix4f projection = sceneManager.getFrustum().getProjectionMatrix();

        Matrix4f end = new Matrix4f();
        end.mul(viewportTransformation, projection);
        end.mul(camera);
        end.mul(itemTransformation);
        float[] data = positions.getData();
        for (int i = 0; i < data.length / 3; i += 3) {
            Vector4f p = new Vector4f(data[i], data[i + 1], data[i + 2], 1);
            Matrix4f tmp = new Matrix4f();
            tmp.setZero();
            tmp.setColumn(0, p);
            tmp.mul(end, tmp);
            tmp.getColumn(0, p);
            p.scale(1 / p.w);
            System.out.println(p);
            if (p.x > 0 && p.y > 0) // check so it doesnt crash
                colorBuffer.setRGB((int) p.x, (int) p.y, Integer.MAX_VALUE);
        }
    }

    private Matrix4f getViewpointTransformation(int width, int height) {
        Matrix4f d = new Matrix4f();
        d.setIdentity();
        d.m00 = (width) / 2;
        d.m11 = (height) / 2;
        d.m22 = (float) 1 / 2;
        d.m03 = (width) / 2;
        d.m13 = (height) / 2;
        d.m23 = (float) 1 / 2;
        return d;
    }

    /**
     * Does nothing. We will not implement shaders for the software renderer.
     */
    @Override
    public Shader makeShader() {
        return new SWShader();
    }

    /**
     * Does nothing. We will not implement shaders for the software renderer.
     */
    @Override
    public void useShader(Shader s) {
    }

    /**
     * Does nothing. We will not implement shaders for the software renderer.
     */
    @Override
    public void useDefaultShader() {
    }

    /**
     * Does nothing. We will not implement textures for the software renderer.
     */
    @Override
    public Texture makeTexture() {
        return new SWTexture();
    }

    @Override
    public VertexData makeVertexData(int n) {
        return new SWVertexData(n);
    }
}
