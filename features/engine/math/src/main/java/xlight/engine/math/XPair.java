package xlight.engine.math;

public class XPair<A, B> {
    public A a;
    public B b;

    public XPair() {
    }

    public XPair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public void clear() {
        a = null;
        b = null;
    }
}