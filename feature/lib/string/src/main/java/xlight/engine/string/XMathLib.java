package xlight.engine.string;

final class XMathLib {

    private XMathLib() {
    }

    private static final byte[] BIT_LENGTH = {0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

    public static int bitLength(long l) {
        int i = (int)(l >> 32);
        if(i > 0)
            return (i < 1 << 16) ? (i < 1 << 8) ? BIT_LENGTH[i] + 32 : BIT_LENGTH[i >>> 8] + 40 : (i < 1 << 24) ? BIT_LENGTH[i >>> 16] + 48 : BIT_LENGTH[i >>> 24] + 56;
        if(i < 0)
            return bitLength(-++l);
        i = (int)l;
        return (i < 0) ? 32 : (i < 1 << 16) ? (i < 1 << 8) ? BIT_LENGTH[i] : BIT_LENGTH[i >>> 8] + 8 : (i < 1 << 24) ? BIT_LENGTH[i >>> 16] + 16 : BIT_LENGTH[i >>> 24] + 24;
    }

    public static int digitLength(int i) {
        if(i >= 0)
            return (i >= 100000) ? (i >= 10000000) ? (i >= 1000000000) ? 10 : (i >= 100000000) ? 9 : 8 : (i >= 1000000) ? 7 : 6 : (i >= 100) ? (i >= 10000) ? 5 : (i >= 1000) ? 4 : 3 : (i >= 10) ? 2 : 1;
        if(i == Integer.MIN_VALUE)
            return 10; // "2147483648".length()
        return digitLength(-i); // No overflow possible.
    }

    public static int digitLength(long l) {
        if(l >= 0)
            return (l <= Integer.MAX_VALUE) ? digitLength((int)l) : // At least 10 digits or more.
                    (l >= 100000000000000L) ? (l >= 10000000000000000L) ? (l >= 1000000000000000000L) ? 19 : (l >= 100000000000000000L) ? 18 : 17 : (l >= 1000000000000000L) ? 16 : 15 : (l >= 100000000000L) ? (l >= 10000000000000L) ? 14 : (l >= 1000000000000L) ? 13 : 12 : (l >= 10000000000L) ? 11 : 10;
        if(l == Long.MIN_VALUE)
            return 19; // "9223372036854775808".length()
        return digitLength(-l);
    }

    public static double toDoublePow2(long m, int n) {
        if(m == 0)
            return 0.0;
        if(m == Long.MIN_VALUE)
            return toDoublePow2(Long.MIN_VALUE >> 1, n + 1);
        if(m < 0)
            return -toDoublePow2(-m, n);
        int bitLength = XMathLib.bitLength(m);
        int shift = bitLength - 53;
        long exp = 1023L + 52 + n + shift; // Use long to avoid overflow.
        if(exp >= 0x7FF)
            return Double.POSITIVE_INFINITY;
        if(exp <= 0) { // Degenerated number (subnormal, assume 0 for bit 52)
            if(exp <= -54)
                return 0.0;
            return toDoublePow2(m, n + 54) / 18014398509481984L; // 2^54 Exact.
        }
        // Normal number.
        long bits = (shift > 0) ? (m >> shift) + ((m >> (shift - 1)) & 1) : // Rounding.
                m << -shift;
        if(((bits >> 52) != 1) && (++exp >= 0x7FF))
            return Double.POSITIVE_INFINITY;
        bits &= 0x000fffffffffffffL; // Clears MSB (bit 52)
        bits |= exp << 52;
        return Double.longBitsToDouble(bits);
    }

    public static double toDoublePow10(long m, int n) {
        if(m == 0)
            return 0.0;
        if(m == Long.MIN_VALUE)
            return toDoublePow10(Long.MIN_VALUE / 10, n + 1);
        if(m < 0)
            return -toDoublePow10(-m, n);
        if(n >= 0) { // Positive power.
            if(n > 308)
                return Double.POSITIVE_INFINITY;
            // Works with 4 x 32 bits registers (x3:x2:x1:x0)
            long x0 = 0; // 32 bits.
            long x1 = 0; // 32 bits.
            long x2 = m & MASK_32; // 32 bits.
            long x3 = m >>> 32; // 32 bits.
            int pow2 = 0;
            while(n != 0) {
                int i = (n >= POW5_INT.length) ? POW5_INT.length - 1 : n;
                int coef = POW5_INT[i]; // 31 bits max.

                if(((int)x0) != 0)
                    x0 *= coef; // 63 bits max.
                if(((int)x1) != 0)
                    x1 *= coef; // 63 bits max.
                x2 *= coef; // 63 bits max.
                x3 *= coef; // 63 bits max.

                x1 += x0 >>> 32;
                x0 &= MASK_32;

                x2 += x1 >>> 32;
                x1 &= MASK_32;

                x3 += x2 >>> 32;
                x2 &= MASK_32;

                // Adjusts powers.
                pow2 += i;
                n -= i;

                // Normalizes (x3 should be 32 bits max).
                long carry = x3 >>> 32;
                if(carry != 0) { // Shift.
                    x0 = x1;
                    x1 = x2;
                    x2 = x3 & MASK_32;
                    x3 = carry;
                    pow2 += 32;
                }
            }

            // Merges registers to a 63 bits mantissa.
            int shift = 31 - XMathLib.bitLength(x3); // -1..30
            pow2 -= shift;
            long mantissa = (shift < 0) ? (x3 << 31) | (x2 >>> 1) : // x3 is 32 bits.
                    (((x3 << 32) | x2) << shift) | (x1 >>> (32 - shift));
            return toDoublePow2(mantissa, pow2);
        }
        else { // n < 0
            if(n < -324 - 20)
                return 0.0;

            // Works with x1:x0 126 bits register.
            long x1 = m; // 63 bits.
            long x0 = 0; // 63 bits.
            int pow2 = 0;
            while(true) {

                // Normalizes x1:x0
                int shift = 63 - XMathLib.bitLength(x1);
                x1 <<= shift;
                x1 |= x0 >>> (63 - shift);
                x0 = (x0 << shift) & MASK_63;
                pow2 -= shift;

                // Checks if division has to be performed.
                if(n == 0)
                    break; // Done.

                // Retrieves power of 5 divisor.
                int i = (-n >= POW5_INT.length) ? POW5_INT.length - 1 : -n;
                int divisor = POW5_INT[i];

                // Performs the division (126 bits by 31 bits).
                long wh = (x1 >>> 32);
                long qh = wh / divisor;
                long r = wh - qh * divisor;
                long wl = (r << 32) | (x1 & MASK_32);
                long ql = wl / divisor;
                r = wl - ql * divisor;
                x1 = (qh << 32) | ql;

                wh = (r << 31) | (x0 >>> 32);
                qh = wh / divisor;
                r = wh - qh * divisor;
                wl = (r << 32) | (x0 & MASK_32);
                ql = wl / divisor;
                x0 = (qh << 32) | ql;

                // Adjusts powers.
                n += i;
                pow2 -= i;
            }
            return toDoublePow2(x1, pow2);
        }
    }

    private static final long MASK_63 = 0x7FFFFFFFFFFFFFFFL;

    private static final long MASK_32 = 0xFFFFFFFFL;

    private static final int[] POW5_INT = {1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125};

    public static long toLongPow10(double d, int n) {
        long bits = Double.doubleToLongBits(d);
        boolean isNegative = (bits >> 63) != 0;
        int exp = ((int)(bits >> 52)) & 0x7FF;
        long m = bits & 0x000fffffffffffffL;
        if(exp == 0x7FF)
            throw new ArithmeticException("Cannot convert to long (Infinity or NaN)");
        if(exp == 0) {
            if(m == 0)
                return 0L;
            return toLongPow10(d * 1E16, n - 16);
        }
        m |= 0x0010000000000000L; // Sets MSB (bit 52)
        int pow2 = exp - 1023 - 52;
        // Retrieves 63 bits m with n == 0.
        if(n >= 0) {
            // Works with 4 x 32 bits registers (x3:x2:x1:x0)
            long x0 = 0; // 32 bits.
            long x1 = 0; // 32 bits.
            long x2 = m & MASK_32; // 32 bits.
            long x3 = m >>> 32; // 32 bits.
            while(n != 0) {
                int i = (n >= POW5_INT.length) ? POW5_INT.length - 1 : n;
                int coef = POW5_INT[i]; // 31 bits max.

                if(((int)x0) != 0)
                    x0 *= coef; // 63 bits max.
                if(((int)x1) != 0)
                    x1 *= coef; // 63 bits max.
                x2 *= coef; // 63 bits max.
                x3 *= coef; // 63 bits max.

                x1 += x0 >>> 32;
                x0 &= MASK_32;

                x2 += x1 >>> 32;
                x1 &= MASK_32;

                x3 += x2 >>> 32;
                x2 &= MASK_32;

                // Adjusts powers.
                pow2 += i;
                n -= i;

                // Normalizes (x3 should be 32 bits max).
                long carry = x3 >>> 32;
                if(carry != 0) { // Shift.
                    x0 = x1;
                    x1 = x2;
                    x2 = x3 & MASK_32;
                    x3 = carry;
                    pow2 += 32;
                }
            }

            // Merges registers to a 63 bits mantissa.
            int shift = 31 - XMathLib.bitLength(x3); // -1..30
            pow2 -= shift;
            m = (shift < 0) ? (x3 << 31) | (x2 >>> 1) : // x3 is 32 bits.
                    (((x3 << 32) | x2) << shift) | (x1 >>> (32 - shift));
        }
        else { // n < 0

            // Works with x1:x0 126 bits register.
            long x1 = m; // 63 bits.
            long x0 = 0; // 63 bits.
            while(true) {

                // Normalizes x1:x0
                int shift = 63 - XMathLib.bitLength(x1);
                x1 <<= shift;
                x1 |= x0 >>> (63 - shift);
                x0 = (x0 << shift) & MASK_63;
                pow2 -= shift;

                // Checks if division has to be performed.
                if(n == 0)
                    break; // Done.

                // Retrieves power of 5 divisor.
                int i = (-n >= POW5_INT.length) ? POW5_INT.length - 1 : -n;
                int divisor = POW5_INT[i];

                // Performs the division (126 bits by 31 bits).
                long wh = (x1 >>> 32);
                long qh = wh / divisor;
                long r = wh - qh * divisor;
                long wl = (r << 32) | (x1 & MASK_32);
                long ql = wl / divisor;
                r = wl - ql * divisor;
                x1 = (qh << 32) | ql;

                wh = (r << 31) | (x0 >>> 32);
                qh = wh / divisor;
                r = wh - qh * divisor;
                wl = (r << 32) | (x0 & MASK_32);
                ql = wl / divisor;
                x0 = (qh << 32) | ql;

                // Adjusts powers.
                n += i;
                pow2 -= i;
            }
            m = x1;
        }
        //        if (pow2 > 0)
        //            throw new ArithmeticException("Overflow");
        if(pow2 < -63)
            return 0;
        m = (m >> -pow2) + ((m >> -(pow2 + 1)) & 1); // Rounding.
        return isNegative ? -m : m;
    }

    public static int floorLog2(double d) {
        if(d <= 0)
            throw new ArithmeticException("Negative number or zero");
        long bits = Double.doubleToLongBits(d);
        int exp = ((int)(bits >> 52)) & 0x7FF;
        if(exp == 0x7FF)
            throw new ArithmeticException("Infinity or NaN");
        if(exp == 0)
            return floorLog2(d * 18014398509481984L) - 54; // 2^54 Exact.
        return exp - 1023;
    }

    public static int floorLog10(double d) {
        int guess = (int)(LOG2_DIV_LOG10 * XMathLib.floorLog2(d));
        double pow10 = XMathLib.toDoublePow10(1, guess);
        if((pow10 <= d) && (pow10 * 10 > d))
            return guess;
        if(pow10 > d)
            return guess - 1;
        return guess + 1;
    }

    private static final double LOG2_DIV_LOG10 = 0.3010299956639811952137388947;

    public static float abs(float f) {
        return (f < 0) ? -f : f;
    }

    public static double abs(double d) {
        return (d < 0) ? -d : d;
    }

    public static int min(int x, int y) {
        return (x < y) ? x : y;
    }
}