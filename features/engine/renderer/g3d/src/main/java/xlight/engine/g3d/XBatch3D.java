package xlight.engine.g3d;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;

public interface XBatch3D {
    void drawModel(RenderableProvider renderableProvider, XBatch3DOp op);
    default void drawModel(RenderableProvider renderableProvider) {
        drawModel(renderableProvider, XBatch3DOp.get());
    }
    void drawLight(BaseLight<?> ligt);
}