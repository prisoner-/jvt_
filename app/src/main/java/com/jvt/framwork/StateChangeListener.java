package com.jvt.framwork;

public interface StateChangeListener {
    /**
     * @param state 状态
     * @param fps   帧率
     * @param kbps  码率
     */
    abstract public void stateChange(int state, String framRate, String devName);

    abstract public void isPlaying(boolean isPlaying);

    abstract public void isRecord(boolean isRecord);

    abstract public void isAudio(boolean isAudio);

    abstract public void isTalk(boolean isTalk);

    abstract public void showControlBtn(boolean isShow);

    abstract public void isMainStream(int stream);
}
