package com.jvt.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.Player.web.websocket.ClientCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteLogThread extends Thread {
    static final String TAG = "WriteLogThread";
    static String DIR = "/sdcard/log/";
    int MAXSIZE = 10;// 文件最大数 ，大于等于此，删除第一个文件
    public static boolean isRun = true;
    FileOutputStream outputStream;

    public WriteLogThread(Context context) {
        if (!Utility.isSDCardAvaible()) {
            isRun = false;
        } else {
            isRun = true;
        }
        DIR = "/sdcard/" + context.getPackageName() + "/";
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    @Override
    public void run() {
        try {
            String s;
            String r = System.getProperty("line.separator");// 换行
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
            outputStream = isFileExsitToOutStream(DIR, format.format(new Date()) + ".txt");
            Log.d(TAG, "开启调试模式");
            if (outputStream == null) {
                isRun = false;
            }
            while (isRun) {
                ClientCore clientCore = ClientCore.getInstance();
                if (clientCore != null) {
                    s = clientCore.CLTLogData(1000);
                    if (!TextUtils.isEmpty(s)) {
                        Log.d(TAG, s);
                        s = s + r;
                        outputStream.write(s.getBytes());
                    }
                }
            }
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    FileOutputStream isFileExsitToOutStream(String dir, String fileName) {
        FileOutputStream outFileStream = null;
        try {

            File Configfile = new File(dir + '/' + fileName);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            /**
             *
             */
            File[] listFile = fileDir.listFiles();
            if (listFile != null) {
                if (listFile.length >= MAXSIZE) {
                    File deleteFile = null;
                    for (int i = 0; i < listFile.length; i++) {
                        if (i == 0) {
                            deleteFile = listFile[i];
                        } else {
                            if (deleteFile.lastModified() > listFile[i]
                                    .lastModified()) {
                                deleteFile = listFile[i];
                            }
                        }

                    }
                    if (deleteFile != null) {
                        deleteFile.delete();
                    }
                }
            }

            if (!Configfile.exists()) {

                Configfile.createNewFile();

            }
            outFileStream = new FileOutputStream(Configfile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return outFileStream;
    }
}
