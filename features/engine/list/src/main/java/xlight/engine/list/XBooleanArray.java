package xlight.engine.list;

import com.badlogic.gdx.utils.BooleanArray;
import xlight.engine.pool.XPoolable;

public class XBooleanArray extends BooleanArray implements XPoolable {
    @Override
    public void onReset() {
        clear();
    }
}