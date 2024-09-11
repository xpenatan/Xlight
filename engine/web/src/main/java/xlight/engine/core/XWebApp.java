package xlight.engine.core;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import xlight.engine.impl.XEngineImpl;

public class XWebApp {

    public XWebApp(XApplication applicationListener, XWebConfiguration config) {
        new TeaApplication(new XApplicationInternal(new XEngineImpl(), applicationListener), config);
    }
}