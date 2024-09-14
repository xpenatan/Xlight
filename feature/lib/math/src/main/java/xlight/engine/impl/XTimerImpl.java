package xlight.engine.impl;

import com.badlogic.gdx.utils.LongArray;
import xlight.engine.math.XTimer;

public class XTimerImpl implements XTimer {

    public static long TMP_TIME;
    public static LongArray TIMES = new LongArray();

    private long time; // Original time

    private long time2; // backup time

    private boolean isPause;

    /**
     * Milli
     */
    static public void TICK_START_MILLI() {
        TMP_TIME = System.currentTimeMillis();
    }

    /**
     * Milli
     */
    static public void TICK_MILLI() {
        long tickTime = System.currentTimeMillis() - TMP_TIME;
        TIMES.add(tickTime);
        TMP_TIME = System.currentTimeMillis();
    }

    /**
     * Nano
     */
    static public void TICK_NANO() {
        long tickTime = System.nanoTime() - TMP_TIME;
        TIMES.add(tickTime);
        TMP_TIME = System.nanoTime();
    }

    /**
     * Nano
     */
    static public void TICK_START_NANO() {
        TMP_TIME = System.nanoTime();
    }

    static public void TICK_PRINT() {
        TMP_TIME = 0;
        for(int i = 0; i < TIMES.size; i++)
            System.out.println("Time " + (i + 1) + ": " + TIMES.get(i));
        System.out.println("-----------------");
        TIMES.clear();
    }

    /**
     * Create a paused clock
     */
    public XTimerImpl() {
        this(true);
    }

    /**
     * Create a clock that is paused or not
     */
    public XTimerImpl(boolean paused) {
        time = getCurrentTime();
        setPause(paused);
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public long getTime() {
        return isPause ? time2 - time : getCurrentTime() - time;
    }

    @Override
    public void setTime(long milisegundos) {
        time = isPause ? time2 - milisegundos : getCurrentTime() - milisegundos;
    }

    @Override
    public long getSeconds() {
        return getTime() / 1000;
    }

    @Override
    public void setPause(boolean toPause) {
        if(toPause && !isPause) {
            time2 = getCurrentTime();
            isPause = true;
        }
        else if(toPause == false && isPause) {
            isPause = false;
            time = time + getCurrentTime() - time2;
        }
    }

    @Override
    public void reset() {
        time = getCurrentTime();
        time2 = time;
    }

    @Override
    public void addTime(int milisegundos) {
        setTime(getTime() + milisegundos);
    }

    @Override
    public boolean isPaused() {
        return isPause;
    }

    @Override
    public void resume() {
        setPause(false);
    }

    @Override
    public void pause() {
        setPause(true);
    }
}