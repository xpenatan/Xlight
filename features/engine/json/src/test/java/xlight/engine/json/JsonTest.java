package xlight.engine.json;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import xlight.engine.json.pool.XJsonValuePool;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class JsonTest {

    static int POOL_START_SIZE = 1000;
    XJson json;
    XJsonValuePool pool;

    @Before
    public void setUp() throws Exception {
        pool = new XJsonValuePool(POOL_START_SIZE, POOL_START_SIZE);
        json = XJson.create(pool);
    }

    @Test
    public void test_create_json_not_null() throws IOException {
        XJsonValue jsonValue = json.createJson();

        assertNotNull(jsonValue);
        assertEquals(jsonValue.getClass(), XJsonValueImpl.class);
    }

    @Test
    public void test_pool_size() throws IOException {
        int freeBefore = pool.getFree();

        assertEquals(POOL_START_SIZE, freeBefore);
    }

    @Test
    public void test_json_return_to_pool() throws IOException {
        int freeBefore = pool.getFree();
        XJsonValue jsonValue = json.createJson();
        int freeAfter = pool.getFree();
        jsonValue.free();
        int freeReturn = pool.getFree();

        assertEquals(POOL_START_SIZE, freeBefore);
        assertEquals(POOL_START_SIZE-1, freeAfter);
        assertEquals(POOL_START_SIZE, freeReturn);
    }

    @Test
    public void test_load_simple_json_not_null() {
        String jsonStr = readString("simpleJson.json");
        XJsonValue jsonValue = json.loadJson(jsonStr);

        assertNotNull(jsonValue);
        assertEquals(jsonValue.getClass(), XJsonValueImpl.class);
    }

    @Test
    public void test_load_simple_json_and_compare_json() {
        String jsonStr = readString("simpleJson.json");
        XJsonValue jsonValue1 = json.loadJson(jsonStr);
        String jsonCopyStr = jsonValue1.toJson();
        XJsonValue jsonValue2 = json.loadJson(jsonCopyStr);
        assertEquals(jsonValue1, jsonValue2);

        int freeBefore = pool.getFree();
        jsonValue1.free();
        jsonValue2.free();
        int freeAfter = pool.getFree();

        assertEquals(966, freeBefore);
        assertEquals(POOL_START_SIZE, freeAfter);
    }

    @Test
    public void test_load_complex_json_and_compare_json() {
        String jsonStr = readString("complexJson_compare_different_01.json");
        XJsonValue jsonValue1 = json.loadJson(jsonStr);
        String jsonCopyStr = jsonValue1.toJson();
        XJsonValue jsonValue2 = json.loadJson(jsonCopyStr);
        assertEquals(jsonValue1, jsonValue2);

        int freeBefore = pool.getFree();
        jsonValue1.free();
        jsonValue2.free();
        int freeAfter = pool.getFree();

        assertEquals(826, freeBefore);
        assertEquals(POOL_START_SIZE, freeAfter);
    }

    @Test
    public void test_load_complex_json_and_compare_json_should_fail() {
        String jsonStr1 = readString("complexJson_compare_different_01.json");
        String jsonStr2 = readString("complexJson_compare_different_02.json");
        XJsonValue jsonValue1 = json.loadJson(jsonStr1);
        XJsonValue jsonValue2 = json.loadJson(jsonStr2);

        assertNotEquals(jsonValue1, jsonValue2);

        int freeBefore = pool.getFree();
        jsonValue1.free();
        jsonValue2.free();
        int freeAfter = pool.getFree();

        assertEquals(826, freeBefore);
        assertEquals(POOL_START_SIZE, freeAfter);
    }

    @Test
    public void test_load_complex_json_and_compare_json_should_succeed() {
        String jsonStr1 = readString("complexJson_compare_equal_01.json");
        String jsonStr2 = readString("complexJson_compare_equal_02.json");
        XJsonValue jsonValue1 = json.loadJson(jsonStr1);
        XJsonValue jsonValue2 = json.loadJson(jsonStr2);
        assertEquals(jsonValue1, jsonValue2);

        int freeBefore = pool.getFree();
        jsonValue1.free();
        jsonValue2.free();
        int freeAfter = pool.getFree();

        assertEquals(826, freeBefore);
        assertEquals(POOL_START_SIZE, freeAfter);
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