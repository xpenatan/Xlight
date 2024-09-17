package xlight.engine.camera.controller;

public class XVelocityTracker {
    int sampleSize = 10;
    public float lastX, lastY;
    public float deltaX, deltaY;
    long lastTime;
    int numSamples;
    float[] meanX = new float[sampleSize];
    float[] meanY = new float[sampleSize];
    long[] meanTime = new long[sampleSize];

    public void start(float x, float y, long timeStamp) {
        lastX = x;
        lastY = y;
        deltaX = 0;
        deltaY = 0;
        numSamples = 0;
        for(int i = 0; i < sampleSize; i++) {
            meanX[i] = 0;
            meanY[i] = 0;
            meanTime[i] = 0;
        }
        lastTime = timeStamp;
    }

    public void update(float x, float y, long timeStamp) {
        long currTime = timeStamp;
        deltaX = x - lastX;
        deltaY = y - lastY;
        lastX = x;
        lastY = y;
        long deltaTime = currTime - lastTime;
        lastTime = currTime;
        int index = numSamples % sampleSize;
        meanX[index] = deltaX;
        meanY[index] = deltaY;
        meanTime[index] = deltaTime;
        numSamples++;
    }

    public float getVelocityX() {
        float meanX = getAverage(this.meanX, numSamples);
        float meanTime = getAverage(this.meanTime, numSamples) / 1000000000.0f;
        if(meanTime == 0)
            return 0;
        return meanX / meanTime;
    }

    public float getVelocityY() {
        float meanY = getAverage(this.meanY, numSamples);
        float meanTime = getAverage(this.meanTime, numSamples) / 1000000000.0f;
        if(meanTime == 0)
            return 0;
        return meanY / meanTime;
    }

    private float getAverage(float[] values, int numSamples) {
        numSamples = Math.min(sampleSize, numSamples);
        float sum = 0;
        for(int i = 0; i < numSamples; i++) {
            sum += values[i];
        }
        return sum / numSamples;
    }

    private long getAverage(long[] values, int numSamples) {
        numSamples = Math.min(sampleSize, numSamples);
        long sum = 0;
        for(int i = 0; i < numSamples; i++) {
            sum += values[i];
        }
        if(numSamples == 0)
            return 0;
        return sum / numSamples;
    }

    private float getSum(float[] values, int numSamples) {
        numSamples = Math.min(sampleSize, numSamples);
        float sum = 0;
        for(int i = 0; i < numSamples; i++) {
            sum += values[i];
        }
        if(numSamples == 0)
            return 0;
        return sum;
    }
}
