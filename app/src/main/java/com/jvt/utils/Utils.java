package com.jvt.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.Player.Core.Constants;
import com.Player.Source.TAlarmSetInfor;
import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.igexin.sdk.PushManager;
import com.jvt.bean.NativeConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;

public class Utils {
    private static final int QR_WIDTH = 400;
    private static final int QR_HEIGHT = 400;

    public static NativeConfig readAlertOption(String path) {
        NativeConfig alertOption = null;
        String readNodeList = Utils.readFile(path);
        if (!TextUtils.isEmpty(readNodeList)) {
            alertOption = JSON.parseObject(readNodeList, NativeConfig.class);
            Log.d("alertOption", "readNodeList size:" + alertOption.toString());
        }
        if (alertOption == null) {
            alertOption = new NativeConfig();
        }
        return alertOption;
    }

    public static boolean writeAlertOption(NativeConfig alertOption, String path) {
        String jsonString = null;
        if (alertOption == null)
            jsonString = "";
        else
            jsonString = JSON.toJSONString(alertOption);
        return writeFile(path, jsonString);
    }

    /**
     * ��ȡimsi
     *
     * @param con
     * @return
     */
    public static String getImsi(Context con) {

        TelephonyManager mTelephonyMgr = (TelephonyManager) con
                .getSystemService(Context.TELEPHONY_SERVICE);
        String secureId = android.provider.Settings.Secure.getString(
                con.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.w("imsi", "secureId:" + secureId);
        return secureId;

    }

    /**
     * ��ȡ�汾��
     *
     * @return
     */
    public static String getVersionName(Context context) {
        String version = "";
        // ��ȡpackagemanager��ʵ��
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "v" + version;
    }

    /***
     * �ı����ɶ�ά��
     *
     * @param text
     * @return Bitmap
     */
    public static Bitmap createImage(String text) {
        try {
            // ��Ҫ����core��
            QRCodeWriter writer = new QRCodeWriter();

            // String text = qr_text.getText().toString();

            Log.i("createImage", "���ɵ��ı���" + text);
            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }

            // ��������ı�תΪ��ά��

            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE,
                    QR_WIDTH, QR_HEIGHT);

            System.out.println("w:" + martix.getWidth() + "h:"
                    + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ʾ�Ƿ�����֪ͨ���ֻ�
     */
    public static boolean showIfNotify(Context context, TAlarmSetInfor alarmInfo) {
        boolean isRun = false;
        if (alarmInfo != null) {
            String token = PushManager.getInstance().getClientid(context);
            if (alarmInfo != null && alarmInfo.notifies != null) {
                for (int i = 0; i < alarmInfo.notifies.length; i++) {
                    Log.w("alarmInfo", alarmInfo.notifies[i].notify_type + "," + alarmInfo.notifies[i].notify_param);
                    if (alarmInfo.notifies[i].notify_type == Constants.NPC_D_MON_ALARM_NOTIFY_TYPE_PHONE_PUSH && token.equals(alarmInfo.notifies[i].notify_param)) {
                        return true;
                    }
                }
            }
        }
        return isRun;
    }

    public static boolean writeFile(String xmlName, String content) {

        File file = new File(xmlName);
        BufferedWriter writer;
        try {
            String dirName = xmlName.substring(0, xmlName.lastIndexOf('/'));
            if (!LocalFile.CreateDirectory(dirName)) {
                return false;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            if (TextUtils.isEmpty(content)) {
                file.delete();
            } else {
                // mFile = new FileOutputStream(file);
                OutputStreamWriter write = new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8");
                writer = new BufferedWriter(write);
                // PrintWriter writer = new PrintWriter(new BufferedWriter(new
                // FileWriter(filePathAndName)));
                // PrintWriter writer = new PrintWriter(new
                // FileWriter(filePathAndName));
                writer.write(content);
                writer.close();
            }
        } catch (Exception e) {

            System.out.println("�����¼����");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readFile(String xmlName) {

        StringBuilder sb = new StringBuilder();
        File file = new File(xmlName);
        BufferedReader reader = null;
        try {
            String dirName = xmlName.substring(0, xmlName.lastIndexOf('/'));
            if (!LocalFile.CreateDirectory(dirName)) {
                return "";
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception e) {

            System.out.println("��ȡ��¼����");
            e.printStackTrace();
        } finally {

        }
        return sb.toString();

    }

}