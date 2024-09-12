package xlight.engine.datamap;

import xlight.engine.list.XArray;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;

public class XDataMapArray extends XArray<XDataMap> implements XPoolable {

    private XPoolController poolController;

    public XDataMapArray(XPoolController poolController) {
        this.poolController = poolController;
    }

    @Override
    public void onReset() {
        clear();
    }

    @Override
    public void clear() {
        int size = getSize();
        for(int i = 0; i < size; i++) {
            XDataMap dataMap = get(i);
            dataMap.free();
        }
        super.clear();
    }

    @Override
    public Class<?> getPoolType() {
        return XDataMapArray.class;
    }

    public void free() {
        poolController.releaseObject(XDataMapArray.class, this);
    }

    public XDataMap obtainDataMap() {
        return XDataMap.obtain(poolController);
    }

    public static XDataMapArray obtain(XPoolController poolController) {
        return poolController.obtainObject(XDataMapArray.class);
    }
}
