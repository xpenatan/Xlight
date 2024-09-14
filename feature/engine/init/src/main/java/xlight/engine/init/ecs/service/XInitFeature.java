package xlight.engine.init.ecs.service;

public class XInitFeature {
    public final int featureKey;

    boolean initialized = false;

    public XInitFeature(int featureKey) {
        this.featureKey = featureKey;
    }

    public void initFeature() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}