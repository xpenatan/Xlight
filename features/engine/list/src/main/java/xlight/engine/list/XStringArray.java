package xlight.engine.list;

import xlight.engine.pool.XPoolable;

public class XStringArray extends XArray<String> implements XPoolable {
    @Override
    public void onReset() {
        clear();
    }
}