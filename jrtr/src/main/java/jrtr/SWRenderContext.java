package jrtr;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import jrtr.VertexData.Semantic;

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
    private float[][] zBuffer;

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
        zBuffer = new float[width][height];
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

        zBuffer = new float[colorBuffer.getWidth()][colorBuffer.getHeight()];
    }

    private void endFrame() {
        colorBuffer.flush();
    }

    /**
     * The main rendering method. You will need to implement this to draw 3D
     * objects.
     */
    private void draw(RenderItem renderItem) {
        Matrix4f itemTransformation = new Matrix4f(renderItem.getT());
        Matrix4f camera = sceneManager.getCamera().getCameraMatrix();
        Matrix4f projection = sceneManager.getFrustum().getProjectionMatrix();

        Matrix4f end = new Matrix4f();
        end.mul(viewportTransformation, projection);
        end.mul(camera);
        end.mul(itemTransformation);
        drawTriangles(renderItem.getShape().getVertexData(), renderItem.getShape().getMaterial(), end);

    }

    private void drawTriangles(VertexData data, Material material, Matrix4f transform) {
        // Variable declarations
        List<VertexData.VertexElement> vertexElements = data.getElements();
        int indices[] = data.getIndices();
        List<Vector4f> positions = new ArrayList<>(3);
        List<Color3f> colors = new ArrayList<>(3);
        List<Vector4f> normals = new ArrayList<>(3);
        List<Point2f> textureCoords = new ArrayList<>(3);
        int k = 0; // index of triangle vertex, k is 0,1, or 2

        // Loop over all vertex indices
        for (int j = 0; j < indices.length; j++) {
            int i = indices[j];
            // Loop over all attributes of current vertex
            ListIterator<VertexData.VertexElement> itr = vertexElements.listIterator(0);
            while (itr.hasNext()) {
                VertexData.VertexElement e = itr.next();
                float[] d = e.getData();
                if (e.getSemantic() == Semantic.POSITION) {
                    Vector4f p = new Vector4f(d[i * 3], d[i * 3 + 1], d[i * 3 + 2], 1);
                    transform.transform(p);
                    positions.add(p);
                } else if (e.getSemantic() == Semantic.COLOR) {
                    Color3f c = new Color3f(d[i * 3], d[i * 3 + 1], d[i * 3 + 2]);
                    colors.add(c);
                } else if (e.getSemantic() == Semantic.NORMAL) {
                    Vector4f n = new Vector4f(d[i * 3], d[i * 3 + 1], d[i * 3 + 2], 1);
                    normals.add(n);
                } else if (e.getSemantic() == Semantic.TEXCOORD) {
                    Point2f p = new Point2f(d[i * 2], d[i * 2 + 1]);
                    textureCoords.add(p);
                }

            }
            if (k == 2) {
                // Draw the triangle with the collected three vertex
                // positions, etc.
                rasterizeTriangle(positions, colors, normals, textureCoords, material);
                k = 0;
                positions.clear();
                colors.clear();
                normals.clear();
                textureCoords.clear();
            } else {
                k++;
            }
        }
    }

    private void rasterizeTriangle(List<Vector4f> positions, List<Color3f> colors, List<Vector4f> normals,
            List<Point2f> textureCoords, Material material) {
        // rasterization, page 24
        Matrix3f edge = new Matrix3f();
        for (int i = 0; i < 3; i++)
            edge.setRow(i, positions.get(i).x, positions.get(i).y, positions.get(i).w);
        edge.invert();

        // Bounding box berechnen
        positions.forEach(p -> p.scale(1 / p.w));
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Vector4f p : positions) {
            minX = p.x < minX ? (int) p.x : minX;
            maxX = p.x > maxX ? (int) p.x : maxX;
            minY = p.y < minY ? (int) p.y : minY;
            maxY = p.y > maxY ? (int) p.y : maxY;
        }
        minX = Math.max(0, minX);
        maxX = Math.min(maxX, colorBuffer.getWidth());
        minY = Math.max(0, minY);
        maxY = Math.min(maxY, colorBuffer.getHeight());

        Matrix3f edgeTranspose = new Matrix3f(edge);
        edgeTranspose.transpose();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                Vector3f v = new Vector3f(1, 1, 1);
                edge.transform(v);
                float w = v.x * x + v.y * y + v.z;
                Vector3f p = new Vector3f(x, y, 1);
                edgeTranspose.transform(p);
                p.scale(1 / (p.x + p.y + p.z));

                if (p.x > 0 && p.y > 0 && p.z > 0) {
                    if (zBuffer[x][y] < w) {
                        int rgb;
                        if (material != null && material.texture != null)
                            rgb = getTextureColor(textureCoords, material, p);
                        else
                            rgb = getColor(colors, p).get().getRGB();

                        colorBuffer.setRGB(x, y, rgb);
                        zBuffer[x][y] = w;
                    }
                }
            }
        }
    }

    private int getTextureColor(List<Point2f> textureCoords, Material material, Vector3f p) {
        Point2f point = new Point2f();
        point.scaleAdd(p.x, textureCoords.get(0), point);
        point.scaleAdd(p.y, textureCoords.get(1), point);
        point.scaleAdd(p.z, textureCoords.get(2), point);
        point.y = 1 - point.y;
        BufferedImage img = ((SWTexture) material.texture).getTexture();

        // evaluate nearest neightbour
//        return nearestNeighbour(point, img);
        // evaluate bilinear
         return bilinear(p, point, img);
    }

    private int bilinear(Vector3f p, Point2f point, BufferedImage img) {
        point.x = Math.min(point.x * img.getWidth(), img.getWidth() - 2);
        point.y = Math.min(point.y * img.getHeight(), img.getHeight() - 2);

        Color3f[] colors = new Color3f[4];
        colors[0] = new Color3f(new Color(img.getRGB((int) point.x, (int) point.y)));
        colors[1] = new Color3f(new Color(img.getRGB((int) point.x + 1, (int) point.y)));
        colors[2] = new Color3f(new Color(img.getRGB((int) point.x, (int) point.y + 1)));
        colors[3] = new Color3f(new Color(img.getRGB((int) point.x + 1, (int) point.y + 1)));

        float w = (point.x - (int) point.x);
        Color3f c1 = new Color3f();
        c1.scale(1 - w, colors[0]);
        c1.scaleAdd(w, colors[1], c1);

        Color3f c2 = new Color3f();
        c2.scale(1 - w, colors[2]);
        c2.scaleAdd(w, colors[3], c2);

        w = (point.y - (int) point.y);
        Color3f finalColor = new Color3f();
        finalColor.scale(1 - w, c1);
        finalColor.scaleAdd(w, c2, finalColor);

        return finalColor.get().getRGB();
    }

    private int nearestNeighbour(Point2f point, BufferedImage img) {
        point.x = Math.min(point.x * img.getWidth(), img.getWidth() - 1);
        point.y = Math.min(point.y * img.getHeight(), img.getHeight() - 1);

        point.x = Math.round(point.x);
        point.y = Math.round(point.y);

        int rgb = img.getRGB((int) point.x, (int) point.y);
        return rgb;
    }

    private Color3f getColor(List<Color3f> colors, Vector3f p) {
        Color3f color = new Color3f();
        color.scaleAdd(p.x, colors.get(0), color);
        color.scaleAdd(p.y, colors.get(1), color);
        color.scaleAdd(p.z, colors.get(2), color);
        color.scale(10); // Scale colors so they are not as dark
        color.x = colorCheck(color.x);
        color.y = colorCheck(color.y);
        color.z = colorCheck(color.z);
        return color;
    }

    private float colorCheck(float c) {
        return (c < 0) ? 0 : ((c > 1) ? 1 : c);
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
