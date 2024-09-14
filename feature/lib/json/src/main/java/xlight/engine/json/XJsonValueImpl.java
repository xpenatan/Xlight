package xlight.engine.json;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import xlight.engine.json.pool.XJsonValuePool;
import xlight.engine.pool.XPoolable;
import java.util.Objects;

public class XJsonValueImpl extends JsonValue implements XJsonValue, XPoolable {
    private static Array<JsonValue> tempArray = new Array<>();

    private final XJsonValuePool pool;

    PrettyPrintSettings settings = new PrettyPrintSettings();

    public XJsonValueImpl(XJsonValuePool pool) {
        super(ValueType.object);
        this.pool = pool;
        settings.outputType = JsonWriter.OutputType.json;
    }

    @Override
    public void onReset() {
        set(0, null);
        setType(ValueType.object);
        child = null;
        parent = null;
        next = null;
        prev = null;
        size = 0;
        name = null;
    }

    @Override
    public String toJson() {
        return prettyPrint(settings);
    }

    @Override
    public void free() {
        // TODO create a solution to free only if its not inside pool array
        releaseAll(true);
    }

    @Override
    public void clear() {
        releaseAll(false);
        onReset();
    }

    @Override
    public void setChild(XJsonValue jsonValue) {
        this.child = (XJsonValueImpl) jsonValue;
    }

    @Override
    public void setParent(XJsonValue jsonValue) {
        this.parent = (XJsonValueImpl) jsonValue;
    }

    @Override
    public void setNext(XJsonValue jsonValue) {
        this.next = (XJsonValueImpl) jsonValue;
    }

    @Override
    public void setPrev(XJsonValue jsonValue) {
        this.prev = (XJsonValueImpl) jsonValue;
    }

    @Override
    public XJsonValue getChild() {
        return (XJsonValueImpl) child;
    }

    @Override
    public XJsonValue getParent() {
        return (XJsonValueImpl) parent;
    }

    @Override
    public XJsonValue getNext() {
        return (XJsonValueImpl) next;
    }

    @Override
    public XJsonValue getPrev() {
        return (XJsonValueImpl) prev;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(ValueType type) {
        super.setType(type);
    }

    @Override
    public ValueType getType() {
        return type();
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void set(double value) {
        set(value, null);
    }

    @Override
    public void set(float value) {
        set(value, null);
    }

    @Override
    public void set(int value) {
        set(value, null);
    }

    @Override
    public void set(long value) {
        set(value, null);
    }

    @Override
    public void addChildValue(String name, XJsonValue value) {
        if(value != null) {
            super.addChild(name, (XJsonValueImpl)value);
        }
    }

    @Override
    public void addArrayValue(XJsonValue value) {
        if(value != null) {
            super.addChild((XJsonValueImpl)value);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof XJsonValueImpl) {
            return isEqual(this, (XJsonValueImpl) obj);
        }
        return false;
    }

    private static boolean isEqual(XJsonValueImpl jsonValue, XJsonValueImpl other) {
        if(jsonValue == null || other == null) {
            return false;
        }

        boolean isEqual = false;
        ValueType type = jsonValue.type();
        boolean sizeEqual = jsonValue.size == other.size;
        boolean nameEqual = Objects.equals(jsonValue.name, other.name);
        boolean typeEqual = type == other.type();
        boolean privateValuesEqual = false;

        if(sizeEqual && nameEqual && typeEqual) {
            // compare private values

            if(type == ValueType.object) {
                boolean failsEqual = false;
                for(int i = 0; i < jsonValue.size; i++) {
                    JsonValue childJsonValue = jsonValue.get(i);
                    JsonValue otherChildJsonValue = other.get(childJsonValue.name);
                    if(!Objects.equals(childJsonValue, otherChildJsonValue)) {
                        failsEqual = true;
                        break;
                    }
                }
                privateValuesEqual = !failsEqual;
            }
            else if(type == ValueType.array) {
                boolean failsEqual = false;
                for(int i = 0; i < jsonValue.size; i++) {
                    JsonValue childJsonValue = jsonValue.get(i);
                    JsonValue otherChildJsonValue = other.get(i);
                    if(!Objects.equals(childJsonValue, otherChildJsonValue)) {
                        failsEqual = true;
                        break;
                    }
                }
                privateValuesEqual = !failsEqual;
            }
            else if(type == ValueType.stringValue) {
                privateValuesEqual = Objects.equals(jsonValue.asString(), other.asString());
            }
            else if(type == ValueType.doubleValue) {
                privateValuesEqual = jsonValue.asDouble() == other.asDouble();
            }
            else if(type == ValueType.longValue) {
                privateValuesEqual = jsonValue.asLong() == other.asLong();
            }
            else if(type == ValueType.booleanValue) {
                privateValuesEqual = jsonValue.asBoolean() == other.asBoolean();
            }
            else if(type == ValueType.nullValue) {
                privateValuesEqual = true;
            }

        }
        isEqual = sizeEqual && nameEqual && typeEqual && privateValuesEqual;
        return isEqual;
    }

    private void releaseAll(boolean freeThis) {
        //TODO check if this json is also included in array
        tempArray.clear();
        removeChild(tempArray, this);
        for(int i = 0; i < tempArray.size; i++) {
            JsonValue jsonValue = tempArray.get(i);
            if(jsonValue == this && !freeThis) {
                continue;
            }
            if(jsonValue instanceof XJsonValueImpl) {
                XJsonValueImpl value = (XJsonValueImpl) jsonValue;
                pool.free(value);
            }
            else {
                throw new RuntimeException("Not XpeJsonValue");
            }
        }
        tempArray.clear();
    }

    private static void removeChild(Array<JsonValue> tempArray, JsonValue root) {
        JsonValue rootParent = root.parent;
        JsonValue cur = root;

        while(cur != null) {
            JsonValue parent = cur.parent;
            JsonValue prev = cur.prev;
            JsonValue next = cur.next;

            if(cur.child == null) {
                tempArray.add(cur);
                if(cur.prev != null)
                    cur.prev.next = cur.next;
                if(cur.next != null)
                    cur.next.prev = cur.prev;
                if(cur.parent != null) {
                    if(cur.parent.child == cur) {
                        cur.parent.child = null;
                        cur.parent = null;
                    }
                }

                if(prev != null) {
                    cur = prev;
                    if(parent != null && parent.child == null) {
                        parent.child = cur;
                    }
                }
                else if(next != null) {
                    cur = next;
                    if(parent != null && parent.child == null) {
                        parent.child = cur;
                    }
                }
                else {
                    if(rootParent != null && parent == rootParent) {
                        cur = null; // stop at the parent child
                    }
                    else {
                        cur = parent;
                    }
                }
            }
            else {
                cur = cur.child;
            }
        }
    }
}