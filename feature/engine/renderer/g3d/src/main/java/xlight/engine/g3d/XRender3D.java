package xlight.engine.g3d;

import xlight.engine.transform.XTransform;

public interface XRender3D {
    void onRender(int engineType, XBatch3D batch);
    void onUpdate(XTransform transform);
}