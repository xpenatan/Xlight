package xlight.engine.json;

import com.badlogic.gdx.utils.JsonValue;

public interface XJsonValue {

    /** Convert a json object to string */
    String toJson();

    void free();
    void clear();

    void setChild(XJsonValue jsonValue);
    void setParent(XJsonValue jsonValue);
    void setNext(XJsonValue jsonValue);
    void setPrev(XJsonValue jsonValue);

    XJsonValue getChild();
    XJsonValue getParent();
    XJsonValue getNext();
    XJsonValue getPrev();
    String getName();
    void setName(String name);

    void setType(JsonValue.ValueType type);
    JsonValue.ValueType getType();

    int getSize();
    void setSize(int size);

    void set (String value);
    void set (double value);
    void set (float value);
    void set (int value);
    void set (long value);
    void set (boolean value);

    String asString();
    float asFloat();
    double asDouble();
    long asLong();
    int asInt();
    boolean asBoolean();
    byte asByte();
    short asShort();
    char asChar();

    void addChildValue (String name, XJsonValue value);
    void addArrayValue (XJsonValue value);
}