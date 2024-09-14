package xlight.editor;

import xlight.editor.core.XEditor;
import xlight.engine.core.XDesktopApp;
import xlight.engine.core.XDesktopConfiguration;

public class DesktopMain {
    public static void main(String[] args) {
        XDesktopConfiguration config = new XDesktopConfiguration();
        new XDesktopApp(XEditor.newInstance(), config);
    }
}