package xlight.engine.datamap;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import xlight.engine.impl.XPoolControllerImpl;
import xlight.engine.pool.XPoolController;
import xlight.engine.datamap.pool.XDataMapPoolUtil;
import xlight.engine.impl.datamap.XDataMapImpl;
import xlight.engine.json.XJson;
import xlight.engine.json.XJsonValue;
import xlight.engine.json.pool.XJsonValuePool;
import xlight.engine.lang.pool.XPrimitivePoolUtil;
import xlight.engine.list.pool.XListPoolUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class XDataMapTest {
    static int POOL_START_SIZE = 1000;

    XPoolController poolController;
    XJson json;
    XJsonValuePool pool;

    @Before
    public void setUp() throws Exception {
        pool = new XJsonValuePool(POOL_START_SIZE);
        json = XJson.create(pool);

        poolController = new XPoolControllerImpl();

        XListPoolUtil.registerPool(poolController);
        XPrimitivePoolUtil.registerPool(poolController);
        XDataMapPoolUtil.registerPool(json, poolController);
    }

    @After
    public void dispose() {
        pool.clear();
        poolController.clear();
    }

    @Test
    public void test_object_not_null() {
        XDataMap dataMap = XDataMap.obtain(poolController);
        assertNotNull(dataMap);
        assertEquals(dataMap.getClass(), XDataMapImpl.class);
    }

    @Test
    public void test_load_map_save_and_compare_json() throws IOException {
        XDataMap dataMap1 = XDataMap.obtain(poolController);
        XDataMap dataMap2 = XDataMap.obtain(poolController);
        XDataMap dataMap3 = XDataMap.obtain(poolController);
        XDataMap dataMap4 = XDataMap.obtain(poolController);
        String jsonStr1 = readString("complexJson_compare_equal_01.json");
        String jsonStr2 = readString("complexJson_compare_equal_02.json");

        XJsonValue jsonValueTest1 = json.loadJson(jsonStr1);
        XJsonValue jsonValueTest2 = json.loadJson(jsonStr2);

        dataMap1.loadJson(jsonStr1);
        dataMap2.loadJson(jsonStr2);
        XJsonValue jsonValue1 = dataMap1.saveJson();
        XJsonValue jsonValue2 = dataMap2.saveJson();

        String jsonValueStr1 = jsonValue1.toJson();
        String jsonValueStr2 = jsonValue2.toJson();

        dataMap3.loadJson(jsonValueStr1);
        dataMap4.loadJson(jsonValueStr2);

        XJsonValue jsonValue3 = dataMap3.saveJson();
        XJsonValue jsonValue4 = dataMap4.saveJson();

        assertEquals(jsonValueTest1, jsonValueTest2);
        assertEquals(jsonValue1, jsonValue2);
        assertEquals(jsonValue1, jsonValue3);
        assertEquals(jsonValue1, jsonValue4);
        assertEquals(jsonValue2, jsonValue3);
        assertEquals(jsonValue2, jsonValue4);
        assertEquals(jsonValue3, jsonValue4);
    }

    @Test
    public void test_load_map_save_and_compare_datamap() throws IOException {
        XDataMap dataMap1 = XDataMap.obtain(poolController);
        XDataMap dataMap2 = XDataMap.obtain(poolController);
        String jsonStr1 = readString("complexJson_compare_equal_01.json");
        String jsonStr2 = readString("complexJson_compare_equal_02.json");
        dataMap1.loadJson(jsonStr1);
        dataMap2.loadJson(jsonStr2);

        assertEquals(dataMap1, dataMap2);
    }

    @Test
    public void test_map_copy_and_compare_datamap() throws IOException {
        XDataMap dataMap1 = XDataMap.obtain(poolController);
        XDataMap dataMap2 = XDataMap.obtain(poolController);
        XDataMap dataMapCopy1 = XDataMap.obtain(poolController);
        XDataMap dataMapCopy2 = XDataMap.obtain(poolController);
        String jsonStr1 = readString("complexJson_compare_equal_01.json");
        String jsonStr2 = readString("complexJson_compare_equal_02.json");
        dataMap1.loadJson(jsonStr1);
        dataMap2.loadJson(jsonStr2);

        dataMapCopy1.copy(dataMap1);
        dataMapCopy2.copy(dataMap2);

        assertEquals(dataMap1, dataMapCopy1);
        assertEquals(dataMap1, dataMapCopy2);
        assertEquals(dataMap2, dataMapCopy1);
        assertEquals(dataMap2, dataMapCopy2);
    }

    @Test
    public void test_load_map_save_and_compare_datamap_not_equal() throws IOException {
        XDataMap dataMap1 = XDataMap.obtain(poolController);
        XDataMap dataMap2 = XDataMap.obtain(poolController);
        String jsonStr1 = readString("complexJson_compare_different_01.json");
        String jsonStr2 = readString("complexJson_compare_different_02.json");
        dataMap1.loadJson(jsonStr1);
        dataMap2.loadJson(jsonStr2);

        assertNotEquals(dataMap1, dataMap2);
    }

    public String readString (String file) {

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(file);

        StringBuilder output = new StringBuilder();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(is);
            char[] buffer = new char[256];
            while (true) {
                int length = reader.read(buffer);
                if (length == -1) break;
                output.append(buffer, 0, length);
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading layout file: " + this, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
        String outputStr = output.toString();
        return json.prettyPrint(outputStr);
    }

}

