package xlight.demo.basic;

import xlight.engine.core.XWebApp;
import xlight.engine.core.XWebConfiguration;

public class WebMain {

    public static void main(String[] args) {
        XWebConfiguration config = new XWebConfiguration("canvas");
        config.useDebugGL = false;
        config.width = 0;
        config.height = 0;
        config.usePhysicalPixels = true;
        config.useGL30 = false;
        config.showDownloadLogs = true;
        new XWebApp(new MainApp(), config);
    }
}