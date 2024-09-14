package xlight.engine.math;

public class XTriple<A, B, C> {
    public A a;
    public B b;
    public C c;

    public XTriple() {
    }

    public XTriple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public void clear() {
        a = null;
        b = null;
        c = null;
    }
}