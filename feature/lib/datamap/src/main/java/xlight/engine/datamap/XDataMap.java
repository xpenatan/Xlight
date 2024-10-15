package xlight.engine.datamap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.json.XJsonValue;
import xlight.engine.list.XArray;
import xlight.engine.list.XBooleanArray;
import xlight.engine.list.XFloatArray;
import xlight.engine.list.XIntArray;
import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPoolController;

/**
 * Putting new values will replace the existing ones
 */
public interface XDataMap {

    static XDataMap obtain(XPoolController poolController) {
        return poolController.obtainObject(XDataMap.class);
    }

    /** Clearing this datapa, will also clear/remove all datamap values */
    XDataMap putDataMap(int key);
    void put(int key, XDataMap dataMap);
    XDataMap getDataMap(int key);
    XDataMapArray putDataMapArray(int key);
    void put(int key, XDataMapArray dataMap);
    XDataMapArray getDataMapArray(int key);

    /** Return this datamap to pool. All references needs to be removed */
    void free();

    void copy(XDataMap dataMap);
    XDataMap clone();

    /** Vector4 is converted to an array */
    void put(int key, Vector4 value);
    /** Vector3 is converted to an array */
    void put(int key, Vector3 value);
    /** Vector2 is converted to an array */
    void put(int key, Vector2 value);
    /** Copy array data */
    void put(int key, IntArray value);
    /** Copy array data */
    void put(int key, FloatArray value);
    /** Copy array data */
    void put(int key, BooleanArray value);
    /** Copy array data */
    void put(int key, XArray<String> value);
    void put(int key, int value);
    void put(int key, long value);
    void put(int key, float value);
    void put(int key, double value);
    void put(int key, boolean value);
    void put(int key, byte value);
    void put(int key, char value);
    void put(int key, String value);
    boolean containsKey(int key);
    /** Check if key exist and the object contains the same type */
    boolean containsKey(int key, Class<?> type);
    /** Return object or null if key is invalid */
    Object get(int key);
    boolean getVector4(int key, Vector4 out);
    boolean getVector3(int key, Vector3 out);
    boolean getVector2(int key, Vector2 out);
    /** Copy the array from memory */
    boolean getIntArray(int key, IntArray out);
    /** Return the array reference. Don't keep reference. */
    XIntArray getIntArray(int key);
    /** Copy the array from memory. */
    boolean getFloatArray(int key, FloatArray out);
    /** Return the array reference. Don't keep reference. */
    XFloatArray getFloatArray(int key);
    /** Copy the array from memory. */
    boolean getBooleanArray(int key, BooleanArray out);
    /** Return the array reference. Don't keep reference. */
    XBooleanArray getBooleanArray(int key);
    /** Copy the array from memory. */
    boolean getStringArray(int key, Array<String> out);
    /** Return the array reference. Don't keep reference. */
    XStringArray getStringArray(int key);
    int getInt(int key, int defaultValue);
    long getLong(int key, long defaultValue);
    float getFloat(int key, float defaultValue);
    double getDouble(int key, double defaultValue);
    boolean getBoolean(int key, boolean defaultValue);
    byte getByte(int key, byte defaultValue);
    String getString(int key, String defaultValue);
    /** Return true if key is set and value is removed. Pool objects return to pool */
    boolean remove(int key);
    /** Clear all data. Pool objects return to pool */
    void clear();
    String saveJsonStr();
    XJsonValue saveJson();
    /** Load json data to this map. Will replace if key/value exists. Pool objects return to pool. */
    void loadJson(String jsonStr);
    void loadJson(XJsonValue json);
    int getSize();
}