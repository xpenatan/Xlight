package xlight.engine.properties;

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
import xlight.engine.impl.properties.XPropertiesImpl;
import xlight.engine.json.XJson;
import xlight.engine.json.XJsonValue;
import xlight.engine.json.pool.XJsonValuePool;
import xlight.engine.lang.pool.XPrimitivePoolUtil;
import xlight.engine.list.pool.XListPoolUtil;
import xlight.engine.properties.pool.XPropertiesPoolUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class XPropertiesTest {
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
        XPropertiesPoolUtil.registerPool(json, poolController);
    }

    @After
    public void dispose() {
        pool.clear();
        poolController.clear();
    }

    @Test
    public void test_object_not_null() {
        XProperties properties = XProperties.obtain(poolController);
        assertNotNull(properties);
        assertEquals(properties.getClass(), XPropertiesImpl.class);
    }

    @Test
    public void test_load_map_save_and_compare_json() throws IOException {
        XProperties properties1 = XProperties.obtain(poolController);
        XProperties properties2 = XProperties.obtain(poolController);
        XProperties properties3 = XProperties.obtain(poolController);
        XProperties properties4 = XProperties.obtain(poolController);
        String jsonStr1 = readString("complexStringJson_compare_equal_01.json");
        String jsonStr2 = readString("complexStringJson_compare_equal_02.json");

        XJsonValue jsonValueTest1 = json.loadJson(jsonStr1);
        XJsonValue jsonValueTest2 = json.loadJson(jsonStr2);

        properties1.loadJson(jsonStr1);
        properties2.loadJson(jsonStr2);
        XJsonValue jsonValue1 = properties1.saveJson();
        XJsonValue jsonValue2 = properties2.saveJson();

        String jsonValueStr1 = jsonValue1.toJson();
        String jsonValueStr2 = jsonValue2.toJson();

        properties3.loadJson(jsonValueStr1);
        properties4.loadJson(jsonValueStr2);

        XJsonValue jsonValue3 = properties3.saveJson();
        XJsonValue jsonValue4 = properties4.saveJson();

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
        XProperties properties1 = XProperties.obtain(poolController);
        XProperties properties2 = XProperties.obtain(poolController);
        String jsonStr1 = readString("complexStringJson_compare_equal_01.json");
        String jsonStr2 = readString("complexStringJson_compare_equal_02.json");
        properties1.loadJson(jsonStr1);
        properties2.loadJson(jsonStr2);

        assertEquals(properties1, properties2);
    }

    @Test
    public void test_map_copy_and_compare_datamap() throws IOException {
        XProperties properties1 = XProperties.obtain(poolController);
        XProperties properties2 = XProperties.obtain(poolController);
        XProperties propertiesCopy1 = XProperties.obtain(poolController);
        XProperties propertiesCopy2 = XProperties.obtain(poolController);
        String jsonStr1 = readString("complexStringJson_compare_equal_01.json");
        String jsonStr2 = readString("complexStringJson_compare_equal_02.json");
        properties1.loadJson(jsonStr1);
        properties2.loadJson(jsonStr2);

        propertiesCopy1.copy(properties1);
        propertiesCopy2.copy(properties2);

        assertEquals(properties1, propertiesCopy1);
        assertEquals(properties1, propertiesCopy2);
        assertEquals(properties2, propertiesCopy1);
        assertEquals(properties2, propertiesCopy2);
    }

    @Test
    public void test_load_map_save_and_compare_datamap_not_equal() throws IOException {
        XProperties properties1 = XProperties.obtain(poolController);
        XProperties properties2 = XProperties.obtain(poolController);
        String jsonStr1 = readString("complexStringJson_compare_different_01.json");
        String jsonStr2 = readString("complexStringJson_compare_different_02.json");
        properties1.loadJson(jsonStr1);
        properties2.loadJson(jsonStr2);

        assertNotEquals(properties1, properties2);
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

