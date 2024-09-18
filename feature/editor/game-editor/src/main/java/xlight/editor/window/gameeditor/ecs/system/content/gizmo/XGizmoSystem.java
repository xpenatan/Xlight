package xlight.editor.window.gameeditor.ecs.system.content.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.gdx.multiview.EmuApplicationWindow;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystemType;

public class XGizmoSystem extends XGameEditorSystem {
    private SpriteBatch batch;
    private OrthographicCamera batchCamera;
    private EmuApplicationWindow emuWindow;
    private ScreenViewport screenViewport;
    private int gizmoPreviewSize = 140;

    @Override
    public void onSystemAttach(XWorld world) {
        batch = new SpriteBatch();
        batchCamera = new OrthographicCamera(); // TODO XCamera orthogonal not working correctly.
        batchCamera.setToOrtho(true);
        screenViewport = new ScreenViewport(batchCamera);
        emuWindow = new EmuApplicationWindow();
        emuWindow.setApplicationListener(new XGizmoApplication(world));
    }

    @Override
    public void onTick(XWorld world) {
        int windowX = Gdx.graphics.getWidth() - gizmoPreviewSize;
        emuWindow.begin(false, false, windowX, 0, gizmoPreviewSize, gizmoPreviewSize);
        emuWindow.loop();
        emuWindow.end();

        screenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batch.setProjectionMatrix(batchCamera.combined);
        batch.begin();
        Texture texture = emuWindow.getTexture();
        batch.draw(texture, windowX, 0, gizmoPreviewSize, gizmoPreviewSize, emuWindow.u, emuWindow.v, emuWindow.u2, emuWindow.v2);
        batch.end();
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
