package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.service.XService;
import xlight.engine.init.ecs.service.XInitFeatureListener;
import xlight.engine.init.ecs.service.XInitFeatureService;

class XInitFeatureServiceImpl implements XInitFeatureService, XService {

    IntMap<XFeatureData> features = new IntMap<>();
    Array<XFeatureData> featureDep = new Array<>();

    @Override
    public boolean isFeatureInitialized(int key) {
        XFeatureData featureData = features.get(key);
        if(featureData != null) {
            return featureData.isInitialized();
        }
        return false;
    }

    @Override
    public void addFeature(int key, XInitFeatureListener featureStart) {
        addFeature(key, featureStart, 0);
    }

    @Override
    public void addFeature(int key, XInitFeatureListener featureStart, int ... featureDependencies) {
        if(key != 0 && features.containsKey(key)) {
            throw new RuntimeException("Feature key already exists: " + key);
        }
        XFeatureData featureData = new XFeatureData(this, key, featureStart);
        updateDependency(featureData, featureDependencies);
        features.put(key, featureData);
    }

    @Override
    public void addFeatureDependency(Runnable run, int... featureDependencies) {
        XFeatureData featureData = new XFeatureData(this, run);
        updateDependency(featureData, featureDependencies);
        featureDep.add(featureData);
    }

    private void updateDependency(XFeatureData featureData, int ... featureDependencies) {
        if(!(featureDependencies.length == 1 && featureDependencies[0] == 0)) {
            for(int i = 0; i < featureDependencies.length; i++) {
                int featureDependency = featureDependencies[i];
                featureData.addFeatureDependency(featureDependency);
            }
        }
    }

    @Override
    public void onTick(XECSWorld world) {
        for(IntMap.Entry<XFeatureData> feature : features) {
            XFeatureData value = feature.value;
            value.update();
        }

        for(int i = 0; i < featureDep.size; i++) {
            XFeatureData dep = featureDep.get(i);
            dep.update();
            if(dep.initListenerCalled) {
                featureDep.removeIndex(i);
                i--;
            }
        }
    }
}