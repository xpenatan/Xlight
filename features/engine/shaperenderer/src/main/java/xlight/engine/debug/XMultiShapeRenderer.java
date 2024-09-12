package xlight.engine.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import xlight.engine.math.XRotSeq;

public class XMultiShapeRenderer {
    private final XShapeRenderer shapeRendererLine;
    private final XShapeRenderer shapeRendererFilled;

    public XMultiShapeRenderer() {
        shapeRendererLine = new XShapeRenderer();
        shapeRendererFilled = new XShapeRenderer();

        shapeRendererLine.set(ShapeRenderer.ShapeType.Line);
        shapeRendererFilled.set(ShapeRenderer.ShapeType.Filled);
    }

    public XShapeRenderer getLineRenderer() {
        return shapeRendererLine;
    }

    public XShapeRenderer getFilledRenderer() {
        return shapeRendererFilled;
    }

    public boolean containsDrawVertices() {
        int numVertices1 = shapeRendererLine.getRenderer().getNumVertices();
        int numVertices2 = shapeRendererFilled.getRenderer().getNumVertices();
        return numVertices1 > 0 || numVertices2 > 0;
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        shapeRendererLine.setProjectionMatrix(matrix);
        shapeRendererFilled.setProjectionMatrix(matrix);
    }

    public void render() {
        render(true);
    }

    public void render(boolean autoClear) {
        shapeRendererLine.begin(ShapeRenderer.ShapeType.Line);
        shapeRendererFilled.begin(ShapeRenderer.ShapeType.Filled);

        boolean isBlend = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        shapeRendererLine.flush(autoClear);

        if(!isBlend) {
            // enable blend to show alpha when rendering filled shape
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        shapeRendererFilled.flush(autoClear);
        if(!isBlend) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void line(ShapeRenderer.ShapeType shapeType, Vector3 v0, Vector3 v1) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.line(v0, v1);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.line(v0, v1);
        }
    }

    public void box(ShapeRenderer.ShapeType shapeType, float x, float y, float z, float width, float height, float depth) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.box(x, y, z, width, height, depth);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.box(x, y, z, width, height, depth);
        }
    }

    public void boundingBox(ShapeRenderer.ShapeType shapeType, Vector3 min, Vector3 max, XRotSeq rotSeq, float rX, float rY, float rZ) {
        boundingBox(shapeType, min.x, min.y, min.z, max.x, max.y, max.z, rotSeq, rX, rY, rZ);
    }

    public void boundingBox(ShapeRenderer.ShapeType shapeType, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, XRotSeq rotSeq, float rX, float rY, float rZ) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.boundingBox(minX, minY, minZ, maxX, maxY, maxZ, rotSeq, rX, rY, rZ);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.boundingBox(minX, minY, minZ, maxX, maxY, maxZ, rotSeq, rX, rY, rZ);
        }
    }

    public void circle(ShapeRenderer.ShapeType shapeType, float x, float y, float z, float radius) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.circle(x, y, 0, radius);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.circle(x, y, 0, radius);
        }
    }

    public void circle(ShapeRenderer.ShapeType shapeType, float x, float y, float z, float radius, int segments) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.circle(x, y, z, radius, segments);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.circle(x, y, z, radius, segments);
        }
    }

    public void setColor(ShapeRenderer.ShapeType shapeType, Color color) {
        setColor(shapeType, color.r, color.g, color.b, color.a);
    }

    public void setColor(ShapeRenderer.ShapeType shapeType, float r, float g, float b, float a) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.setColor(r, g, b, a);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.setColor(r, g, b, a);
        }
    }

    /**
     * Set XColor.toRGBA8888 Color
     */
    public void setColor(ShapeRenderer.ShapeType shapeType, int rgbaColor) {
        if(shapeType == ShapeRenderer.ShapeType.Line) {
            shapeRendererLine.setColor(rgbaColor);
        }
        else if(shapeType == ShapeRenderer.ShapeType.Filled) {
            shapeRendererFilled.setColor(rgbaColor);
        }
    }

    public void reset() {
        shapeRendererLine.reset();
        shapeRendererFilled.reset();
    }
}
