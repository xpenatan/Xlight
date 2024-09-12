package xlight.engine.json;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import xlight.engine.json.pool.XJsonValuePool;

public class XJsonImpl implements XJson {

    Json json = new Json();

    private final XJsonValuePool pool;

    private XJsonReader jsonReader;

    public XJsonImpl(XJsonValuePool pool) {
        this.pool = pool;
        // Type javascript so string stay double quotation
        json.setOutputType(JsonWriter.OutputType.javascript);
        jsonReader = new XJsonReader(pool);
    }

    @Override
    public XJsonValue loadJson(String jsonStr) {
        XJsonValue jsonValue = (XJsonValue)jsonReader.parse(jsonStr);
        return jsonValue;
    }

    @Override
    public String prettyPrint(String input) {
        return json.prettyPrint(input);
    }

    @Override
    public XJsonValue createJson() {
        return pool.obtain();
    }
}
