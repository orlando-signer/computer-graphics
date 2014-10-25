package jrtr;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
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
        // Code to clear the image from
        // http://blog.keilly.com/2007/09/clear-bufferedimage-in-java.html
        Graphics2D g2D = (Graphics2D) colorBuffer.getGraphics();
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, colorBuffer.getWidth(), colorBuffer.getHeight());
        g2D.fill(rect);
    }

    private void endFrame() {
        colorBuffer.flush();
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
        Matrix4f projection = sceneManager.getFrustum().getProjectionMatrix();

        Matrix4f end = new Matrix4f();
        end.mul(viewportTransformation, projection);
        end.mul(camera);
        end.mul(itemTransformation);
        float[] data = positions.getData();
        for (int i = 0; i < data.length; i += 3) {
            Vector4f p = new Vector4f(data[i], data[i + 1], data[i + 2], 1);
            end.transform(p);
            p.scale(1 / p.w);
            if (p.x >= 0 && p.y >= 0 && p.x < colorBuffer.getWidth() && p.y < colorBuffer.getHeight())
                colorBuffer.setRGB((int) p.x, (int) p.y, Integer.MAX_VALUE);
        }
    }

    private Matrix4f getViewpointTransformation(int width, int height) {
        // lecture 3, page 56
        Matrix4f d = new Matrix4f();
        d.setIdentity();
        d.m00 = width / 2F;
        d.m11 = -height / 2F;
        d.m22 = 1 / 2F;
        d.m03 = width / 2F;
        d.m13 = height / 2F;
        d.m23 = 1 / 2F;
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
