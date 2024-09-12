package xlight.engine.list;

import com.badlogic.gdx.utils.IntArray;
import xlight.engine.pool.XPoolable;

public class XIntArray extends IntArray implements XPoolable {
    @Override
    public void onReset() {
        clear();
    }
}