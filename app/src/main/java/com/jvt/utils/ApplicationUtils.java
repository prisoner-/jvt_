package com.jvt.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApplicationUtils {
    public static boolean netOK = true;

    // 监测网络
    public static boolean netState(Context context) {
        // 获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state = connManager.getActiveNetworkInfo();
        if (state != null) {
            netOK= state.isAvailable();

        } else {
            netOK = false;
        }
        // // 获取WIFI网络连接状态
        // State stateWifi = connManager.getNetworkInfo(
        // ConnectivityManager.TYPE_WIFI).getState();
        // // 获取GPRS网络连接状态
        // State stateGprs = connManager.getNetworkInfo(
        // ConnectivityManager.TYPE_MOBILE).getState();
        // // 判断是否正在使用WIFI网络
        // if ((State.CONNECTED == stateWifi) || (State.CONNECTED == stateGprs))
        // {
        // netOK = true;
        // } else {
        // netOK = false;
        // }

        return netOK;
    }
}
