package xpeengine.engine.core.lang;

public class XBoolean extends XPrimitive {
    private boolean value;

    public XBoolean() {
    }

    public XBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public void onReset() {
        value = false;
        super.onReset();
    }

    @Override
    public TYPE getType() {
        return TYPE.Boolean;
    }

    @Override
    public int intValue() {
        return value ? 1 : 0;
    }

    @Override
    public long longValue() {
        return value ? 1 : 0;
    }

    @Override
    public float floatValue() {
        return value ? 1 : 0;
    }

    @Override
    public double doubleValue() {
        return value ? 1 : 0;
    }

    @Override
    public boolean booleanValue() {
        return value;
    }

    @Override
    public void intValue(int value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void longValue(long value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void floatValue(float value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void doubleValue(double value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void byteValue(byte value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void shortValue(short value) {
        this.value = value == 0 ? false : true;
    }

    @Override
    public void booleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public char charValue() {
        return 0;
    }

    @Override
    public void charValue(char value) {
    }

    @Override
    public boolean equals(int value) {
        try {
            throw new Exception("Cannot compare boolean with int");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(long value) {
        try {
            throw new Exception("Cannot compare boolean with long");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(float value) {
        try {
            throw new Exception("Cannot compare boolean with float");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(double value) {
        try {
            throw new Exception("Cannot compare boolean with double");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(byte value) {
        try {
            throw new Exception("Cannot compare boolean with byte");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(short value) {
        try {
            throw new Exception("Cannot compare boolean with short");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(boolean value) {
        if(this.value == value)
            return true;
        return false;
    }

    @Override
    public boolean equals(char value) {
        try {
            throw new Exception("Cannot compare boolean with char");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
