package xlight.engine.init.ecs.service;

public interface XInitFeatureService {

    boolean isFeatureInitialized(int key);

    /**
     * Add a feature to be initialized. Use feature object to initialize the feature.
     */
    void addFeature(int key, XInitFeatureListener featureStart);

    /**
     * Use feature object to initialize the feature by initFeature
     */
    void addFeature(int key, XInitFeatureListener featureStart, int ... featureDependencies);

    /**
     * Works similar to addFeature, but it's only called if all features are initialized.
     */
    void addFeatureDependency(Runnable run, int featureDependency, int ... featureDependencies);
}