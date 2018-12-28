package com.jvt.wifi;

import android.os.Handler;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevWifiInfor;

public class WiFiSetThread extends Thread {
    public static final int SET_OK = 0;
    public static final int SET_FALL = 1;
    PlayerClient pc;
    String deviceId;
    TDevWifiInfor devWifiInfo;
    Handler handler;

    public WiFiSetThread(PlayerClient pc, String deviceId,
                         TDevWifiInfor devWifiInfo, Handler handler) {
        this.pc = pc;
        this.deviceId = deviceId;
        this.devWifiInfo = devWifiInfo;
        this.handler = handler;
    }

    @Override
    public void run() {
        Log.d("setWifiInfo", "设备ID：" + deviceId + ",wifi SSID:"
                + devWifiInfo.sWifiSSID + ",bDhcpEnable:"
                + devWifiInfo.bDhcpEnable);
        int ret = pc.CameraSetWIFIConfig(deviceId, devWifiInfo);
        if (ret > 0) {
            handler.sendEmptyMessage(SET_OK);
        } else
            handler.sendEmptyMessage(SET_FALL);

    }
}
