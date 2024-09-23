package xlight.engine.string;

import com.badlogic.gdx.utils.CharArray;
import org.w3c.dom.Text;

public class XTextBuilderDesktop implements XTextBuilder {
    private static final long serialVersionUID = 0x600L; // Version.
    // We do a full resize (and copy) only when the capacity is less than C1.
    // For large collections, multi-dimensional arrays are employed.
    private static final int B0 = 5; // Initial capacity in bits.
    private static final int C0 = 1 << B0; // Initial capacity (32)
    private static final int B1 = 10; // Low array maximum capacity in bits.
    private static final int C1 = 1 << B1; // Low array maximum capacity (1024).
    private static final int M1 = C1 - 1; // Mask.
    // Resizes up to 1024 maximum (32, 64, 128, 256, 512, 1024).
    private char[] _low = new char[C0];
    // For larger capacity use multi-dimensional array.
    private char[][] _high = new char[1][];

    private int _length;

    private int _capacity = C0;

    public XTextBuilderDesktop() {
        _high[0] = _low;
    }

    public XTextBuilderDesktop(String str) {
        this();
        append(str);
    }

    public XTextBuilderDesktop(int capacity) {
        this();
        while(capacity > _capacity) {
            increaseCapacity();
        }
    }

    @Override
    public final int length() {
        return _length;
    }

    @Override
    public final char charAt(int index) {
        if(index >= _length)
            throw new IndexOutOfBoundsException();
        return index < C1 ? _low[index] : _high[index >> B1][index & M1];
    }

    public final void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if((srcBegin < 0) || (srcBegin > srcEnd) || (srcEnd > this._length))
            throw new IndexOutOfBoundsException();
        for(int i = srcBegin, j = dstBegin; i < srcEnd; ) {
            char[] chars0 = _high[i >> B1];
            int i0 = i & M1;
            int length = XMathLib.min(C1 - i0, srcEnd - i);
            System.arraycopy(chars0, i0, dst, j, length);
            i += length;
            j += length;
        }
    }

    public final void setCharAt(int index, char c) {
        if((index < 0) || (index >= _length))
            throw new IndexOutOfBoundsException();
        _high[index >> B1][index & M1] = c;
    }

    public final int getCharIndex(char c) {
        for(int i = 0; i < _length; i++) {
            if(charAt(i) == c)
                return i;
        }
        return -1;
    }

    @Override
    public final void setLength(int newLength) {
        setLength(newLength, '\u0000');
    }

    public final void setLength(int newLength, char fillChar) {
        if(newLength < 0)
            throw new IndexOutOfBoundsException();
        if(newLength <= _length)
            _length = newLength;
        else
            for(int i = _length; i++ < newLength; ) {
                append(fillChar);
            }
    }

    public String substring(int srcBegin, int srcEnd) {
        if((srcBegin < 0) || (srcBegin > srcEnd) || (srcEnd > this._length))
            throw new IndexOutOfBoundsException();
        char dst[] = new char[srcEnd - srcBegin];
        int dstBegin = 0;
        for(int i = srcBegin, j = dstBegin; i < srcEnd; ) {
            char[] chars0 = _high[i >> B1];
            int i0 = i & M1;
            int length = XMathLib.min(C1 - i0, srcEnd - i);
            System.arraycopy(chars0, i0, dst, j, length);
            i += length;
            j += length;
        }
        return new String(dst, srcBegin, srcEnd - srcBegin);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    @Override
    public final XTextBuilder append(char c) {
        if(_length >= _capacity)
            increaseCapacity();
        _high[_length >> B1][_length & M1] = c;
        _length++;
        return this;
    }

    @Override
    public final XTextBuilder append(CharSequence csq) {
        return (csq == null) ? append("null") : append(csq, 0, csq.length());
    }

    @Override
    public final XTextBuilder append(CharSequence csq, int start, int end) {
        if(csq == null)
            return append("null");
        if((start < 0) || (end < 0) || (start > end) || (end > csq.length()))
            throw new IndexOutOfBoundsException();
        for(int i = start; i < end; ) {
            append(csq.charAt(i++));
        }
        return this;
    }

    @Override
    public final XTextBuilder append(String str) {
        return (str == null) ? append("null") : append(str, 0, str.length());
    }

    public final XTextBuilder append(String str, int start, int end) {
        if(str == null)
            return append("null");
        if((start < 0) || (end < 0) || (start > end) || (end > str.length()))
            throw new IndexOutOfBoundsException("start: " + start + ", end: " + end + ", str.length(): " + str.length());
        int newLength = _length + end - start;
        while(_capacity < newLength) {
            increaseCapacity();
        }
        for(int i = start, j = _length; i < end; ) {
            char[] chars = _high[j >> B1];
            int dstBegin = j & M1;
            int inc = XMathLib.min(C1 - dstBegin, end - i);
            str.getChars(i, (i += inc), chars, dstBegin);
            j += inc;
        }
        _length = newLength;
        return this;
    }

    public final XTextBuilder append(char chars[]) {
        append(chars, 0, chars.length);
        return this;
    }

    public final XTextBuilder append(char chars[], int offset, int length) {
        final int end = offset + length;
        if((offset < 0) || (length < 0) || (end > chars.length))
            throw new IndexOutOfBoundsException();
        int newLength = _length + length;
        while(_capacity < newLength) {
            increaseCapacity();
        }
        for(int i = offset, j = _length; i < end; ) {
            char[] dstChars = _high[j >> B1];
            int dstBegin = j & M1;
            int inc = XMathLib.min(C1 - dstBegin, end - i);
            System.arraycopy(chars, i, dstChars, dstBegin, inc);
            i += inc;
            j += inc;
        }
        _length = newLength;
        return this;
    }

    @Override
    public final XTextBuilder append(boolean b) {
        return b ? append("true") : append("false");
    }

    public final XTextBuilder append(int i) {
        if(i <= 0) {
            if(i == 0)
                return append("0");
            if(i == Integer.MIN_VALUE) // Negation would overflow.
                return append("-2147483648");
            append('-');
            i = -i;
        }
        int digits = XMathLib.digitLength(i);
        if(_capacity < _length + digits)
            increaseCapacity();
        _length += digits;
        for(int index = _length - 1; ; index--) {
            int j = i / 10;
            _high[index >> B1][index & M1] = (char)('0' + i - (j * 10));
            if(j == 0)
                return this;
            i = j;
        }
    }

    @Override
    public XTextBuilder append(short s) {
        return append((int)s);
    }

    public final XTextBuilder append(int i, int radix) {
        if(radix == 10)
            return append(i); // Faster.
        if(radix < 2 || radix > 36)
            throw new IllegalArgumentException("radix: " + radix);
        if(i < 0) {
            append('-');
            if(i == Integer.MIN_VALUE) { // Negative would overflow.
                appendPositive(-(i / radix), radix);
                return append(DIGIT_TO_CHAR[-(i % radix)]);
            }
            i = -i;
        }
        appendPositive(i, radix);
        return this;
    }

    private void appendPositive(int l1, int radix) {
        if(l1 >= radix) {
            int l2 = l1 / radix;
            // appendPositive(l2, radix);
            if(l2 >= radix) {
                int l3 = l2 / radix;
                // appendPositive(l3, radix);
                if(l3 >= radix) {
                    int l4 = l3 / radix;
                    appendPositive(l4, radix);
                    append(DIGIT_TO_CHAR[l3 - (l4 * radix)]);
                }
                else
                    append(DIGIT_TO_CHAR[l3]);
                append(DIGIT_TO_CHAR[l2 - (l3 * radix)]);
            }
            else
                append(DIGIT_TO_CHAR[l2]);
            append(DIGIT_TO_CHAR[l1 - (l2 * radix)]);
        }
        else
            append(DIGIT_TO_CHAR[l1]);
    }

    private final static char[] DIGIT_TO_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public final XTextBuilder append(long l) {
        if(l <= 0) {
            if(l == 0)
                return append("0");
            if(l == Long.MIN_VALUE) // Negation would overflow.
                return append("-9223372036854775808");
            append('-');
            l = -l;
        }
        if(l <= Integer.MAX_VALUE)
            return append((int)l);
        append(l / 1000000000);
        int i = (int)(l % 1000000000);
        int digits = XMathLib.digitLength(i);
        append("000000000", 0, 9 - digits);
        return append(i);
    }

    @Override
    public final XTextBuilder append(long l, int radix) {
        if(radix == 10)
            return append(l); // Faster.
        if(radix < 2 || radix > 36)
            throw new IllegalArgumentException("radix: " + radix);
        if(l < 0) {
            append('-');
            if(l == Long.MIN_VALUE) { // Negative would overflow.
                appendPositive(-(l / radix), radix);
                return append(DIGIT_TO_CHAR[(int)-(l % radix)]);
            }
            l = -l;
        }
        appendPositive(l, radix);
        return this;
    }

    private void appendPositive(long l1, int radix) {
        if(l1 >= radix) {
            long l2 = l1 / radix;
            // appendPositive(l2, radix);
            if(l2 >= radix) {
                long l3 = l2 / radix;
                // appendPositive(l3, radix);
                if(l3 >= radix) {
                    long l4 = l3 / radix;
                    appendPositive(l4, radix);
                    append(DIGIT_TO_CHAR[(int)(l3 - (l4 * radix))]);
                }
                else
                    append(DIGIT_TO_CHAR[(int)l3]);
                append(DIGIT_TO_CHAR[(int)(l2 - (l3 * radix))]);
            }
            else
                append(DIGIT_TO_CHAR[(int)l2]);
            append(DIGIT_TO_CHAR[(int)(l1 - (l2 * radix))]);
        }
        else
            append(DIGIT_TO_CHAR[(int)l1]);
    }

    @Override
    public final XTextBuilder append(float f) {
        return append(f, 10, (XMathLib.abs(f) >= 1E7) || (XMathLib.abs(f) < 0.001), false);
    }

    @Override
    public final XTextBuilder append(double d) {
        return append(d, -1, (XMathLib.abs(d) >= 1E7) || (XMathLib.abs(d) < 0.001), false);
    }

    @Override
    public final XTextBuilder append(double d, int digits, boolean scientific, boolean showZero) {
        if(digits > 19)
            throw new IllegalArgumentException("digits: " + digits);
        if(d != d) // NaN
            return append("NaN");
        if(d == Double.POSITIVE_INFINITY)
            return append("Infinity");
        if(d == Double.NEGATIVE_INFINITY)
            return append("-Infinity");
        if(d == 0.0) { // Zero.
            if(digits < 0)
                return append("0.0");
            append('0');
            if(showZero) {
                append('.');
                for(int j = 1; j < digits; j++) {
                    append('0');
                }
            }
            return this;
        }
        if(d < 0) { // Work with positive number.
            d = -d;
            append('-');
        }

        // Find the exponent e such as: value == x.xxx * 10^e
        int e = XMathLib.floorLog10(d);

        long m;
        if(digits < 0) { // Use 16 or 17 digits.
            // Try 17 digits.
            long m17 = XMathLib.toLongPow10(d, (17 - 1) - e);
            // Check if we can use 16 digits.
            long m16 = m17 / 10;
            double dd = XMathLib.toDoublePow10(m16, e - 16 + 1);
            if(dd == d) { // 16 digits is enough.
                digits = 16;
                m = m16;
            }
            else { // We cannot remove the last digit.
                digits = 17;
                m = m17;
            }
        }
        else
            // Use the specified number of digits.
            m = XMathLib.toLongPow10(d, (digits - 1) - e);

        // Formats.
        if(scientific || (e >= digits)) {
            // Scientific notation has to be used ("x.xxxEyy").
            long pow10 = POW10_LONG[digits - 1];
            int k = (int)(m / pow10); // Single digit.
            append((char)('0' + k));
            m = m - pow10 * k;
            appendFraction(m, digits - 1, showZero);
            append('E');
            append(e);
        }
        else { // Dot within the string ("xxxx.xxxxx").
            int exp = digits - e - 1;
            if(exp < POW10_LONG.length) {
                long pow10 = POW10_LONG[exp];
                long l = m / pow10;
                append(l);
                m = m - pow10 * l;
            }
            else
                append('0'); // Result of the division by a power of 10 larger than any long.
            appendFraction(m, exp, showZero);
        }
        return this;
    }

    private void appendFraction(long l, int digits, boolean showZero) {
        append('.');
        if(l == 0)
            if(showZero)
                for(int i = 0; i < digits; i++) {
                    append('0');
                }
            else
                append('0');
        else { // l is different from zero.
            int length = XMathLib.digitLength(l);
            for(int j = length; j < digits; j++) {
                append('0'); // Add leading zeros.
            }
            if(!showZero)
                while(l % 10 == 0) {
                    l /= 10; // Remove trailing zeros.
                }
            append(l);
        }
    }

    private static final long[] POW10_LONG = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};

    public final XTextBuilder insert(int index, java.lang.CharSequence csq) {
        if((index < 0) || (index > _length))
            throw new IndexOutOfBoundsException("index: " + index);
        final int shift = csq.length();
        int newLength = _length + shift;
        while(newLength >= _capacity) {
            increaseCapacity();
        }
        _length = newLength;
        for(int i = _length - shift; --i >= index; ) {
            this.setCharAt(i + shift, this.charAt(i));
        }
        for(int i = csq.length(); --i >= 0; ) {
            this.setCharAt(index + i, csq.charAt(i));
        }
        return this;
    }

    public final XTextBuilder clear() {
        _length = 0;
        return this;
    }

    public final XTextBuilder delete(int start, int end) {
        if((start < 0) || (end < 0) || (start > end) || (end > this.length()))
            throw new IndexOutOfBoundsException();
        for(int i = end, j = start; i < _length; ) {
            this.setCharAt(j++, this.charAt(i++));
        }
        _length -= end - start;
        return this;
    }

    public final XTextBuilder reverse() {
        final int n = _length - 1;
        for(int j = (n - 1) >> 1; j >= 0; ) {
            char c = charAt(j);
            setCharAt(j, charAt(n - j));
            setCharAt(n - j--, c);
        }
        return this;
    }

    @Override
    public final String toString() {
        return (_length < C1) ? new String(_low, 0, _length) : toLargeString();
    }

    private String toLargeString() {
        char[] data = new char[_length];
        this.getChars(0, _length, data, 0);
        return new String(data, 0, _length);
    }

    public final CharArray toCharArray() {
        CharArray cArray = new CharArray();
        char[] data;
        if(_length < C1) {
            data = _low;
        }
        else {
            data = new char[_length];
            this.getChars(0, _length, data, 0);
        }
        //        cArray.setArray(data, 0, _length);
        cArray.addAll(data, 0, _length);
        return cArray;
    }

    @Override
    public final int hashCode() {
        int h = 0;
        for(int i = 0; i < _length; ) {
            h = 31 * h + charAt(i++);
        }
        return h;
    }

    @Override
    public final boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof XTextBuilderDesktop))
            return false;
        XTextBuilderDesktop that = (XTextBuilderDesktop)obj;
        if(this._length != that._length)
            return false;
        for(int i = 0; i < _length; ) {
            if(this.charAt(i) != that.charAt(i++))
                return false;
        }
        return true;
    }

    public final boolean contentEquals(java.lang.CharSequence csq) {
        if(csq.length() != _length)
            return false;
        for(int i = 0; i < _length; ) {
            char c = _high[i >> B1][i & M1];
            if(csq.charAt(i++) != c)
                return false;
        }
        return true;
    }

    private void increaseCapacity() {
        if(_capacity < C1) { // For small capacity, resize.
            _capacity <<= 1;
            char[] tmp = new char[_capacity];
            System.arraycopy(_low, 0, tmp, 0, _length);
            _low = tmp;
            _high[0] = tmp;
        }
        else { // Add a new low block of 1024 elements.
            int j = _capacity >> B1;
            if(j >= _high.length) { // Resizes _high.
                char[][] tmp = new char[_high.length * 2][];
                System.arraycopy(_high, 0, tmp, 0, _high.length);
                _high = tmp;
            }
            _high[j] = new char[C1];
            _capacity += C1;
        }
    }
}