package xlight.engine.list;

import com.badlogic.gdx.utils.FloatArray;
import xlight.engine.pool.XPoolable;

public class XFloatArray extends FloatArray implements XPoolable {
    @Override
    public void onReset() {
        clear();
    }
}
