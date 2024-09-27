package xlight.engine.core;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaGraphics;
import xlight.engine.app.XGraphics;
import xlight.engine.ecs.XWorld;
import xlight.engine.impl.XEngineImpl;

public class XWebApp {

    public XWebApp(XApplication applicationListener, XWebConfiguration config) {
        new TeaApplication(new XApplicationInternal(new XEngineImpl(), applicationListener) {
            @Override
            public void create() {
                XWorld world = engine.getWorld();
                world.registerGlobalData(XGraphics.class, new XGraphicsWeb(((TeaGraphics)Gdx.graphics)));
                super.create();
            }
        }, config);
    }

    private static class XGraphicsWeb implements XGraphics {
        private TeaGraphics graphics;

        public XGraphicsWeb(TeaGraphics graphics) {
            this.graphics = graphics;
        }

        @Override
        public float getDPIScale() {
            return (float)graphics.getNativeScreenDensity();
        }
    }
}