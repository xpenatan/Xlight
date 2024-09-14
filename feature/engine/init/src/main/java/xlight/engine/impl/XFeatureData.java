package xlight.engine.impl;

import com.badlogic.gdx.utils.IntArray;
import xlight.engine.init.ecs.service.XFeature;
import xlight.engine.init.ecs.service.XInitFeatureListener;

public class XFeatureData {

    private XFeature feature;

    private IntArray featureInitializedDependency = new IntArray();
    private IntArray featureDependency = new IntArray();

    private XInitFeatureListener featureStart;
    private Runnable run;

    public boolean initListenerCalled = false;

    XInitFeatureServiceImpl service;

    public XFeatureData(XInitFeatureServiceImpl service, int featureKey, XInitFeatureListener featureStart) {
        feature = new XFeature(featureKey);
        this.service = service;
        this.featureStart = featureStart;
    }

    public XFeatureData(XInitFeatureServiceImpl service, Runnable run) {
        feature = new XFeature(0);
        this.service = service;
        this.run = run;
    }

    public void addFeatureDependency(int featureKey) {
        featureDependency.add(featureKey);
    }

    public boolean isInitialized() {
        return feature.isInitialized();
    }

    public void update() {
        if(!initListenerCalled) {
            boolean canInitialize = true;
            for(int i = 0; i < featureDependency.size; i++) {
                int key = featureDependency.get(i);
                boolean featureInitialized = service.isFeatureInitialized(key);
                if(featureInitialized) {
                    featureDependency.removeIndex(i);
                    i--;
                    featureInitializedDependency.add(key);
                }
                canInitialize = canInitialize && featureInitialized;
            }

            if(canInitialize) {
                initListenerCalled = true;

                if(featureStart != null) {
                    featureStart.initFeature(feature);
                }
                else if(run != null) {
                    run.run();
                }
            }
        }
    }

    public void printNotInitializedFeatures() {
        IntArray notInitializedFeatures = getNotInitializedFeatures();
        for(int i = 0; i < notInitializedFeatures.size; i++) {
            int key = notInitializedFeatures.get(i);
            System.out.println("Not initialized feature: " + key);
        }
    }

    public IntArray getNotInitializedFeatures() {
        return featureDependency;
    }
}