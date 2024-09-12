package xlight.engine.properties;

import com.badlogic.gdx.utils.Array;
import xlight.engine.json.XJsonValue;
import xlight.engine.list.XArray;
import xlight.engine.list.XList;
import xlight.engine.list.XObjectMap;
import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPoolController;

/**
 * Putting new values will replace the existing ones
 */
public interface XProperties {

    static XProperties obtain(XPoolController poolController) {
        return poolController.obtainObject(XProperties.class);
    }

    void put(String key, String value);
    void put(String key, XProperties properties);
    void put(String key, XArray<String> value);
    XProperties putProperties(String key);
    String get(String key, String defaultValue);
    XProperties getProperties(String key);
    /** Copy the array from memory. */
    boolean getStringArray(String key, Array<String> out);
    /** Return the array reference. Don't keep reference. */
    XStringArray getStringArray(String key);

    boolean remove(String key);
    boolean containsKey(String key);
    void copy(XProperties properties);
    /** Return true if key is set and value is removed. Pool objects return to pool */
    void clear();
    void free();
    int getSize();
    String saveJsonStr();
    XJsonValue saveJson();
    /** Load json data to this map. Will replace if key/value exists. Pool objects return to pool. */
    void loadJson(String jsonStr);
    void loadJson(XJsonValue json);

    XList<XObjectMap.XObjectMapNode<String, Object>> getList();
}