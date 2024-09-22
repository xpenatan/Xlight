package xlight.engine.g2d;

import xlight.engine.transform.XTransform;

public interface XRender2D {
    void onRender(int engineType, XBatch2D batch);
    void onUpdate(XTransform transform);
    int getRenderId();
}