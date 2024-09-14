package xlight.engine.impl.datamap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.JsonValue;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;
import xlight.engine.json.XJson;
import xlight.engine.json.XJsonValue;
import xlight.engine.lang.XBoolean;
import xlight.engine.lang.XByte;
import xlight.engine.lang.XChar;
import xlight.engine.lang.XDouble;
import xlight.engine.lang.XFloat;
import xlight.engine.lang.XInt;
import xlight.engine.lang.XLong;
import xlight.engine.lang.XPrimitive;
import xlight.engine.list.XArray;
import xlight.engine.list.XBooleanArray;
import xlight.engine.list.XFloatArray;
import xlight.engine.list.XIntArray;
import xlight.engine.list.XIntMap;
import xlight.engine.list.XIntMapListNode;
import xlight.engine.list.XList;
import xlight.engine.list.XStringArray;

public class XDataMapImpl implements XDataMap, XPoolable {

    private final XIntMap<Object> map;

    private final XPoolController poolController;
    private final XJson json;

    public XDataMapImpl(XPoolController poolController, XJson json) {
        this.poolController = poolController;
        this.json = json;
        map = new XIntMap<>();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + saveJsonStr();
    }

    @Override
    public void onReset() {
        clear();
    }

    @Override
    public Class<?> getPoolType() {
        return XDataMap.class;
    }

    @Override
    public XDataMap putDataMap(int key) {
        XDataMap dataMap = XDataMap.obtain(poolController);
        map.put(key, dataMap);
        return dataMap;
    }

    @Override
    public void put(int key, XDataMap dataMap) {
        map.put(key, dataMap);
    }

    @Override
    public XDataMap getDataMap(int key) {
        Object o = map.get(key);
        return (o instanceof XDataMap) ? (XDataMap)o : null;
    }

    @Override
    public XDataMapArray putDataMapArray(int key) {
        XDataMapArray dataMapArray = XDataMapArray.obtain(poolController);
        map.put(key, dataMapArray);
        return dataMapArray;
    }

    @Override
    public void put(int key, XDataMapArray dataMap) {
        map.put(key, dataMap);
    }

    @Override
    public XDataMapArray getDataMapArray(int key) {
        Object o = map.get(key);
        if(o instanceof XDataMapArray) {
            return (XDataMapArray)o;
        }
        return null;
    }

    @Override
    public void free() {
        poolController.releaseObject(XDataMap.class, this);
    }

    @Override
    public void copy(XDataMap map) {
        XDataMapImpl dataMap = (XDataMapImpl)map;
        XIntMapListNode<Object> cur = dataMap.map.getHead();
        while(cur != null) {
            int key = cur.getKey();
            Object val = cur.getValue();

            if(val instanceof XPrimitive) {
                XPrimitive value = (XPrimitive)val;
                XPrimitive.TYPE type = value.getType();
                switch(type) {
                    case Integer:
                        put(key, value.intValue());
                        break;
                    case Long:
                        put(key, value.longValue());
                        break;
                    case Float:
                        put(key, value.floatValue());
                        break;
                    case Double:
                        put(key, value.doubleValue());
                        break;
                    case Short:
                        put(key, value.shortValue());
                        break;
                    case Byte:
                        put(key, value.byteValue());
                        break;
                    case Boolean:
                        put(key, value.booleanValue());
                        break;
                    case Char:
                        put(key, value.charValue());
                        break;
                }
            }
            else if(val instanceof String) {
                put(key, (String)val);
            }
            else if(val instanceof XDataMap) {
                XDataMap value = (XDataMap)val;
                XDataMap dataMapValue = putDataMap(key);
                dataMapValue.copy(value);
            }
            else if(val instanceof XDataMapArray) {
                XDataMapArray value = (XDataMapArray)val;
                XDataMapArray itemDataMapArray = XDataMapArray.obtain(poolController);
                put(key, itemDataMapArray);
                int size = value.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap copyDataMap = value.get(i);
                    XDataMap newDatamap = XDataMap.obtain(poolController);
                    newDatamap.copy(copyDataMap);
                    itemDataMapArray.add(newDatamap);
                }
            }
            else if(val instanceof XIntArray) {
                XIntArray value = (XIntArray)val;
                put(key, value);
            }
            else if(val instanceof XFloatArray) {
                XFloatArray value = (XFloatArray)val;
                put(key, value);
            }
            else if(val instanceof XBooleanArray) {
                XBooleanArray value = (XBooleanArray)val;
                put(key, value);
            }
            else if(val instanceof XStringArray) {
                XStringArray value = (XStringArray)val;
                put(key, value);
            }
            cur = cur.getNext();
        }
    }

    @Override
    public void put(int key, Vector4 value) {
        if(value == null) {
            return;
        }
        XFloatArray array = poolController.obtainObject(XFloatArray.class);
        array.add(value.x);
        array.add(value.y);
        array.add(value.z);
        array.add(value.w);
        map.put(key, array);
    }

    @Override
    public void put(int key, Vector3 value) {
        if(value == null) {
            return;
        }
        XFloatArray array = poolController.obtainObject(XFloatArray.class);
        array.add(value.x);
        array.add(value.y);
        array.add(value.z);
        map.put(key, array);
    }

    @Override
    public void put(int key, Vector2 value) {
        if(value == null) {
            return;
        }
        XFloatArray array = poolController.obtainObject(XFloatArray.class);
        array.add(value.x);
        array.add(value.y);
        map.put(key, array);
    }

    @Override
    public void put(int key, IntArray value) {
        if(value == null) {
            return;
        }
        IntArray array = poolController.obtainObject(XIntArray.class);
        array.addAll(value);
        map.put(key, array);
    }

    @Override
    public void put(int key, FloatArray value) {
        if(value == null) {
            return;
        }
        XFloatArray array = poolController.obtainObject(XFloatArray.class);
        array.addAll(value);
        map.put(key, array);
    }

    @Override
    public void put(int key, BooleanArray value) {
        if(value == null) {
            return;
        }
        XBooleanArray array = poolController.obtainObject(XBooleanArray.class);
        array.addAll(value);
        map.put(key, array);
    }

    @Override
    public void put(int key, XArray<String> value) {
        if(value == null) {
            return;
        }
        XStringArray array = poolController.obtainObject(XStringArray.class);
        array.addAll(value);
        map.put(key, array);
    }

    @Override
    public void put(int key, int value) {
        XPrimitive primitive = poolController.obtainObject(XInt.class);
        primitive.intValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, long value) {
        XPrimitive primitive = poolController.obtainObject(XLong.class);
        primitive.longValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, float value) {
        XPrimitive primitive = poolController.obtainObject(XFloat.class);
        primitive.floatValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, double value) {
        XPrimitive primitive = poolController.obtainObject(XDouble.class);
        primitive.doubleValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, boolean value) {
        XPrimitive primitive = poolController.obtainObject(XBoolean.class);
        primitive.booleanValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, byte value) {
        XPrimitive primitive = poolController.obtainObject(XByte.class);
        primitive.byteValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, char value) {
        XPrimitive primitive = poolController.obtainObject(XChar.class);
        primitive.charValue(value);
        map.put(key, primitive);
    }

    @Override
    public void put(int key, String value) {
        map.put(key, value);
    }

    @Override
    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsKey(int key, Class<?> type) {
        Object value = map.get(key);
        return value != null && value.getClass() == type;
    }

    @Override
    public Object get(int key) {
        return map.get(key);
    }

    @Override
    public boolean getVector4(int key, Vector4 out) {
        Object obj = map.get(key);
        if(obj instanceof XFloatArray) {
            XFloatArray value = (XFloatArray)obj;
            if(value.size == 4) {
                out.x = value.get(0);
                out.y = value.get(1);
                out.z = value.get(2);
                out.w = value.get(3);
                return true;
            }
        }
        else if(obj instanceof XIntArray) {
            XIntArray value = (XIntArray)obj;
            if(value.size == 4) {
                out.x = value.get(0);
                out.y = value.get(1);
                out.z = value.get(2);
                out.w = value.get(3);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getVector3(int key, Vector3 out) {
        Object obj = map.get(key);
        if(obj instanceof XFloatArray) {
            XFloatArray value = (XFloatArray)obj;
            if(value.size == 3) {
                out.x = value.get(0);
                out.y = value.get(1);
                out.z = value.get(2);
                return true;
            }
        }
        else if(obj instanceof XIntArray) {
            XIntArray value = (XIntArray)obj;
            if(value.size == 3) {
                out.x = value.get(0);
                out.y = value.get(1);
                out.z = value.get(2);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getVector2(int key, Vector2 out) {
        Object obj = map.get(key);
        if(obj instanceof XFloatArray) {
            XFloatArray value = (XFloatArray)obj;
            if(value.size == 2) {
                out.x = value.get(0);
                out.y = value.get(1);
                return true;
            }
        }
        else if(obj instanceof XIntArray) {
            XIntArray value = (XIntArray)obj;
            if(value.size == 2) {
                out.x = value.get(0);
                out.y = value.get(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getIntArray(int key, IntArray out) {
        Object obj = map.get(key);
        if(obj instanceof XIntArray) {
            XIntArray value = (XIntArray)obj;
            out.addAll(value);
            return true;
        }
        return false;
    }

    @Override
    public XIntArray getIntArray(int key) {
        Object o = map.get(key);
        return (o instanceof XIntArray) ? (XIntArray)o : null;
    }

    @Override
    public boolean getFloatArray(int key, FloatArray out) {
        Object obj = map.get(key);
        if(obj instanceof XFloatArray) {
            XFloatArray value = (XFloatArray)obj;
            out.addAll(value);
            return true;
        }
        return false;
    }

    @Override
    public XFloatArray getFloatArray(int key) {
        Object o = map.get(key);
        return (o instanceof XFloatArray) ? (XFloatArray)o : null;
    }

    @Override
    public boolean getBooleanArray(int key, BooleanArray out) {
        Object obj = map.get(key);
        if(obj instanceof XBooleanArray) {
            XBooleanArray value = (XBooleanArray)obj;
            out.addAll(value);
            return true;
        }
        return false;
    }

    @Override
    public XBooleanArray getBooleanArray(int key) {
        Object o = map.get(key);
        return (o instanceof XBooleanArray) ? (XBooleanArray)o : null;
    }

    @Override
    public boolean getStringArray(int key, Array<String> out) {
        Object obj = map.get(key);
        if(obj instanceof XStringArray) {
            XStringArray value = (XStringArray)obj;
            for(String val : value.getList()) {
                out.addAll(val);
            }
            return true;
        }
        return false;
    }

    @Override
    public XStringArray getStringArray(int key) {
        Object o = map.get(key);
        return (o instanceof XStringArray) ? (XStringArray)o : null;
    }

    @Override
    public int getInt(int key, int defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.intValue() : defaultValue;
    }

    @Override
    public long getLong(int key, long defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.longValue() : defaultValue;
    }

    @Override
    public float getFloat(int key, float defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.floatValue() : defaultValue;
    }

    @Override
    public double getDouble(int key, double defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.doubleValue() : defaultValue;
    }

    @Override
    public boolean getBoolean(int key, boolean defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.booleanValue() : defaultValue;
    }

    @Override
    public byte getByte(int key, byte defaultValue) {
        Object o = map.get(key);
        XPrimitive primitive = (o instanceof XPrimitive) ? (XPrimitive)o : null;
        return primitive != null ? primitive.byteValue() : defaultValue;
    }

    @Override
    public String getString(int key, String defaultValue) {
        Object value = map.get(key);
        if(value instanceof String) {
            return (String)value;
        }
        return defaultValue;
    }

    @Override
    public boolean remove(int key) {
        Object value = map.remove(key);
        return value != null;
    }

    @Override
    public void clear() {
        XList<Object> list = map.getList();
        for(Object object : list) {
            if(!(object instanceof String)) {
                poolController.releaseObject(object);
            }
        }
        map.clear();
    }

    @Override
    public boolean equals(Object other) {
        if(other != this && other instanceof XDataMapImpl) {
            XDataMapImpl otherDataMap = (XDataMapImpl)other;
            XJsonValue jsonValue = saveJson();
            XJsonValue otherJsonValue = otherDataMap.saveJson();
            return jsonValue.equals(otherJsonValue);
        }
        return false;
    }

    @Override
    public String saveJsonStr() {
        XJsonValue jsonValue = this.saveJson();
        String jsonStr = jsonValue.toJson();
        jsonValue.free();
        return jsonStr;
    }

    @Override
    public XJsonValue saveJson() {
        XJsonValue jsonValueRoot = json.createJson();
        XIntMapListNode<Object> cur = map.getHead();
        while(cur != null) {
            int key = cur.getKey();
            Object val = cur.getValue();

            if(val instanceof XPrimitive) {
                XPrimitive value = (XPrimitive)val;
                XPrimitive.TYPE type = value.getType();
                if(type == XPrimitive.TYPE.Integer) {
                    String keyStr = String.valueOf(key);
                    XJsonValue child = json.createJson();
                    child.set(value.intValue());
                    jsonValueRoot.addChildValue(keyStr, child);
                }
                else if(type == XPrimitive.TYPE.Float) {
                    String keyStr = String.valueOf(key);
                    XJsonValue child = json.createJson();
                    child.set(value.floatValue());
                    jsonValueRoot.addChildValue(keyStr, child);
                }
                else if(type == XPrimitive.TYPE.Boolean) {
                    String keyStr = String.valueOf(key);
                    XJsonValue child = json.createJson();
                    child.set(value.booleanValue());
                    jsonValueRoot.addChildValue(keyStr, child);
                }
            }
            else if(val instanceof String) {
                String value = (String)val;
                String keyStr = String.valueOf(key);
                XJsonValue child = json.createJson();
                child.set(value);
                jsonValueRoot.addChildValue(keyStr, child);
            }
            else if(val instanceof XDataMap) {
                XDataMap value = (XDataMap)val;
                String keyStr = String.valueOf(key);
                XJsonValue child = value.saveJson();
                jsonValueRoot.addChildValue(keyStr, child);
            }
            else if(val instanceof XDataMapArray) {
                XDataMapArray value = (XDataMapArray)val;
                String keyStr = String.valueOf(key);
                XJsonValue childArray = json.createJson();
                childArray.setType(JsonValue.ValueType.array);
                int size = value.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap dataMap = value.get(i);
                    XJsonValue arrayItem = dataMap.saveJson();
                    childArray.addArrayValue(arrayItem);
                }
                jsonValueRoot.addChildValue(keyStr, childArray);
            }
            else if(val instanceof XIntArray) {
                XIntArray value = (XIntArray)val;
                String keyStr = String.valueOf(key);
                XJsonValue childArray = json.createJson();
                childArray.setType(JsonValue.ValueType.array);
                for(int i = 0; i < value.size; i++) {
                    int v = value.get(i);
                    XJsonValue arrayItem = json.createJson();
                    arrayItem.set(v);
                    childArray.addArrayValue(arrayItem);
                }
                jsonValueRoot.addChildValue(keyStr, childArray);
            }
            else if(val instanceof XFloatArray) {
                XFloatArray value = (XFloatArray)val;
                String keyStr = String.valueOf(key);
                XJsonValue childArray = json.createJson();
                childArray.setType(JsonValue.ValueType.array);
                for(int i = 0; i < value.size; i++) {
                    float v = value.get(i);
                    XJsonValue arrayItem = json.createJson();
                    arrayItem.set(v);
                    childArray.addArrayValue(arrayItem);
                }
                jsonValueRoot.addChildValue(keyStr, childArray);

            }
            else if(val instanceof XBooleanArray) {
                XBooleanArray value = (XBooleanArray)val;
                String keyStr = String.valueOf(key);
                XJsonValue childArray = json.createJson();
                childArray.setType(JsonValue.ValueType.array);
                for(int i = 0; i < value.size; i++) {
                    boolean v = value.get(i);
                    XJsonValue arrayItem = json.createJson();
                    arrayItem.set(v);
                    childArray.addArrayValue(arrayItem);
                }
                jsonValueRoot.addChildValue(keyStr, childArray);
            }
            else if(val instanceof XStringArray) {
                XStringArray value = (XStringArray)val;
                String keyStr = String.valueOf(key);
                XJsonValue childArray = json.createJson();
                childArray.setType(JsonValue.ValueType.array);
                for(int i = 0; i < value.getSize(); i++) {
                    String v = value.get(i);
                    XJsonValue arrayItem = json.createJson();
                    arrayItem.set(v);
                    childArray.addArrayValue(arrayItem);
                }
                jsonValueRoot.addChildValue(keyStr, childArray);
            }
            cur = cur.getNext();
        }
        return jsonValueRoot;
    }

    @Override
    public void loadJson(String jsonStr) {
        XJsonValue jsonValue = json.loadJson(jsonStr);
        this.loadJson(jsonValue);
        jsonValue.free();
    }

    @Override
    public void loadJson(XJsonValue json) {
        if(json == null) {
            return;
        }

        loadJsonInternal(this, json);

        XJsonValue next = json.getNext();
        loadJson(next);
    }

    @Override
    public int getSize() {
        return map.getSize();
    }

    private static void loadJsonInternal(XDataMapImpl dataMap, XJsonValue json) {
        String name = json.getName();
        JsonValue.ValueType type = json.getType();
        if(type == JsonValue.ValueType.stringValue) {
            int key = Integer.parseInt(name);
            dataMap.put(key, json.asString());
        }
        else if(type == JsonValue.ValueType.longValue) {
            int key = Integer.parseInt(name);
            dataMap.put(key, json.asInt());
        }
        else if(type == JsonValue.ValueType.doubleValue) {
            int key = Integer.parseInt(name);
            dataMap.put(key, json.asFloat());
        }
        else if(type == JsonValue.ValueType.booleanValue) {
            int key = Integer.parseInt(name);
            dataMap.put(key, json.asBoolean());
        }
        else if(type == JsonValue.ValueType.array) {
            dataMap.loadArrayJson(name, json);
        }
        else if(type == JsonValue.ValueType.object) {
            XJsonValue childObj = json.getChild();
            if(childObj != null) {
                if(name != null) {
                    int idx = isIntOrString(childObj);
                    if(idx == 1) {
                        //Int data map
                        int key = Integer.parseInt(name);
                        XDataMap childDataMap = dataMap.putDataMap(key);
                        childDataMap.loadJson(childObj);
                    }
                }
                else {
                    // This is called in the root json where there is no key. Ex:
//                    {
//                        "0": "9911"
//                    }
                    dataMap.loadJson(childObj);
                }
            }
        }
    }

    private static int isIntOrString(XJsonValue json) {
        if(json == null)
            return 0;
        boolean isKeyInt = true;
        boolean isValueString = false;

        //Check if all keys is integer
        XJsonValue cur = json;
        while(cur != null) {
            String key = cur.getName();
            if(!isStringInt(key)) {
                isKeyInt = false;
                break;
            }
            cur = cur.getNext();
        }

        if(isKeyInt) {
            return 1;
        }
        else {
            isValueString = true;
            // Key contains at least 1 string name. Check if every value is string
            cur = json;
            while(cur != null) {
                JsonValue.ValueType type = cur.getType();
                if(type != JsonValue.ValueType.stringValue && type != JsonValue.ValueType.object) {
                    isValueString = false;
                    break;
                }
                cur = cur.getNext();
            }

            if(isValueString) {
                return 2;
            }
        }
        // Must not parse this json because its wrong format
        return 0;
    }

    private static boolean isStringInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private void loadArrayJson(String name, XJsonValue jsonArray) {
        XJsonValue cur = jsonArray.getChild();
        JsonValue.ValueType arrayType = JsonValue.ValueType.nullValue;

        int floatCount = 0;
        int booleanCount = 0;
        int stringCount = 0;
        int intCount = 0;

        while(cur != null) {
            JsonValue.ValueType childType = cur.getType();
            if(childType == JsonValue.ValueType.nullValue) {
                floatCount = 0;
                booleanCount = 0;
                stringCount = 0;
                intCount = 0;
                break;
            }
            else if(childType == JsonValue.ValueType.stringValue) {
                stringCount++;
            }
            else if(childType == JsonValue.ValueType.doubleValue) {
                floatCount++;
            }
            else if(childType == JsonValue.ValueType.longValue) {
                intCount++;
            }
            else if(childType == JsonValue.ValueType.booleanValue) {
                booleanCount++;
            }
            else if(childType == JsonValue.ValueType.object) {
                floatCount = 0;
                booleanCount = 0;
                stringCount = 0;
                intCount = 0;
                arrayType = JsonValue.ValueType.object;
                break;
            }
            else if(childType == JsonValue.ValueType.array) {
                break;
            }
            cur = cur.getNext();
        }

        if(stringCount == 0 && booleanCount == 0) {
            if(intCount > 0) {
                arrayType = JsonValue.ValueType.longValue;
            }
            if(floatCount > 0) {
                arrayType = JsonValue.ValueType.doubleValue;
            }
        }
        else if(stringCount > 0 && booleanCount == 0 && intCount == 0 && floatCount == 0){
            arrayType = JsonValue.ValueType.stringValue;
        }
        else if(booleanCount > 0 && stringCount == 0 && intCount == 0 && floatCount == 0){
            arrayType = JsonValue.ValueType.booleanValue;
        }


        if(arrayType == JsonValue.ValueType.stringValue) {
            int key = Integer.parseInt(name);
            XStringArray array = poolController.obtainObject(XStringArray.class);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                String val = arrayCur.asString();
                array.add(val);
                arrayCur = arrayCur.getNext();
            }
            map.put(key, array);
        }
        else if(arrayType == JsonValue.ValueType.longValue) {
            int key = Integer.parseInt(name);
            XIntArray array = poolController.obtainObject(XIntArray.class);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                int val = arrayCur.asInt();
                array.add(val);
                arrayCur = arrayCur.getNext();
            }
            map.put(key, array);
        }
        else if(arrayType == JsonValue.ValueType.booleanValue) {
            int key = Integer.parseInt(name);
            XBooleanArray array = poolController.obtainObject(XBooleanArray.class);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                boolean val = arrayCur.asBoolean();
                array.add(val);
                arrayCur = arrayCur.getNext();
            }
            map.put(key, array);
        }
        else if(arrayType == JsonValue.ValueType.doubleValue) {
            int key = Integer.parseInt(name);
            XFloatArray array = poolController.obtainObject(XFloatArray.class);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                float val = arrayCur.asFloat();
                array.add(val);
                arrayCur = arrayCur.getNext();
            }
            map.put(key, array);
        }
        else if(arrayType == JsonValue.ValueType.object) {
            int key = Integer.parseInt(name);
//            XDataMapArray dataMapArray = putDataMapArray(key, 0);
            XDataMapArray dataMapArray = XDataMapArray.obtain(poolController);
            put(key, dataMapArray);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                XDataMapImpl arrayItem = (XDataMapImpl)XDataMap.obtain(poolController);
                loadJsonInternal(arrayItem, arrayCur);
                dataMapArray.add(arrayItem);
                arrayCur = arrayCur.getNext();
            }
        }
//        else if(arrayType == JsonValue.ValueType.array) {
            // array not supported
            // Maybe support primitive matrix?
//        }
    }
}