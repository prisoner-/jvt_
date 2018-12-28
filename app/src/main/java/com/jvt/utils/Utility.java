package com.jvt.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.Player.Source.TDateTime;
import com.Player.Source.TLoginParam;
import com.igexin.sdk.PushManager;
import com.jvt.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {
    final static String DEMO_THUMB_PATH = "/sdcard/NewUMEye/demo/";

    public static TLoginParam initeLogin(Context con) {
        TLoginParam tmpLoginParam = new TLoginParam();
        tmpLoginParam.iClientType = 3;
        tmpLoginParam.sDevModel = "Android";
        tmpLoginParam.sDevVersion = "v1.0.1";
        tmpLoginParam.sClientOwner = Const.CustomName;
        tmpLoginParam.sClientCustomFlag = Const.CustomName;
        tmpLoginParam.sClientLanguage = Utility.isZh(con) == 2 ? "SimpChinese"
                : "English";// English, SimpChinese
        String clientId = PushManager.getInstance().getClientid(con);
        Log.d("clientId", "getClientid:" + clientId);
        tmpLoginParam.sClientToke = clientId;
        return tmpLoginParam;
    }

    public static String[] languages = {"en", "zh", "es", "pt", "fr", "de", "it", "pl", "el"};

    public static int isZh(Context con) {
        Locale locale = con.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if ("en".equals(language))
            return 1;
        else if ("zh".equals(language)) {
            if ("TW".equals(locale.getCountry())) {
                return 3;
            } else {
                return 2;
            }
        } else if ("de".equals(language))
            return 4;
        else if ("fr".equals(language)) {
            return 5;
        } else if ("pt".equals(language)) {
            return 6;
        } else if ("ru".equals(language)) {
            return 7;
        } else if ("it".equals(language)) {
            return 8;
        } else if ("tr".equals(language)) {
            return 9;
        } else if ("pl".equals(language)) {
            return 10;
        } else if ("ar".equals(language)) {
            return 11;
        }
        return 1;
    }

    public static void Logout(Activity c)// 注销行为
    {
        // Intent intent=new Intent(c,Settings.class);
        // intent.putExtra("logout", true);
        // c.startActivity(intent);
        c.finish();
    }

    private static class OnDialogBack implements
            android.content.DialogInterface.OnClickListener// 确认对话框的确定按钮事件
    {
        private Activity context;

        public OnDialogBack(Activity c) {
            context = c;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Utility.Logout(context);
        }
    }

    public static void ShowConfirmDialog(Activity c)// 显示确认对话框
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.exit);
        builder.setMessage(R.string.exit_info);
        builder.setPositiveButton(R.string.positive, new OnDialogBack(c));
        builder.setNegativeButton(
                R.string.negative, null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public static String FormateTime(int time)// hh:mm:ss,time是毫秒级的时间
    {
        int hour = time / (1000 * 60 * 60);
        int minute = (time % (1000 * 60 * 60)) / (1000 * 60);
        int second = ((time % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        String h, m, s;
        if (hour < 10) {
            h = "0" + hour;
        } else {
            h = String.valueOf(hour);
        }
        if (minute < 10) {
            m = "0" + minute;
        } else {
            m = String.valueOf(minute);
        }
        if (second < 10) {
            s = "0" + second;
        } else {
            s = String.valueOf(second);
        }
        String str = h + ":" + m + ":" + s;

        return str;
    }

    /**
     * 检查输入的IP地址是否合法
     *
     * @param ip
     * @return
     */
    public static boolean isValidIP(String ip) {
        ip = ip.replace('.', '#');
        String[] numbers = ip.split("#");
        if (numbers.length != 4)
            return false;
        for (int i = 0; i < numbers.length; i++) {
            int number;
            try {
                number = Integer.parseInt(numbers[i]);
            } catch (Exception e) {
                return false;
            }
            if ((number < 0) || (number > 255))
                return false;
        }
        return true;
    }

    /**
     * 检查是否有效数字
     *
     * @param n
     * @return
     */
    public static boolean isValidNumber(String n) {

        try {
            Integer.parseInt(n);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 得到中文的当前时间，精确到秒
     *
     * @return
     */
    public static String GetCurrentTime() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String s = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1)
                + "月" + c.get(Calendar.DAY_OF_MONTH) + "日"
                + c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE)
                + "分" + c.get(Calendar.SECOND) + "秒";
        return s;
    }

    /**
     * 得到中文的当前时间，精确到秒
     *
     * @return
     */
    public static TDateTime GetCurrentTime1() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        TDateTime tPhoneTime = new TDateTime();
        tPhoneTime.iYear = c.get(Calendar.YEAR);
        tPhoneTime.iMonth = (c.get(Calendar.MONTH) + 1);
        tPhoneTime.iDay = c.get(Calendar.DAY_OF_MONTH);
        tPhoneTime.iHour = c.get(Calendar.HOUR_OF_DAY);
        tPhoneTime.iMinute = c.get(Calendar.MINUTE);
        tPhoneTime.iSecond = c.get(Calendar.SECOND);
        return tPhoneTime;
    }

    /**
     * 获取图片的小缩略图
     *
     * @param fileName 文件名字
     * @return
     */
    public static Bitmap GetThumbImage(String fileName, int w, int h) {
        Bitmap result = null;
        try {
            BitmapFactory.Options op = new Options();
            Bitmap bmp = BitmapFactory.decodeFile(fileName, op);
            int x = (int) (op.outWidth / (w * 1.0));
            int y = (int) (op.outHeight / (h * 1.0));
            int scale = x <= y ? x : y;
            bmp.recycle();
            op.inSampleSize = scale;
            result = BitmapFactory.decodeFile(fileName, op);
            // System.out.println("scale:"+scale);
        } catch (RuntimeException e) {
            System.out.println("RuntimeException获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static Bitmap GetThumbImage(String fileName) {
        Bitmap result = null;
        try {

            int scale = 3;

            BitmapFactory.Options options = new Options();
            options.inSampleSize = scale;
            result = BitmapFactory.decodeFile(fileName, options);
            // System.out.println("scale:"+scale);
        } catch (RuntimeException e) {
            System.out.println("RuntimeException获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static Bitmap GetThumbImage(String fileName, int maxNumOfPixel) {
        Bitmap result = null;
        try {
            BitmapFactory.Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
            options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixel);
            options.inJustDecodeBounds = false;

            result = BitmapFactory.decodeFile(fileName, options);
            // System.out.println("scale:"+scale);
        } catch (RuntimeException e) {
            System.out.println("RuntimeException获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    public static Bitmap getBitmap(String fileName) {
        Bitmap bmp = null;
        try {

            bmp = BitmapFactory.decodeFile(fileName);

        } catch (RuntimeException e) {
            System.out.println("RuntimeException获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("获取缩略图出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return bmp;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 同过网络读取图片
     *
     * @param imageUrl
     * @return
     */
    public static Bitmap loadImageFromNetwork(String imageUrl, int maxNumOfPixel) {
        Bitmap tmpBitmap = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            if (!Utility.isSDCardAvaible()) {

                return null;
            }
            File dirFile = new File(DEMO_THUMB_PATH);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File[] files = dirFile.listFiles();
            boolean isExsit = false;
            String picPath = "";
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(fileName)) {
                    isExsit = true;
                    picPath = files[i].getPath();
                    break;
                }
            }
            if (isExsit) {
                tmpBitmap = GetThumbImage(picPath, maxNumOfPixel);
            } else {
                InputStream is = new java.net.URL(imageUrl).openStream();
                tmpBitmap = BitmapFactory.decodeStream(is);
                is.close();
                File myCaptureFile = getFilePath(DEMO_THUMB_PATH, fileName);
                FileOutputStream out = new FileOutputStream(myCaptureFile);
                tmpBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                out.flush();
                out.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpBitmap;
    }

    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 将值写入至配置文件
     *
     * @param c
     * @param fileName
     * @param key
     * @param value
     */
    public static void WriteLocal(Context c, String fileName, String key,
                                  String value) {
        SharedPreferences pref = c.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 读取相应的值
     *
     * @param c
     * @param fileName
     * @param key
     * @return
     */
    public static String ReadLocal(Context c, String fileName, String key) {
        SharedPreferences pref = c.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    /**
     * 删除相应的键
     *
     * @param c
     * @param fileName
     * @param key
     */
    public static void RemoveLocal(Context c, String fileName, String key) {
        SharedPreferences pref = c.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 将用户名与密码及服务器名生成一个独一无二的文件名字
     *
     * @param server   服务器名
     * @param userName 用户名
     * @param password 密码
     * @return 生成文件名
     */
    public static String ToFileName(String server, String userName,
                                    String password) {
        String ser = server.replace(":", "").replace(".", "").replace("//", "");// 将.与:去掉
        // return ser+userName+password;
        return ser + userName;
    }

    /**
     * 内存卡是否可以使用
     *
     * @return 可以用为true, 不可以用为false
     */
    public static boolean isSDCardAvaible() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 得到版本的VersionCode
     *
     * @param c
     * @return
     */
    public static int GetVersionCode(Context c) {
        String pName = c.getPackageName();
        try {
            PackageInfo pinfo = c.getPackageManager().getPackageInfo(pName,
                    PackageManager.GET_CONFIGURATIONS);
            return pinfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void OpenAPKFile(String fileName, Context context) {
        File file = new File(fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static String getJasonString(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setRequestProperty("Accept", "application/json");
            InputStream is = conn.getInputStream();

            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),
                    1024);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            try {
                is.close();
            } catch (IOException e) {
            }

            String response = sb.toString();

            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 日期 ,月份判断 是否删除流量数据
     *
     * @param context
     */
    public static void isDayOrMonthFirstStart(Context context) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        Log.i("data", "日期：" + day + ",月份：" + month);
        int sday = SharedPrefsUtil.getValue(context, SharedPrefsUtil.DAY, 0);
        int smonth = SharedPrefsUtil
                .getValue(context, SharedPrefsUtil.MONTH, 0);
        if (day != sday) {
            SharedPrefsUtil.putValue(context, SharedPrefsUtil.DAY_DATA, 0);

        }
        if (month != smonth) {
            SharedPrefsUtil.putValue(context, SharedPrefsUtil.DAY_DATA, 0);
            SharedPrefsUtil.putValue(context, SharedPrefsUtil.MONTH_DATA, 0);

        }
    }

    /**
     * 保存数据流量,同时保存时间
     *
     * @param con
     * @param count
     */
    public static void saveData(Context con, long count) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        long dday = SharedPrefsUtil.getLongValue(con, SharedPrefsUtil.DAY_DATA,
                0);
        long dmonth = SharedPrefsUtil.getLongValue(con,
                SharedPrefsUtil.MONTH_DATA, 0);
        long dhistory = SharedPrefsUtil.getLongValue(con,
                SharedPrefsUtil.HISTORY_DATA, 0);
        SharedPrefsUtil.putValue(con, SharedPrefsUtil.DAY, day);
        SharedPrefsUtil.putValue(con, SharedPrefsUtil.MONTH, month);
        SharedPrefsUtil.putValue(con, SharedPrefsUtil.DAY_DATA, count + dday);
        SharedPrefsUtil.putValue(con, SharedPrefsUtil.MONTH_DATA, count
                + dmonth);
        SharedPrefsUtil.putValue(con, SharedPrefsUtil.HISTORY_DATA, count
                + dhistory);
    }
}
