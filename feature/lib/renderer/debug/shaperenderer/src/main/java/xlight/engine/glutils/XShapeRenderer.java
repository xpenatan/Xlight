package xlight.engine.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import xlight.engine.math.XMath;
import xlight.engine.math.XRotSeq;
import xlight.engine.math.XRotationUtils;

public class XShapeRenderer implements Disposable {

    private boolean isDepthTest;

    public void boundingBox(BoundingBox boundingBox) {
        float width = boundingBox.getWidth();
        float height = boundingBox.getHeight();
        float depth = boundingBox.getDepth();

        float x = boundingBox.min.x;
        float y = boundingBox.min.y;
        float z = boundingBox.min.z;
        boundingBox(boundingBox.min.x, boundingBox.min.y, boundingBox.min.z, boundingBox.max.x, boundingBox.max.y, boundingBox.max.z);
    }

    public void triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
        triangle(getCurrentType(), v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z);
    }

    public void triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        triangle(getCurrentType(), x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

    public void triangle(ShapeType shapeType, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        ImmediateModeRenderer renderer = getRenderer();
        Color color = getColor();

        check(ShapeType.Line, ShapeType.Filled, 6);
        float colorBits = color.toFloatBits();
        if(shapeType == ShapeType.Line) {
            renderer.color(colorBits);
            renderer.vertex(x1, y1, z1);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, z2);

            renderer.color(colorBits);
            renderer.vertex(x2, y2, z2);
            renderer.color(colorBits);
            renderer.vertex(x3, y3, z3);

            renderer.color(colorBits);
            renderer.vertex(x3, y3, z3);
            renderer.color(colorBits);
            renderer.vertex(x1, y1, z1);
        }
        else {
            renderer.color(colorBits);
            renderer.vertex(x1, y1, z1);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, z2);
            renderer.color(colorBits);
            renderer.vertex(x3, y3, z3);
        }
    }

    private final XImmediateModeRenderer20 renderer;
    private boolean matrixDirty = false;
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();
    private final Vector2 tmp = new Vector2();
    private final Color color = new Color(1, 1, 1, 1);
    private ShapeType shapeType;
    private boolean autoShapeType;
    private float defaultRectLineWidth = 0.75f;

    public XShapeRenderer() {
        this(5000);
    }

    public XShapeRenderer(int maxVertices) {
        this(maxVertices, null);
    }

    public XShapeRenderer(int maxVertices, ShaderProgram defaultShader) {
        if(defaultShader == null) {
            renderer = new XImmediateModeRenderer20(maxVertices, false, true, 0);
        }
        else {
            renderer = new XImmediateModeRenderer20(maxVertices, false, true, 0, defaultShader);
        }
        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        matrixDirty = true;
    }

    /**
     * Sets the color to be used by the next shapes drawn.
     */
    public void setColor(Color color) {
        this.color.set(color);
    }

    /**
     * Sets the color to be used by the next shapes drawn.
     */
    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    /**
     * Sets RGBA8888 color to be used by the next shapes drawn.
     */
    public void setColor(int color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color;
    }

    public void updateMatrices() {
        matrixDirty = true;
    }

    /**
     * Sets the projection matrix to be used for rendering. Usually this will be set to {@link Camera#combined}.
     *
     * @param matrix
     */
    public void setProjectionMatrix(Matrix4 matrix) {
        projectionMatrix.set(matrix);
        matrixDirty = true;
    }

    /**
     * If the matrix is modified, {@link #updateMatrices()} must be called.
     */
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setTransformMatrix(Matrix4 matrix) {
        transformMatrix.set(matrix);
        matrixDirty = true;
    }

    /**
     * If the matrix is modified, {@link #updateMatrices()} must be called.
     */
    public Matrix4 getTransformMatrix() {
        return transformMatrix;
    }

    /**
     * Sets the transformation matrix to identity.
     */
    public void identity() {
        transformMatrix.idt();
        matrixDirty = true;
    }

    /**
     * Multiplies the current transformation matrix by a translation matrix.
     */
    public void translate(float x, float y, float z) {
        transformMatrix.translate(x, y, z);
        matrixDirty = true;
    }

    /**
     * Multiplies the current transformation matrix by a rotation matrix.
     */
    public void rotate(float axisX, float axisY, float axisZ, float degrees) {
        transformMatrix.rotate(axisX, axisY, axisZ, degrees);
        matrixDirty = true;
    }

    /**
     * Multiplies the current transformation matrix by a scale matrix.
     */
    public void scale(float scaleX, float scaleY, float scaleZ) {
        transformMatrix.scale(scaleX, scaleY, scaleZ);
        matrixDirty = true;
    }

    /**
     * If true, when drawing a shape cannot be performed with the current shape type, the batch is flushed and the shape type is
     * changed automatically. This can increase the number of batch flushes if care is not taken to draw the same type of shapes
     * together. Default is false.
     */
    public void setAutoShapeType(boolean autoShapeType) {
        this.autoShapeType = autoShapeType;
    }

    /**
     * Begins a new batch without specifying a shape type.
     *
     * @throws IllegalStateException if {@link #autoShapeType} is false.
     */
    public void begin() {
        if(!autoShapeType) throw new IllegalStateException("autoShapeType must be true to use this method.");
        begin(ShapeType.Line);
    }

    /**
     * Starts a new batch of shapes. Shapes drawn within the batch will attempt to use the type specified. The call to this method
     * must be paired with a call to {@link #end()}.
     *
     * @see #setAutoShapeType(boolean)
     */
    public void begin(ShapeType type) {
        shapeType = type;
        if(matrixDirty) {
            combinedMatrix.set(projectionMatrix);
            Matrix4.mul(combinedMatrix.val, transformMatrix.val);
            matrixDirty = false;
        }
        renderer.begin(combinedMatrix, shapeType.getGlType());
    }

    public void beginDepth(ShapeType type) {
        isDepthTest = Gdx.gl.glIsEnabled(GL20.GL_DEPTH_TEST);
        if(!isDepthTest) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        }
        begin(type);
    }

    public void endDepth() {
        end();
        if(!isDepthTest) {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }
    }

    public void set(ShapeType type) {
        this.shapeType = type;
    }

    /**
     * Draws a point using {@link ShapeType#Point}, {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void point(float x, float y, float z) {
        if(shapeType == ShapeType.Line) {
            float size = defaultRectLineWidth * 0.5f;
            line(x - size, y - size, z, x + size, y + size, z);
            return;
        }
        else if(shapeType == ShapeType.Filled) {
            float size = defaultRectLineWidth * 0.5f;
            box(x - size, y - size, z - size, defaultRectLineWidth, defaultRectLineWidth, defaultRectLineWidth);
            return;
        }
        check(ShapeType.Point, null, 1);
        renderer.color(color);
        renderer.vertex(x, y, z);
    }

    /**
     * Draws a line using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public final void line(float x, float y, float z, float x2, float y2, float z2) {
        line(x, y, z, x2, y2, z2, color, color);
    }

    /**
     * Custom code
     */
    public final void line(ShapeType shapeType, float x, float y, float z, float x2, float y2, float z2) {
        line(x, y, z, x2, y2, z2, color, color);
    }

    /**
     * @see #line(float, float, float, float, float, float)
     */
    public final void line(Vector3 v0, Vector3 v1) {
        line(v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, color, color);
    }

    /**
     * @see #line(float, float, float, float, float, float)
     */
    public final void line(float x, float y, float x2, float y2) {
        line(x, y, 0.0f, x2, y2, 0.0f, color, color);
    }

    /**
     * @see #line(float, float, float, float, float, float)
     */
    public final void line(Vector2 v0, Vector2 v1) {
        line(v0.x, v0.y, 0.0f, v1.x, v1.y, 0.0f, color, color);
    }

    /**
     * @see #line(float, float, float, float, float, float, Color, Color)
     */
    public final void line(float x, float y, float x2, float y2, Color c1, Color c2) {
        line(x, y, 0.0f, x2, y2, 0.0f, c1, c2);
    }

    /**
     * Draws a line using {@link ShapeType#Line} or {@link ShapeType#Filled}. The line is drawn with two colors interpolated
     * between the start and end points.
     */
    public void line(float x, float y, float z, float x2, float y2, float z2, Color c1, Color c2) {
        check(ShapeType.Line, null, 2);
        renderer.color(c1.r, c1.g, c1.b, c1.a);
        renderer.vertex(x, y, z);
        renderer.color(c2.r, c2.g, c2.b, c2.a);
        renderer.vertex(x2, y2, z2);
    }

    /**
     * Draws a curve using {@link ShapeType#Line}.
     */
    public void curve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, int segments) {
        check(ShapeType.Line, null, segments * 2 + 2);
        float colorBits = color.toFloatBits();

        // Algorithm from: http://www.antigrain.com/research/bezier_interpolation/index.html#PAGE_BEZIER_INTERPOLATION
        float subdiv_step = 1f / segments;
        float subdiv_step2 = subdiv_step * subdiv_step;
        float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

        float pre1 = 3 * subdiv_step;
        float pre2 = 3 * subdiv_step2;
        float pre4 = 6 * subdiv_step2;
        float pre5 = 6 * subdiv_step3;

        float tmp1x = x1 - cx1 * 2 + cx2;
        float tmp1y = y1 - cy1 * 2 + cy2;

        float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
        float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

        float fx = x1;
        float fy = y1;

        float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
        float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

        float ddfx = tmp1x * pre4 + tmp2x * pre5;
        float ddfy = tmp1y * pre4 + tmp2y * pre5;

        float dddfx = tmp2x * pre5;
        float dddfy = tmp2y * pre5;

        while(segments-- > 0) {
            renderer.color(colorBits);
            renderer.vertex(fx, fy, 0);
            fx += dfx;
            fy += dfy;
            dfx += ddfx;
            dfy += ddfy;
            ddfx += dddfx;
            ddfy += dddfy;
            renderer.color(colorBits);
            renderer.vertex(fx, fy, 0);
        }
        renderer.color(colorBits);
        renderer.vertex(fx, fy, 0);
        renderer.color(colorBits);
        renderer.vertex(x2, y2, 0);
    }

    /**
     * Draws a triangle in x/y plane using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        check(ShapeType.Line, ShapeType.Filled, 6);
        float colorBits = color.toFloatBits();
        if(shapeType == ShapeType.Line) {
            renderer.color(colorBits);
            renderer.vertex(x1, y1, 0);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, 0);

            renderer.color(colorBits);
            renderer.vertex(x2, y2, 0);
            renderer.color(colorBits);
            renderer.vertex(x3, y3, 0);

            renderer.color(colorBits);
            renderer.vertex(x3, y3, 0);
            renderer.color(colorBits);
            renderer.vertex(x1, y1, 0);
        }
        else {
            renderer.color(colorBits);
            renderer.vertex(x1, y1, 0);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, 0);
            renderer.color(colorBits);
            renderer.vertex(x3, y3, 0);
        }
    }

    /**
     * Draws a triangle in x/y plane with colored corners using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, Color col1, Color col2, Color col3) {
        check(ShapeType.Line, ShapeType.Filled, 6);
        if(shapeType == ShapeType.Line) {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);

            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);

            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
        }
        else {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);
        }
    }

    /**
     * Draws a rectangle in the x/y plane using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void rect(float x, float y, float width, float height) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float colorBits = color.toFloatBits();
        if(shapeType == ShapeType.Line) {
            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, 0);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, 0);

            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, 0);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, 0);

            renderer.color(colorBits);
            renderer.vertex(x, y + height, 0);
            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
        }
        else {
            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, 0);

            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, 0);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, 0);
            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
        }
    }

    /**
     * Draws a rectangle in the x/y plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. The x and y specify the lower
     * left corner.
     *
     * @param col1 The color at (x, y).
     * @param col2 The color at (x + width, y).
     * @param col3 The color at (x + width, y + height).
     * @param col4 The color at (x, y + height).
     */
    public void rect(float x, float y, float width, float height, Color col1, Color col2, Color col3, Color col4) {
        check(ShapeType.Line, ShapeType.Filled, 8);

        if(shapeType == ShapeType.Line) {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x, y, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x + width, y, 0);

            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x + width, y, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x + width, y + height, 0);

            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x + width, y + height, 0);
            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x, y + height, 0);

            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x, y + height, 0);
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x, y, 0);
        }
        else {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x, y, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x + width, y, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x + width, y + height, 0);

            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x + width, y + height, 0);
            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x, y + height, 0);
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x, y, 0);
        }
    }

    /**
     * Draws a rectangle in the x/y plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. The x and y specify the lower
     * left corner. The originX and originY specify the point about which to rotate the rectangle.
     */
    public void rect(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY,
                     float degrees) {
        rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees, color, color, color, color);
    }

    /**
     * Draws a rectangle in the x/y plane using {@link ShapeType#Line} or {@link ShapeType#Filled}. The x and y specify the lower
     * left corner. The originX and originY specify the point about which to rotate the rectangle.
     *
     * @param col1 The color at (x, y)
     * @param col2 The color at (x + width, y)
     * @param col3 The color at (x + width, y + height)
     * @param col4 The color at (x, y + height)
     */
    public void rect(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY,
                     float degrees, Color col1, Color col2, Color col3, Color col4) {
        check(ShapeType.Line, ShapeType.Filled, 8);

        float cos = MathUtils.cosDeg(degrees);
        float sin = MathUtils.sinDeg(degrees);
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        if(scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        float worldOriginX = x + originX;
        float worldOriginY = y + originY;

        float x1 = cos * fx - sin * fy + worldOriginX;
        float y1 = sin * fx + cos * fy + worldOriginY;

        float x2 = cos * fx2 - sin * fy + worldOriginX;
        float y2 = sin * fx2 + cos * fy + worldOriginY;

        float x3 = cos * fx2 - sin * fy2 + worldOriginX;
        float y3 = sin * fx2 + cos * fy2 + worldOriginY;

        float x4 = x1 + (x3 - x2);
        float y4 = y3 - (y2 - y1);

        if(shapeType == ShapeType.Line) {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);

            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);

            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);
            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x4, y4, 0);

            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x4, y4, 0);
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
        }
        else {
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
            renderer.color(col2.r, col2.g, col2.b, col2.a);
            renderer.vertex(x2, y2, 0);
            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);

            renderer.color(col3.r, col3.g, col3.b, col3.a);
            renderer.vertex(x3, y3, 0);
            renderer.color(col4.r, col4.g, col4.b, col4.a);
            renderer.vertex(x4, y4, 0);
            renderer.color(col1.r, col1.g, col1.b, col1.a);
            renderer.vertex(x1, y1, 0);
        }
    }

    /**
     * Draws a line using a rotated rectangle, where with one edge is centered at x1, y1 and the opposite edge centered at x2,
     * y2.
     */
    public void rectLine(float x1, float y1, float x2, float y2, float width) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float colorBits = color.toFloatBits();
        Vector2 t = tmp.set(y2 - y1, x1 - x2).nor();
        width *= 0.5f;
        float tx = t.x * width;
        float ty = t.y * width;
        if(shapeType == ShapeType.Line) {
            renderer.color(colorBits);
            renderer.vertex(x1 + tx, y1 + ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x1 - tx, y1 - ty, 0);

            renderer.color(colorBits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x2 - tx, y2 - ty, 0);

            renderer.color(colorBits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x1 + tx, y1 + ty, 0);

            renderer.color(colorBits);
            renderer.vertex(x2 - tx, y2 - ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
        }
        else {
            renderer.color(colorBits);
            renderer.vertex(x1 + tx, y1 + ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x2 + tx, y2 + ty, 0);

            renderer.color(colorBits);
            renderer.vertex(x2 - tx, y2 - ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(colorBits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
        }
    }

    /**
     * Draws a line using a rotated rectangle, where with one edge is centered at x1, y1 and the opposite edge centered at x2,
     * y2.
     */
    public void rectLine(float x1, float y1, float x2, float y2, float width, Color c1, Color c2) {
        check(ShapeType.Line, ShapeType.Filled, 8);
        float col1Bits = c1.toFloatBits();
        float col2Bits = c2.toFloatBits();
        Vector2 t = tmp.set(y2 - y1, x1 - x2).nor();
        width *= 0.5f;
        float tx = t.x * width;
        float ty = t.y * width;
        if(shapeType == ShapeType.Line) {
            renderer.color(col1Bits);
            renderer.vertex(x1 + tx, y1 + ty, 0);
            renderer.color(col1Bits);
            renderer.vertex(x1 - tx, y1 - ty, 0);

            renderer.color(col2Bits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(col2Bits);
            renderer.vertex(x2 - tx, y2 - ty, 0);

            renderer.color(col2Bits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(col1Bits);
            renderer.vertex(x1 + tx, y1 + ty, 0);

            renderer.color(col2Bits);
            renderer.vertex(x2 - tx, y2 - ty, 0);
            renderer.color(col1Bits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
        }
        else {
            renderer.color(col1Bits);
            renderer.vertex(x1 + tx, y1 + ty, 0);
            renderer.color(col1Bits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
            renderer.color(col2Bits);
            renderer.vertex(x2 + tx, y2 + ty, 0);

            renderer.color(col2Bits);
            renderer.vertex(x2 - tx, y2 - ty, 0);
            renderer.color(col2Bits);
            renderer.vertex(x2 + tx, y2 + ty, 0);
            renderer.color(col1Bits);
            renderer.vertex(x1 - tx, y1 - ty, 0);
        }
    }

    /**
     * @see #rectLine(float, float, float, float, float)
     */
    public void rectLine(Vector2 p1, Vector2 p2, float width) {
        rectLine(p1.x, p1.y, p2.x, p2.y, width);
    }

    public void boundingBox(Vector3 min, Vector3 max) {
        boundingBox(min.x, min.y, min.z, max.x, max.y, max.z, null, 0, 0, 0);
    }

    public void boundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        boundingBox(minX, minY, minZ, maxX, maxY, maxZ, null, 0, 0, 0);
    }

    public void boundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, XRotSeq rotSeq, float rX, float rY, float rZ) {
        float colorBits = color.toFloatBits();

        XMath.BOUNDING_BOX_1.min.set(minX, minY, minZ);
        XMath.BOUNDING_BOX_1.max.set(maxX, maxY, maxZ);

        float centerX = XMath.BOUNDING_BOX_1.getCenterX();
        float centerY = XMath.BOUNDING_BOX_1.getCenterY();
        float centerZ = XMath.BOUNDING_BOX_1.getCenterZ();

        if(shapeType == ShapeType.Line) {
            check(ShapeType.Line, ShapeType.Filled, 24);

            // BOTTOM
            renderer.color(colorBits);
            rotateVertex(minX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(minX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            // TOP
            renderer.color(colorBits);
            rotateVertex(minX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(minX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            // Bottom/Top line

            renderer.color(colorBits);
            rotateVertex(minX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, minY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, maxY, minZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(maxX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(maxX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);

            renderer.color(colorBits);
            rotateVertex(minX, minY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
            renderer.color(colorBits);
            rotateVertex(minX, maxY, maxZ, centerX, centerY, centerZ, rotSeq, rX, rY, rZ);
        }
        else {
            check(ShapeType.Line, ShapeType.Filled, 36);

            // Front
            renderer.color(colorBits);
            renderer.vertex(minX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, minZ);

            renderer.color(colorBits);
            renderer.vertex(minX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, minZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, minZ);

            // Back
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);

            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, maxZ);

            // Left
            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, minZ);

            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, minZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, maxZ);

            // Right
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);

            renderer.color(colorBits);
            renderer.vertex(maxX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, minZ);

            // Top
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);

            renderer.color(colorBits);
            renderer.vertex(minX, maxY, minZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, maxY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(minX, maxY, maxZ);

            // Bottom
            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, minZ);

            renderer.color(colorBits);
            renderer.vertex(minX, minY, maxZ);
            renderer.color(colorBits);
            renderer.vertex(maxX, minY, minZ);
            renderer.color(colorBits);
            renderer.vertex(minX, minY, minZ);
        }
    }

    private void rotateVertex(float x, float y, float z, float centerX, float centerY, float centerZ, XRotSeq rotSeq, float rX, float rY, float rZ) {
        if(rotSeq != null) {

            float centerXX = centerX;
            float centerYY = centerY;
            float centerZZ = centerZ;
            if(centerX > 0) {
                centerXX *= -1;
            }
            if(centerY > 0) {
                centerYY *= -1;
            }
            if(centerZ > 0) {
                centerZZ *= -1;
            }

            x = x + centerXX;
            y = y + centerYY;
            z = z + centerZZ;

            XMath.MAT4_1.idt();
            XRotationUtils.rotateMatrix(rotSeq, rX, rY, rZ, XMath.MAT4_1, false);
            XMath.MAT4_1.translate(x, y, z);
            XMath.MAT4_1.getTranslation(XMath.VEC3_1);

            x = XMath.VEC3_1.x + centerX;
            y = XMath.VEC3_1.y + centerY;
            z = XMath.VEC3_1.z + centerZ;
            renderer.vertex(x, y, z);
        }
        else {
            renderer.vertex(x, y, z);
        }
    }

    /**
     * Draws a cube using {@link ShapeType#Line} or {@link ShapeType#Filled}. The x, y and z specify the bottom, left, front
     * corner of the rectangle.
     */
    public void box(float x, float y, float z, float width, float height, float depth) {
        depth = -depth;
        float colorBits = color.toFloatBits();
        if(shapeType == ShapeType.Line) {
            check(ShapeType.Line, ShapeType.Filled, 24);

            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y, z);

            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);
        }
        else {
            check(ShapeType.Line, ShapeType.Filled, 36);

            // Front
            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);

            // Back
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);

            // Left
            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);

            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);

            // Right
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);

            // Top
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);

            renderer.color(colorBits);
            renderer.vertex(x, y + height, z);
            renderer.color(colorBits);
            renderer.vertex(x + width, y + height, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x, y + height, z + depth);

            // Bottom
            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);

            renderer.color(colorBits);
            renderer.vertex(x, y, z + depth);
            renderer.color(colorBits);
            renderer.vertex(x + width, y, z);
            renderer.color(colorBits);
            renderer.vertex(x, y, z);
        }
    }

    /**
     * Draws two crossed lines using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void x(float x, float y, float size) {
        line(x - size, y - size, x + size, y + size);
        line(x - size, y + size, x + size, y - size);
    }

    /**
     * @see #x(float, float, float)
     */
    public void x(Vector2 p, float size) {
        x(p.x, p.y, size);
    }

    /**
     * Calls {@link #arc(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth arc.
     */
    public void arc(float x, float y, float radius, float start, float degrees) {
        arc(x, y, radius, start, degrees, Math.max(1, (int)(6 * (float)Math.cbrt(radius) * (degrees / 360.0f))));
    }

    /**
     * Draws an arc using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void arc(float x, float y, float radius, float start, float degrees, int segments) {
        if(segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        float colorBits = color.toFloatBits();
        float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
        float cos = MathUtils.cos(theta);
        float sin = MathUtils.sin(theta);
        float cx = radius * MathUtils.cos(start * MathUtils.degreesToRadians);
        float cy = radius * MathUtils.sin(start * MathUtils.degreesToRadians);

        if(shapeType == ShapeType.Line) {
            check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);

            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, 0);
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, 0);
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, 0);
            }
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, 0);
        }
        else {
            check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);

            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x, y, 0);
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, 0);
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, 0);
            }
            renderer.color(colorBits);
            renderer.vertex(x, y, 0);
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, 0);
        }

        float temp = cx;
        cx = 0;
        cy = 0;
        renderer.color(colorBits);
        renderer.vertex(x + cx, y + cy, 0);
    }

    public void circle(float x, float y, float z, float radius) {
        circle(x, y, z, radius, Math.max(1, (int)(6 * (float)Math.cbrt(radius))));
    }

    public void circle(float x, float y, float z, float radius, int segments) {
        if(segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        float colorBits = color.toFloatBits();
        float angle = 2 * MathUtils.PI / segments;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float cx = radius, cy = 0;
        if(shapeType == ShapeType.Line) {
            check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
            }
            // Ensure the last segment is identical to the first.
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, z);
        }
        else {
            check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);
            segments--;
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x, y, z);
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
            }
            // Ensure the last segment is identical to the first.
            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, z);
        }

        float temp = cx;
        cx = radius;
        cy = 0;
        renderer.color(colorBits);
        renderer.vertex(x + cx, y + cy, z);
    }

    /**
     * Calls {@link #ellipse(float, float, float, float, int)} by estimating the number of segments needed for a smooth
     * ellipse.
     */
    public void ellipse(float x, float y, float width, float height) {
        ellipse(x, y, width, height, Math.max(1, (int)(12 * (float)Math.cbrt(Math.max(width * 0.5f, height * 0.5f)))));
    }

    /**
     * Draws an ellipse using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void ellipse(float x, float y, float width, float height, int segments) {
        if(segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        check(ShapeType.Line, ShapeType.Filled, segments * 3);
        float colorBits = color.toFloatBits();
        float angle = 2 * MathUtils.PI / segments;

        float cx = x + width / 2, cy = y + height / 2;
        if(shapeType == ShapeType.Line) {
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(cx + (width * 0.5f * MathUtils.cos(i * angle)), cy + (height * 0.5f * MathUtils.sin(i * angle)), 0);

                renderer.color(colorBits);
                renderer.vertex(cx + (width * 0.5f * MathUtils.cos((i + 1) * angle)),
                        cy + (height * 0.5f * MathUtils.sin((i + 1) * angle)), 0);
            }
        }
        else {
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(cx + (width * 0.5f * MathUtils.cos(i * angle)), cy + (height * 0.5f * MathUtils.sin(i * angle)), 0);

                renderer.color(colorBits);
                renderer.vertex(cx, cy, 0);

                renderer.color(colorBits);
                renderer.vertex(cx + (width * 0.5f * MathUtils.cos((i + 1) * angle)),
                        cy + (height * 0.5f * MathUtils.sin((i + 1) * angle)), 0);
            }
        }
    }

    /**
     * Calls {@link #ellipse(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth
     * ellipse.
     */
    public void ellipse(float x, float y, float width, float height, float rotation) {
        ellipse(x, y, width, height, rotation, Math.max(1, (int)(12 * (float)Math.cbrt(Math.max(width * 0.5f, height * 0.5f)))));
    }

    /**
     * Draws an ellipse using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void ellipse(float x, float y, float width, float height, float rotation, int segments) {
        if(segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        check(ShapeType.Line, ShapeType.Filled, segments * 3);
        float colorBits = color.toFloatBits();
        float angle = 2 * MathUtils.PI / segments;

        rotation = MathUtils.PI * rotation / 180f;
        float sin = MathUtils.sin(rotation);
        float cos = MathUtils.cos(rotation);

        float cx = x + width / 2, cy = y + height / 2;
        float x1 = width * 0.5f;
        float y1 = 0;
        if(shapeType == ShapeType.Line) {
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, 0);

                x1 = (width * 0.5f * MathUtils.cos((i + 1) * angle));
                y1 = (height * 0.5f * MathUtils.sin((i + 1) * angle));

                renderer.color(colorBits);
                renderer.vertex(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, 0);
            }
        }
        else {
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, 0);

                renderer.color(colorBits);
                renderer.vertex(cx, cy, 0);

                x1 = (width * 0.5f * MathUtils.cos((i + 1) * angle));
                y1 = (height * 0.5f * MathUtils.sin((i + 1) * angle));

                renderer.color(colorBits);
                renderer.vertex(cx + cos * x1 - sin * y1, cy + sin * x1 + cos * y1, 0);
            }
        }
    }

    /**
     * Calls {@link #cone(float, float, float, float, float, int)} by estimating the number of segments needed for a smooth
     * circular base.
     */
    public void cone(float x, float y, float z, float radius, float height) {
        cone(x, y, z, radius, height, Math.max(1, (int)(4 * (float)Math.sqrt(radius))));
    }

    /**
     * Draws a cone using {@link ShapeType#Line} or {@link ShapeType#Filled}.
     */
    public void cone(float x, float y, float z, float radius, float height, int segments) {
        if(segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
        check(ShapeType.Line, ShapeType.Filled, segments * 4 + 2);
        float colorBits = color.toFloatBits();
        float angle = 2 * MathUtils.PI / segments;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float cx = radius, cy = 0;
        if(shapeType == ShapeType.Line) {
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                renderer.color(colorBits);
                renderer.vertex(x, y, z + height);
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                float temp = cx;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
            }
            // Ensure the last segment is identical to the first.
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, z);
        }
        else {
            segments--;
            for(int i = 0; i < segments; i++) {
                renderer.color(colorBits);
                renderer.vertex(x, y, z);
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                float temp = cx;
                float temp2 = cy;
                cx = cos * cx - sin * cy;
                cy = sin * temp + cos * cy;
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);

                renderer.color(colorBits);
                renderer.vertex(x + temp, y + temp2, z);
                renderer.color(colorBits);
                renderer.vertex(x + cx, y + cy, z);
                renderer.color(colorBits);
                renderer.vertex(x, y, z + height);
            }
            // Ensure the last segment is identical to the first.
            renderer.color(colorBits);
            renderer.vertex(x, y, z);
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, z);
        }
        float temp = cx;
        float temp2 = cy;
        cx = radius;
        cy = 0;
        renderer.color(colorBits);
        renderer.vertex(x + cx, y + cy, z);
        if(shapeType != ShapeType.Line) {
            renderer.color(colorBits);
            renderer.vertex(x + temp, y + temp2, z);
            renderer.color(colorBits);
            renderer.vertex(x + cx, y + cy, z);
            renderer.color(colorBits);
            renderer.vertex(x, y, z + height);
        }
    }

    /**
     * Draws a polygon in the x/y plane using {@link ShapeType#Line}. The vertices must contain at least 3 points (6 floats
     * x,y).
     */
    public void polygon(float[] vertices, int offset, int count) {
        if(count < 6) throw new IllegalArgumentException("Polygons must contain at least 3 points.");
        if(count % 2 != 0) throw new IllegalArgumentException("Polygons must have an even number of vertices.");

        check(ShapeType.Line, null, count);
        float colorBits = color.toFloatBits();
        float firstX = vertices[0];
        float firstY = vertices[1];

        for(int i = offset, n = offset + count; i < n; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];

            float x2;
            float y2;

            if(i + 2 >= count) {
                x2 = firstX;
                y2 = firstY;
            }
            else {
                x2 = vertices[i + 2];
                y2 = vertices[i + 3];
            }

            renderer.color(colorBits);
            renderer.vertex(x1, y1, 0);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, 0);
        }
    }

    /**
     * @see #polygon(float[], int, int)
     */
    public void polygon(float[] vertices) {
        polygon(vertices, 0, vertices.length);
    }

    /**
     * Draws a polyline in the x/y plane using {@link ShapeType#Line}. The vertices must contain at least 2 points (4 floats
     * x,y).
     */
    public void polyline(float[] vertices, int offset, int count) {
        if(count < 4) throw new IllegalArgumentException("Polylines must contain at least 2 points.");
        if(count % 2 != 0) throw new IllegalArgumentException("Polylines must have an even number of vertices.");

        check(ShapeType.Line, null, count);
        float colorBits = color.toFloatBits();
        for(int i = offset, n = offset + count - 2; i < n; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];

            float x2;
            float y2;

            x2 = vertices[i + 2];
            y2 = vertices[i + 3];

            renderer.color(colorBits);
            renderer.vertex(x1, y1, 0);
            renderer.color(colorBits);
            renderer.vertex(x2, y2, 0);
        }
    }

    /**
     * @see #polyline(float[], int, int)
     */
    public void polyline(float[] vertices) {
        polyline(vertices, 0, vertices.length);
    }

    /**
     * Changed
     */
    private void check(ShapeType preferred, ShapeType other, int newVertices) {
        int maxVertices = renderer.getMaxVertices();
        int numVertices = renderer.getNumVertices();
        if(maxVertices - numVertices < newVertices) {
            flush();
            // Not enough space.
//            throw new IllegalStateException("Not enough space: maxVertices: " + maxVertices + " numVertices: " + numVertices + " newVertices: " + newVertices);
        }
    }

    /**
     * Finishes the batch of shapes and ensures they get rendered.
     */
    public void end() {
        end(true);
    }

    /**
     * Finishes the batch of shapes and ensures they get rendered.
     */
    public void end(boolean reset) {
        renderer.end();
        if(reset) {
            reset();
        }
    }

    public void flush() {
        flush(true);
    }

    public void flush(boolean autoClear) {
        ShapeType type = shapeType;
        if(type == null) return;
        end(autoClear);
        begin(type);
    }

    public void reset() {
        renderer.reset();
    }

    /**
     * Returns the current shape type.
     */
    public ShapeType getCurrentType() {
        return shapeType;
    }

    public ImmediateModeRenderer getRenderer() {
        return renderer;
    }

    /**
     * @return true if currently between begin and end.
     */
    public boolean isDrawing() {
        return shapeType != null;
    }

    public void dispose() {
        renderer.dispose();
    }
}