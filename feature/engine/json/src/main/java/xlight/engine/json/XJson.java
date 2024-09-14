package xlight.engine.json;

import xlight.engine.pool.XPoolController;
import xlight.engine.json.pool.XJsonValuePool;

public interface XJson {

    static XJson create(XPoolController poolController) {
        XJsonValuePool pool = poolController.getPool(XJsonValue.class);
        return create(pool);
    }

    static XJson create() {
        return create(new XJsonValuePool(200, 300));
    }

    static XJson create(XJsonValuePool pool) {
        return new XJsonImpl(pool);
    }

    /** Convert string json to a json object representation */
    XJsonValue loadJson(String jsonStr);

    String prettyPrint(String input);

    XJsonValue createJson();
}
