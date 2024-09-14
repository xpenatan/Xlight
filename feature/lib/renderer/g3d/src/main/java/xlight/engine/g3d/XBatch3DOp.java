package xlight.engine.g3d;

public class XBatch3DOp {

    private static XBatch3DOp op = new XBatch3DOp();

    public static XBatch3DOp get() {
        op.reset();
        return op;
    }

    public boolean environment;

    private XBatch3DOp() {
    }

    public void reset() {
        environment = true;
    }
}