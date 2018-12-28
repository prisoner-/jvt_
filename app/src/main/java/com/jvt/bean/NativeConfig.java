package com.jvt.bean;

import java.io.Serializable;

public class NativeConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2300987978907601836L;
    public int ckwurao = 0;
    public int ckVioce = 0;
    /**
     * 播放比例 0为全屏播放，1为按比例缩放
     */
    public int scaleMode = 0;

    public NativeConfig() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "NativeConfig [ckwurao=" + ckwurao + ", ckVioce=" + ckVioce
                + ", scaleMode=" + scaleMode + "]";
    }

}
