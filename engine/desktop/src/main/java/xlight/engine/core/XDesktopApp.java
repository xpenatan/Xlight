package xlight.engine.core;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import xlight.engine.impl.XEngineImpl;

public class XDesktopApp {

    public XDesktopApp(XApplication applicationListener, XDesktopConfiguration config) {
        new Lwjgl3Application(new XApplicationInternal(new XEngineImpl(), applicationListener), config);
    }
}