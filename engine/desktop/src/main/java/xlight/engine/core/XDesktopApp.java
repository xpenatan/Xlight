package xlight.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import org.lwjgl.glfw.GLFW;
import xlight.engine.app.XGraphics;
import xlight.engine.ecs.XWorld;

public class XDesktopApp {

    public XDesktopApp(XApplication applicationListener, XDesktopConfiguration config) {
        new Lwjgl3Application(new XApplicationInternal(XEngine.newInstance(), applicationListener){
            @Override
            public void create() {
                XWorld world = engine.getWorld();
                world.registerGlobalData(XGraphics.class, new XGraphicsDesktop((Lwjgl3Graphics)Gdx.graphics));
                super.create();
            }
        }, config);
    }

    private static class XGraphicsDesktop implements XGraphics {
        private Lwjgl3Graphics graphics;
        float[] scaleX = new float[1];
        float[] scaleY = new float[1];

        public XGraphicsDesktop(Lwjgl3Graphics graphics) {
            this.graphics = graphics;
        }

        @Override
        public float getDPIScale() {
            Lwjgl3Graphics.Lwjgl3Monitor monitor = (Lwjgl3Graphics.Lwjgl3Monitor)graphics.getMonitor();
            GLFW.glfwGetMonitorContentScale(monitor.getMonitorHandle(), scaleX, scaleY);
            return scaleX[0];
        }
    }
}