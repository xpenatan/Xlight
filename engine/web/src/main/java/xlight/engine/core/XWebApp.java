package xlight.engine.core;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaGraphics;
import xlight.engine.impl.XEngineImpl;

public class XWebApp {

    public XWebApp(XApplication applicationListener, XWebConfiguration config) {
        TeaApplication teaApplication = new TeaApplication(new XApplicationInternal(new XEngineImpl(), applicationListener), config);
        double nativeScreenDensity = ((TeaGraphics)teaApplication.getGraphics()).getNativeScreenDensity();
        System.out.println("NativeScreenDensity: " + nativeScreenDensity);
    }
}