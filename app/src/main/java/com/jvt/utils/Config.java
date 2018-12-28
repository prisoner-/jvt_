package com.jvt.utils;

import android.annotation.SuppressLint;


@SuppressLint("SdCardPath")
public class Config {
    public static final String APPNAME = "JuLong";
    private static final String IMAGE_DIR = "/sdcard/" + APPNAME + "/Snap/";// 图像的目录
    private static final String THUMB_DIR = "/sdcard/" + APPNAME + "/thumb/";// 图像的目录
    private static final String VIDEO_DIR = "/sdcard/" + APPNAME + "/Video/";// 录像的目录
    private static final String USERIMG_DIR = "/sdcard/" + APPNAME + "/User/";// 录像的目录
    public static final String SETTING_PATH = "/sdcard/" + APPNAME
            + "/setting.dat";// 报警设置目录
    public static final String VIDEO_EX = "mp4";
    public static final String IAMGE_EX = "jpeg";
    public static String UserImageDir = "";
    public static String ThumbDir = "";
    public static String UserVideoDir = "";
    public static String userAddDir ="";
    /**
     * 初始化用户截图目录及录像目录
     *
     * @param server
     *            服务器地址
     * @param userName
     *            用户名
     * @param password
     *            密码
     */
    public static void InitUserDir(String server, String userName,
                                   String password) {
        UserImageDir = IMAGE_DIR
                + Utility.ToFileName(server, userName, password) + "/";
        UserVideoDir = VIDEO_DIR
                + Utility.ToFileName(server, userName, password) + "/";
        ThumbDir = THUMB_DIR + Utility.ToFileName(server, userName, password)
                + "/";
        userAddDir = USERIMG_DIR +  Utility.ToFileName(server, userName, password)
                + "/";
    }
}
