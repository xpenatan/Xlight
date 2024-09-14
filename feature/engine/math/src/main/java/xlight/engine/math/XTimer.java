package xlight.engine.math;

import xlight.engine.impl.XTimerImpl;

public interface XTimer {
    static XTimer newInstance() { return new XTimerImpl(true); }
    static XTimer newInstance(boolean paused) { return new XTimerImpl(paused); }

    long getTime();
    void setTime(long milliseconds);
    void addTime(int milliseconds);
    long getSeconds();
    void setPause(boolean toPause);
    boolean isPaused();
    void reset();
    void resume();
    void pause();
}