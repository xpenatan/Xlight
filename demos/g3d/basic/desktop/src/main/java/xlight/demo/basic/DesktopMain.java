package xlight.demo.basic;

import xlight.engine.core.XDesktopApp;
import xlight.engine.core.XDesktopConfiguration;

public class DesktopMain {
    public static void main(String[] args) {
        XDesktopConfiguration config = new XDesktopConfiguration();
        new XDesktopApp(new MainApp(), config);
    }
}