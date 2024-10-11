package xlight.editor;

import com.badlogic.gdx.graphics.glutils.HdpiMode;
import xlight.editor.core.XEditor;
import xlight.engine.core.XDesktopApp;
import xlight.engine.core.XDesktopConfiguration;

public class DesktopMain {
    public static void main(String[] args) {
        XDesktopConfiguration config = new XDesktopConfiguration();
        config.setWindowedMode(1800, 1300);
        config.setTitle("Xlight Editor");
        config.setHdpiMode(HdpiMode.Pixels);
        new XDesktopApp(XEditor.newInstance(), config);
    }
}