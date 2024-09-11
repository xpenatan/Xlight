package xlight.engine.ecs.util.timestep.timestep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class XSimpleFixedTimeStep {
    //	float timestep = 1.0f / 61.0f;
    float timestep = 0.0133f;

    double accumulator = 0;

    int cycles;

    long time = 0; // system time

    Array<XStepUpdate> stepUpdates;

    boolean enableTimeStep = true;

    public XSimpleFixedTimeStep() {
        stepUpdates = new Array<>();
    }

    public void tick() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(deltaTime > 0.25f)
            deltaTime = 0.25f;
        accumulator += deltaTime;

        if(accumulator >= timestep)
            cycles = 0;
        else
            cycles = -1;

        while(accumulator >= timestep) {
            accumulator -= timestep;
            for(int i = 0; i < stepUpdates.size; i++) {
                XStepUpdate stepUpdate = stepUpdates.get(i);
                stepUpdate.onUpdate();
            }
            time += (timestep * 100000);
            cycles++;

            if(enableTimeStep == false)
                break;
        }
    }

    public float getTimestep() {
        return timestep;
    }

    public void setTimestep(float timestep) {
        this.timestep = timestep;
    }

    public long currentTimeMillis() {
        return time / 100;
    }

    public void addStepListener(XStepUpdate listener) {
        stepUpdates.add(listener);
    }

    public void removeStepListener(XStepUpdate listener) {
        stepUpdates.removeValue(listener, true);
    }

    public double getAccumulator() {
        return accumulator;
    }
}