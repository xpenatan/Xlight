package xlight.engine.core;

public abstract class XApplication {

    private XEngine engine;

    public XApplication() {

    }

    public abstract void setup(XEngine engine);
}