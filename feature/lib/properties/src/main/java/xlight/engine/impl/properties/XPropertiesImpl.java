package xlight.engine.impl.properties;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import xlight.engine.json.XJson;
import xlight.engine.json.XJsonValue;
import xlight.engine.list.XArray;
import xlight.engine.list.XList;
import xlight.engine.list.XObjectMap;
import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPoolController;
import xlight.engine.properties.XProperties;

public class XPropertiesImpl implements XProperties {

    private final XObjectMap<String, Object> map;
    private final XPoolController poolController;
    private final XJson json;

    public XPropertiesImpl(XPoolController poolController, XJson json) {
        this.poolController = poolController;
        this.json = json;
        map = new XObjectMap<>();
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void put(String key, XProperties properties) {
        map.put(key, properties);
    }

    @Override
    public void put(String key, XArray<String> value) {
        if(value == null) {
            return;
        }
        XStringArray array = poolController.obtainObject(XStringArray.class);
        array.addAll(value);
        map.put(key, array);
    }

    @Override
    public XProperties putProperties(String key) {
        Object obj = map.get(key);
        if(obj instanceof XProperties) {
            return (XProperties)obj;
        }

        XProperties dataMap = XProperties.obtain(poolController);
        map.put(key, dataMap);
        return dataMap;
    }

    @Override
    public String get(String key, String defaultValue) {
        Object obj = map.get(key);
        if(obj instanceof String) {
            return (String)obj;
        }
        return defaultValue;
    }

    @Override
    public XProperties getProperties(String key) {
        Object obj = map.get(key);
        if(obj instanceof XProperties) {
            return (XProperties)obj;
        }
        return null;
    }

    @Override
    public boolean getStringArray(String key, Array<String> out) {
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
    public XStringArray getStringArray(String key) {
        Object o = map.get(key);
        return (o instanceof XStringArray) ? (XStringArray)o : null;
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public void copy(XProperties properties) {
        XPropertiesImpl prop = (XPropertiesImpl)properties;
        XObjectMap.XObjectMapNode<String, Object> cur = prop.map.getHead();
        while(cur != null) {
            String key = cur.getKey();
            Object object = cur.getValue();
            if(object instanceof XProperties) {
                XProperties value = (XProperties)object;
                XProperties dataMapValue = putProperties(key);
                dataMapValue.copy(value);
            }
            else if(object instanceof XStringArray) {
                XStringArray value = (XStringArray)object;
                put(key, value);
            }
            else {
                put(key, (String)object);
            }
            cur = cur.getNext();
        }
    }

    @Override
    public boolean remove(String key) {
        Object value = map.remove(key);
        return value != null;
    }

    @Override
    public void clear() {
        XList<Object> list = map.getList();
        for(Object object : list) {
            if(object instanceof XProperties) {
                poolController.releaseObject(object);
            }
        }
        map.clear();
    }

    @Override
    public boolean equals(Object other) {
        if(other != this && other instanceof XPropertiesImpl) {
            XPropertiesImpl otherDataMap = (XPropertiesImpl)other;
            XJsonValue jsonValue = saveJson();
            XJsonValue otherJsonValue = otherDataMap.saveJson();
            return jsonValue.equals(otherJsonValue);
        }
        return false;
    }

    @Override
    public void free() {
        poolController.releaseObject(XProperties.class, this);
    }

    @Override
    public int getSize() {
        return map.getSize();
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
        XObjectMap.XObjectMapNode<String, Object> cur = map.getHead();
        while(cur != null) {
            String key = cur.getKey();
            Object val = cur.getValue();

            if(val instanceof XProperties) {
                XProperties value = (XProperties)val;
                String keyStr = String.valueOf(key);
                XJsonValue child = value.saveJson();
                jsonValueRoot.addChildValue(keyStr, child);
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
            else {
                String value = (String)val;
                String keyStr = String.valueOf(key);
                XJsonValue child = json.createJson();
                child.set(value);
                jsonValueRoot.addChildValue(keyStr, child);
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
    public XList<XObjectMap.XObjectMapNode<String, Object>> getList() {
        return map.getNodeList();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + saveJsonStr();
    }

    private static void loadJsonInternal(XPropertiesImpl properties, XJsonValue json) {
        String name = json.getName();
        JsonValue.ValueType type = json.getType();
        if(type == JsonValue.ValueType.stringValue) {
            properties.put(name, json.asString());
        }
        else if(type == JsonValue.ValueType.array) {
            properties.loadArrayJson(name, json);
        }
        else if(type == JsonValue.ValueType.object) {
            XJsonValue childObj = json.getChild();
            if(childObj != null) {
                if(name != null) {
                    XProperties childProperties = properties.putProperties(name);
                    childProperties.loadJson(childObj);
                }
                else {
                    // This is called in the root json where there is no key. Ex:
//                    {
//                        "0": "9911"
//                    }
                    properties.loadJson(childObj);
                }
            }
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
            XStringArray array = poolController.obtainObject(XStringArray.class);
            XJsonValue arrayCur = jsonArray.getChild();
            while(arrayCur != null) {
                String val = arrayCur.asString();
                array.add(val);
                arrayCur = arrayCur.getNext();
            }
            map.put(name, array);
        }
    }
}