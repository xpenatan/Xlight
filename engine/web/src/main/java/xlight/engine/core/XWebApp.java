package xlight.engine.core;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

public class XWebApp {

    public XWebApp(XApplication applicationListener, XWebConfiguration config) {
        new TeaApplication(new XApplicationInternal(applicationListener), config);
    }
}