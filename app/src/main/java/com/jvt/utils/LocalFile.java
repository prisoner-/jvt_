package com.jvt.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 本地文件的操作，用于本地回放
 * */
public class LocalFile {
    private static List<File> files = null;

    public static Boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * 获取文件夹内的有着后缀为ext标识的文件
     *
     * @param dir
     *            目标文件夹
     * @param ext
     *            后缀，多个后缀以#分割开
     * @return 获取成功返回列表，失败返回空
     */
    public static List<String> GetMatchFile(String dir, String ext) {
        List<String> list = new ArrayList<String>();
        String[] a = ext.split("#");
        File[] allFiles = null;
        File file = null;
        try {
            file = new File(dir);
            allFiles = file.listFiles();
            if (file.isDirectory()) {
                for (int k = 0; k < allFiles.length; k++) {
                    if (!allFiles[k].isDirectory()) {
                        for (int i = 0; i < a.length; i++) {
                            if (allFiles[k].getName().toLowerCase()
                                    .endsWith(a[i].toLowerCase())) {
                                list.add(allFiles[k].getPath());
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            System.out.println("获取文件内列表出错：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 创建文件夹，如果存在或者创建成功，则为true,否则为false
     *
     * @param dir
     * @return
     */
    public static boolean CreateDirectory(String dir) {
        File file = new File(dir);
        boolean b = false;
        if (!file.exists()) {
            b = file.mkdirs();
            System.out.println("不存在，创建文件夹" + b + dir);
            return b;
        } else {
            System.out.println("存在文件夹" + dir);
            return true;
        }
    }

    public static void SaveImage(Bitmap bmp, String name, String dir) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(dir + name);
            bmp.compress(CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 得到目录dir下所有后缀名为ext的文件列表
     *
     * @param dir
     * @param ext
     */
    private static void GetFiles(String dir) {
        File file = new File(dir);
        File[] fs = file.listFiles();
        if (fs == null || fs.length == 0)
            return;
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isFile()
                    && (fs[i].getName().endsWith(Config.IAMGE_EX.toLowerCase()) || fs[i]
                    .getName().endsWith(Config.VIDEO_EX.toLowerCase()))) {
                files.add(fs[i]);
                System.out.println("找到文件：" + fs[i].getPath());
            } else if (fs[i].isDirectory()) {
                GetFiles(fs[i].getPath());
            }
        }
    }

    /**
     * 得到目录dir下所有后缀名为ext的文件列表
     *
     * @param dir
     *            目录
     *            后缀名
     * @return 文件列表
     */
    public static List<File> GetMatchExtFiles(String dir) {
        if (files == null) {
            files = new ArrayList<File>();
        } else {
            files.clear();
        }
        GetFiles(dir);
        return files;
    }

    /**
     * 得到目录dir下所有后缀名为ext的文件列表
     *
     * @param dir
     *            目录
     * @param ext
     *            后缀名
     * @return 文件列表
     */
    public static List<File> GetMatchExtFiles(String dir, String ext) {
        if (files == null) {
            files = new ArrayList<File>();
        } else {
            files.clear();
        }
        GetFiles(dir, ext);
        //Collections.sort(files, new SortByTime());
        return files;
    }

    /**
     * 得到目录dir下所有后缀名为ext的文件列表
     *
     * @param dir
     * @param ext
     */
    private static void GetFiles(String dir, String ext) {
        File file = new File(dir);
        File[] fs = file.listFiles();
        if (fs == null || fs.length == 0)
            return;

        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isFile() && fs[i].getName().endsWith(ext.toLowerCase())) {
                files.add(fs[i]);
                System.out.println("找到文件：" + fs[i].getPath() + "时间："
                        + fs[i].lastModified());
            } else if (fs[i].isDirectory()) {
                GetFiles(fs[i].getPath(), ext);
            }
        }
    }

    public static File[] SortedByCreateTime(File[] files) {
        for (int i = 0; i < files.length; i++) {

            for (int j = 0; j < files.length; j++) {
                Long t1 = files[i].lastModified();
                Long t2 = files[j].lastModified();
                if (Compare(t1, t2))// 假如i点的小于j点，就交换值
                {
                    // files.set(i, files[i]);
                    File t = files[j];
                    files[j] = files[i];
                    // files.set(j, files.get(i));
                    files[i] = t;
                }
            }

        }
        return files;

    }

    private static List<File> SortedByCreateTime(List<File> files) {

//		for (int i = 0; i < files.size(); i++) {
//			for (int k = i + 1; k < files.size(); k++) {
//
//				Long s1 = files.get(i).lastModified();
//				Long s2 = files.get(k).lastModified();
//				if (Compare(s1, s2))// 假如i点的小于k点，就交换值
//				{
//					File t = files.get(k);
//					files.set(k, files.get(i));
//					files.set(i, t);
//				}
//			}
//		}
        return files;
    }

    /**
     * 检查s1是否小于s2，如果是，返回True，否则False
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean Compare(long s1, long s2) {

        return s1 < s2;
    }

    /**
     * 检查s1是否小于s2，如果是，返回True，否则False
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean Compare(String s1, String s2) {
        String v1 = s1.substring(s1.lastIndexOf('/') + 1, s1.lastIndexOf('.'));
        String v2 = s2.substring(s2.lastIndexOf('/') + 1, s2.lastIndexOf('.'));
        return GetSecond(v1) < GetSecond(v2);
    }

    public static double GetSecond(String time) {
        double year = Double.parseDouble(time.substring(0, 4)) * 12 * 30 * 24
                * 60 * 60;
        double month = Double.parseDouble(time.substring(4, 6)) * 30 * 24 * 60
                * 60;
        double day = Double.parseDouble(time.substring(6, 8)) * 24 * 60 * 60;
        double hour = Double.parseDouble(time.substring(8, 10)) * 60 * 60;
        double minute = Double.parseDouble(time.substring(10, 12)) * 60;
        double second = Double.parseDouble(time.substring(12, 14));
        return year + month + day + hour + minute + second;
    }

}
