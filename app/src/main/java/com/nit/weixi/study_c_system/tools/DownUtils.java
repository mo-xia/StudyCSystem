package com.nit.weixi.study_c_system.tools;

import android.content.Context;
import android.os.Environment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DownUtils {

    /**
     * 判断外置sd卡是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是网络url
     *
     * @param link 传入的url
     * @return 如果是网络url则返回true 否则返回false
     */
    public static boolean isURL(String link) {
        return link.startsWith("http") || link.startsWith("https");
    }

    /**
     * 检测文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean exists(String path) {
        File f = new File(path);
        return f.exists();
    }

    /**
     * 读文件
     */
    public static String readFile(String path) {
        return readFile(new File(path));
    }

    /**
     * 读文件
     */
    public static String readFile(File file) {
        try {
            return readFile(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读文件
     */
    public static String readFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }


    /**
     * 下载对应的语言
     *
     * @param lang            模块
     * @param responseHandler 异步下载框架
     *                        String.format("/md-%s/dist/%s.zip", lang.toLowerCase(), lang.toLowerCase()) 文件路径
     */
    public static void downloadLang(String lang, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        String path = String.format("/md-%s/dist/%s.zip", lang.toLowerCase(), lang.toLowerCase());
        params.put("path", path);
        RestClient.get("/" + lang, params, responseHandler);
    }

    /**
     * 获得应用的根目录
     * 如果外部sd卡可写 ，返回的是ExternalStorage路径方便调试 /storage/emulated/0/Android/data/<App Name>/files
     * 否则 ，返回的时InternalStorage /data/data/<App Name>/files
     *
     * @param context 上下文
     * @return 应用的根路径
     */
    public static String getRootPath(Context context) {
        if (isExternalStorageWritable()) {
            return context.getExternalFilesDir(null).getAbsolutePath();
        }
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获得markdown文件根目录
     * <br>
     * ROOT_PATH/md
     *
     * @param context
     * @return
     */
    public static String getMdPath(Context context) {
        String MD_PATH = getRootPath(context) + File.separator + "md";
        File f = new File(MD_PATH);
        if (!f.exists()) {
            f.mkdir();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获得下载路径
     * <br>
     * ROOT_PATH/Download
     *
     * @param context
     * @return
     */
    public static String getDownloadPath(Context context) {
        String DOWNLOAD_PATH = getRootPath(context) + File.separator + "Download";
        File f = new File(DOWNLOAD_PATH);
        if (!f.exists()) {
            f.mkdir();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获得不带后缀名(扩展名)的文件名
     *
     * @return
     */
    public static String getFileNameWithoutExt(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != filename.length() - 1) {
            return filename.substring(0, filename.lastIndexOf("."));
        }
        return filename;

    }

    /**
     * 获得不带序号,后缀名的文件名
     *
     * @param filename
     * @return
     */
    public static String getFileNameWithouExtAndNumber(String filename) {
        filename = getFileNameWithoutExt(filename);
        if (filename.contains("-")) {
            return filename.substring(filename.indexOf("-") + 1, filename.length());
        }
        return filename;
    }

    /**
     * 从ResourceCenter里的文件名提取文章的标题
     *
     * @param filename
     * @return
     */
    public static String getResouceTitle(String filename) {
        if (filename.contains("-")) {
            return getFileNameWithoutExt(filename).split("-")[1];
        }
        return getFileNameWithoutExt(filename);
    }
}
