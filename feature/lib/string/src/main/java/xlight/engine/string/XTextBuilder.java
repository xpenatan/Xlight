package xlight.engine.string;

import java.io.Serializable;

public interface XTextBuilder extends Appendable, CharSequence, Serializable {
    XTextBuilder append(double d, int digits, boolean scientific, boolean showZero);

    XTextBuilder append(long l, int radix);

    XTextBuilder append(long l);

    XTextBuilder append(double d);

    XTextBuilder append(float f);

    XTextBuilder append(int i);

    XTextBuilder append(short s);

    XTextBuilder append(boolean b);

    XTextBuilder append(String s);

    @Override
    XTextBuilder append(char c);

    void setLength(int newLength);
}