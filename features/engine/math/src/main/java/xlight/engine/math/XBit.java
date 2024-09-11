package xlight.engine.math;

import com.badlogic.gdx.utils.Bits;

/**
 * Helper class. BIT operations needs both number in binary and must have only 1
 * bit in a binary form: ex 0x0010000, 0x0000001, 0x0000100, etc
 **/
public class XBit {

    public static Bits BITS_1 = new Bits();
    public static Bits BITS_2 = new Bits();

    private XBit() {
    }

    /**
     * Check if curBit contains otherBit
     */
    public static boolean bitContains(long curBit, long otherBit) {
        return (curBit & otherBit) == otherBit;
    }

    /**
     * Adds other Bit to curBit and returns it.
     */
    public static long bitAdd(long curBit, long otherBit) {
        return curBit | otherBit;
    }

    /**
     * Remove otherBit from curBit and returns it.
     */
    public static long bitRemove(long curBit, long otherBit) {
        return curBit & ~otherBit;
    }

    /**
     *
     */
    public static boolean bitContains(Bits curBit, int otherBit) {
        return curBit.get(otherBit);
    }

    /**
     *
     */
    public static void bitAdd(Bits curBit, int otherBit) {
        curBit.set(otherBit);
    }

    /**
     *
     */
    public static void bitRemove(Bits curBit, int otherBit) {
        curBit.clear(otherBit);
    }
}
