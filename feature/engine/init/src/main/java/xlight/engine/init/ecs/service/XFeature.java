package xlight.engine.init.ecs.service;

public class XFeature {
    public final int featureKey;

    boolean initialized = false;

    public XFeature(int featureKey) {
        this.featureKey = featureKey;
    }

    public void initFeature() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}