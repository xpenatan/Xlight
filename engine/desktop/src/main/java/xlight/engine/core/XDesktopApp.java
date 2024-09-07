package xlight.engine.core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;

public class XDesktopApp {

    public XDesktopApp(XApplication applicationListener, XDesktopConfiguration config) {
        new Lwjgl3Application(new XApplicationInternal(applicationListener), config);
    }
}