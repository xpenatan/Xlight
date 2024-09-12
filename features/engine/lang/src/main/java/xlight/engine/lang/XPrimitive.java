package xlight.engine.lang;

import xlight.engine.pool.XPoolable;

public abstract class XPrimitive implements XPoolable {
    public int poolID = -1; // used by pool controller to identity objects created by this pool or other sources

    public enum TYPE {
        Integer, Long, Float, Double, Short, Byte, Boolean, Char
    }

    @Override
    public void onReset() {
        poolID = -1;
    }

    public abstract TYPE getType();
    public abstract int intValue();
    public abstract long longValue();
    public abstract float floatValue();
    public abstract double doubleValue();
    public abstract char charValue();
    public byte byteValue() { return (byte)intValue(); }
    public short shortValue() { return (short)intValue(); }
    public abstract boolean booleanValue();
    public abstract void intValue(int value);
    public abstract void longValue(long value);
    public abstract void floatValue(float value);
    public abstract void doubleValue(double value);
    public abstract void byteValue(byte value);
    public abstract void shortValue(short value);
    public abstract void booleanValue(boolean value);
    public abstract void charValue(char value);
    public abstract boolean equals(int value);
    public abstract boolean equals(long value);
    public abstract boolean equals(float value);
    public abstract boolean equals(double value);
    public abstract boolean equals(byte value);
    public abstract boolean equals(short value);
    public abstract boolean equals(boolean value);
    public abstract boolean equals(char value);

    public static float get(String value, float defaultValue) {
        if(value != null && !value.isEmpty()) {
            try {
                defaultValue = Float.valueOf(value);
            }
            catch(Exception e) {
            }
        }
        return defaultValue;
    }

    public static double get(String value, double defaultValue) {
        if(value != null && !value.isEmpty()) {
            try {
                defaultValue = Double.valueOf(value);
            }
            catch(Exception e) {
            }
        }
        return defaultValue;
    }

    public static int get(String value, int defaultValue) {
        if(value != null && !value.isEmpty()) {
            try {
                defaultValue = Integer.valueOf(value);
            }
            catch(Exception e) {
            }
        }
        return defaultValue;
    }

    public static long get(String value, long defaultValue) {
        if(value != null && !value.isEmpty()) {
            try {
                defaultValue = Long.valueOf(value);
            }
            catch(Exception e) {
            }
        }
        return defaultValue;
    }

    public static boolean get(String value, boolean defaultValue) {
        if(value != null && !value.isEmpty()) {
            try {
                defaultValue = Boolean.valueOf(value);
            }
            catch(Exception e) {
            }
        }
        return defaultValue;
    }
}