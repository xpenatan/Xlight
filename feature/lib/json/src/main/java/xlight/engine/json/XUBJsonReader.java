package xlight.engine.json;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.UBJsonReader;
import java.io.DataInputStream;
import java.io.IOException;
import xlight.engine.pool.XPoolController;

public class XUBJsonReader extends UBJsonReader {

    private final XPoolController poolController;

    public XUBJsonReader(XPoolController poolController) {
        this.poolController = poolController;
    }

    private XJsonValueImpl newJsonValue (JsonValue.ValueType type) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.setType(type);
        return jsonValue;
    }

    /** @param value May be null. */
    private XJsonValueImpl newJsonValue (@Null String value) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value);
        return jsonValue;
    }

    private XJsonValueImpl newJsonValue (double value) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value, null);
        return jsonValue;
    }

    private XJsonValueImpl newJsonValue (long value) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value, null);
        return jsonValue;
    }

    private XJsonValueImpl newJsonValue(double value, String stringValue) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value, null);
        return jsonValue;
    }

    private XJsonValueImpl newJsonValue (long value, String stringValue) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value, null);
        return jsonValue;
    }

    private XJsonValueImpl newJsonValue (boolean value) {
        XJsonValueImpl jsonValue = poolController.obtainObject(XJsonValue.class);
        jsonValue.set(value);
        return jsonValue;
    }

    protected JsonValue parse (final DataInputStream din, final byte type) throws IOException {
        if (type == '[')
            return parseArray(din);
        else if (type == '{')
            return parseObject(din);
        else if (type == 'Z')
            return newJsonValue(JsonValue.ValueType.nullValue);
        else if (type == 'T')
            return newJsonValue(true);
        else if (type == 'F')
            return newJsonValue(false);
        else if (type == 'B')
            return newJsonValue((long)readUChar(din));
        else if (type == 'U')
            return newJsonValue((long)readUChar(din));
        else if (type == 'i')
            return newJsonValue(oldFormat ? (long)din.readShort() : (long)din.readByte());
        else if (type == 'I')
            return newJsonValue(oldFormat ? (long)din.readInt() : (long)din.readShort());
        else if (type == 'l')
            return newJsonValue((long)din.readInt());
        else if (type == 'L')
            return newJsonValue(din.readLong());
        else if (type == 'd')
            return newJsonValue(din.readFloat());
        else if (type == 'D')
            return newJsonValue(din.readDouble());
        else if (type == 's' || type == 'S')
            return newJsonValue(parseString(din, type));
        else if (type == 'a' || type == 'A')
            return parseData(din, type);
        else if (type == 'C')
            return newJsonValue(din.readChar());
        else
            throw new GdxRuntimeException("Unrecognized data type");
    }

    protected JsonValue parseArray (final DataInputStream din) throws IOException {
        JsonValue result = newJsonValue(JsonValue.ValueType.array);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == '$') {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == '#') {
            size = parseSize(din, false, -1);
            if (size < 0) throw new GdxRuntimeException("Unrecognized data type");
            if (size == 0) return result;
            type = valueType == 0 ? din.readByte() : valueType;
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != ']') {
            final JsonValue val = parse(din, type);
            val.parent = result;
            if (prev != null) {
                val.prev = prev;
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
            if (size > 0 && ++c >= size) break;
            type = valueType == 0 ? din.readByte() : valueType;
        }
        return result;
    }

    protected JsonValue parseObject (final DataInputStream din) throws IOException {
        JsonValue result = newJsonValue(JsonValue.ValueType.object);
        byte type = din.readByte();
        byte valueType = 0;
        if (type == '$') {
            valueType = din.readByte();
            type = din.readByte();
        }
        long size = -1;
        if (type == '#') {
            size = parseSize(din, false, -1);
            if (size < 0) throw new GdxRuntimeException("Unrecognized data type");
            if (size == 0) return result;
            type = din.readByte();
        }
        JsonValue prev = null;
        long c = 0;
        while (din.available() > 0 && type != '}') {
            final String key = parseString(din, true, type);
            final JsonValue child = parse(din, valueType == 0 ? din.readByte() : valueType);
            child.setName(key);
            child.parent = result;
            if (prev != null) {
                child.prev = prev;
                prev.next = child;
                result.size++;
            } else {
                result.child = child;
                result.size = 1;
            }
            prev = child;
            if (size > 0 && ++c >= size) break;
            type = din.readByte();
        }
        return result;
    }

    protected JsonValue parseData (final DataInputStream din, final byte blockType) throws IOException {
        // FIXME: a/A is currently not following the specs because it lacks strong typed, fixed sized containers,
        // see: https://github.com/thebuzzmedia/universal-binary-json/issues/27
        final byte dataType = din.readByte();
        final long size = blockType == 'A' ? readUInt(din) : (long)readUChar(din);
        final JsonValue result = new JsonValue(JsonValue.ValueType.array);
        JsonValue prev = null;
        for (long i = 0; i < size; i++) {
            final JsonValue val = parse(din, dataType);
            val.parent = result;
            if (prev != null) {
                prev.next = val;
                result.size++;
            } else {
                result.child = val;
                result.size = 1;
            }
            prev = val;
        }
        return result;
    }
}
