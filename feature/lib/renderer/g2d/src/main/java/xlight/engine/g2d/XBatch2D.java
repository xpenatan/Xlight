package xlight.engine.g2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public interface XBatch2D extends Batch {
    void drawSprite(Texture texture, float[] spriteVertices, int offset, int count, float regionWidth, float regionHeight);
}