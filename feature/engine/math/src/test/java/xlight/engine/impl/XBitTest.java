package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;
import xlight.engine.math.XBit;

public class XBitTest {

    @Before
    public void setUp() {
    }

    @Test
    public void addBits() {
        long bit = 0;
        bit = XBit.bitAdd(bit, 1);
        bit = XBit.bitAdd(bit, 256);
        bit = XBit.bitAdd(bit, 8);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 8)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 4)).isFalse();
    }

    @Test
    public void removeBits() {
        long bit = 0;
        bit = XBit.bitAdd(bit, 1);
        bit = XBit.bitRemove(bit, 8);
        bit = XBit.bitAdd(bit, 256);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 8)).isFalse();

        bit = XBit.bitRemove(bit, 1);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isFalse();

        bit = XBit.bitRemove(bit, 8);
        Truth.assertThat(XBit.bitContains(bit, 8)).isFalse();
        Truth.assertThat(XBit.bitContains(bit, 1)).isFalse();
    }

    @Test
    public void addBitsObject() {
        Bits bit = new Bits();
        XBit.bitAdd(bit, 1);
        XBit.bitAdd(bit, 256);
        XBit.bitAdd(bit, 8);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 8)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 4)).isFalse();
    }

    @Test
    public void removeBitsObject() {
        Bits bit = new Bits();
        XBit.bitAdd(bit, 1);
        XBit.bitRemove(bit, 8);
        XBit.bitAdd(bit, 256);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 8)).isFalse();

        XBit.bitRemove(bit, 1);
        Truth.assertThat(XBit.bitContains(bit, 256)).isTrue();
        Truth.assertThat(XBit.bitContains(bit, 1)).isFalse();

        XBit.bitRemove(bit, 8);
        Truth.assertThat(XBit.bitContains(bit, 8)).isFalse();
        Truth.assertThat(XBit.bitContains(bit, 1)).isFalse();
    }
}