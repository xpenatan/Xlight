package xlight.engine.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

public class XFrameBufferExt {
    protected int frameBufferWidth = 1024;
    protected int frameBufferHeight = 1024;
    private FrameBuffer frameBuffer;
    private RenderContext renderContext;

    private Pixmap.Format format = Pixmap.Format.RGBA8888;

    private boolean isDirty;

    private boolean begin;

    private TextureDescriptor<Texture> textureDesc;

    private boolean hasDepth = true;

    public XFrameBufferExt(int frameBufferWidth, int frameBufferHeight, Pixmap.Format format, boolean hasDepth) {
        this.frameBufferWidth = frameBufferWidth;
        this.frameBufferHeight = frameBufferHeight;
        this.format = format;
        this.hasDepth = hasDepth;
        init();
    }

    public XFrameBufferExt(Pixmap.Format format, boolean hasDepth) {
        this.format = format;
        this.hasDepth = hasDepth;
        init();
    }

    public XFrameBufferExt() {
        init();
    }

    protected void init() {
        createFrameBuffer();
        textureDesc = new TextureDescriptor<Texture>();
    }

    private void createFrameBuffer() {
        dispose();
        frameBuffer = new FrameBuffer(format, frameBufferWidth, frameBufferHeight, hasDepth);
        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
    }

    public void setFrameBufferSize(int width, int height) {
        if(!begin) {
            this.frameBufferWidth = width;
            this.frameBufferHeight = height;
            isDirty = true;
        }
    }

    public void begin() {
        if(!begin) {
            begin = true;
            if(isDirty) {
                isDirty = false;
                createFrameBuffer();
            }

            renderContext.begin();
            frameBuffer.begin();
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
    }

    public void render(Camera camera, ModelBatch modelBatch, final Array<RenderableProvider> modelInstances) {
        modelBatch.begin(camera);
        modelBatch.render(modelInstances);
        modelBatch.end();
    }

    public void render(Camera camera, ModelBatch modelBatch, final RenderableProvider modelInstance) {
        modelBatch.begin(camera);
        modelBatch.render(modelInstance);
        modelBatch.end();
    }

    public void end() {
        if(begin) {
            begin = false;
            frameBuffer.end();
            renderContext.end();
        }
    }

    public TextureDescriptor<Texture> getColorBufferTexture() {
        textureDesc.texture = frameBuffer.getColorBufferTexture();
        return textureDesc;
    }

    public void dispose() {
        if(frameBuffer != null) {
            frameBuffer.dispose();
            frameBuffer = null;
        }
    }

    public int getFrameBufferWidth() {
        return frameBufferWidth;
    }

    public int getFrameBufferHeight() {
        return frameBufferHeight;
    }
}
