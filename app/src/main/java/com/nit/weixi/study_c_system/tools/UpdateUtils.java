package com.nit.weixi.study_c_system.tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * 更新题库工具类
 * Created by weixi on 2016/5/11.
 */
public class UpdateUtils {

    public static final String DBFILENAME ="updatetiku.db"; //下载下来的文件名字

    /**
     * 请求服务器看是否有更新
     * @param context 上下文
     * @param mProgressDialog 进度条 用于显示更新的进度
     */
    public static void findTikuUpdate(final Context context, final ProgressDialog mProgressDialog) {
        final SharedPreferences sp = context.getSharedPreferences(MyConstants.TIKU_SP, Context.MODE_PRIVATE);
        String version = sp.getString(MyConstants.TIKU_SP_VERSION, "0");
        RequestParams params = new RequestParams();
        params.put("downloadtag", "islast");
        params.put("version", version);
        RestClient.get(MyConstants.UPDATE_URL_DOWNLOAD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String lastVersion = new String(responseBody);
                sp.edit().putString(MyConstants.TIKU_SP_LASTVERSION, lastVersion).apply();
                if (lastVersion.length() != 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setIcon(R.drawable.ic_warning_grey_700_36dp);
                    alertDialog.setTitle("请更新题库");
                    alertDialog.setMessage("发现有新的题库，如果不更新将会导致各种问题。");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "你取消了更新，请尽快去设置界面下手动更新", Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UpdateUtils.downloadTiku(context, mProgressDialog, sp);
                        }
                    });

                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(context, statusCode);
            }
        });
    }

    /**
     * 下载更新题库
     * @param context 上下文
     * @param mProgressDialog 进度条
     * @param sp 题库版本的sp
     */
    public static void downloadTiku(final Context context, final ProgressDialog mProgressDialog, final SharedPreferences sp) {
        //1.首先下载最新的题库
        File file = new File(DownUtils.getRootPath(context), DBFILENAME);
        final String version = sp.getString(MyConstants.TIKU_SP_VERSION, "0");
        RequestParams params = new RequestParams();
        params.put("downloadtag", "download");
        params.put("version", version);
        RestClient.get(MyConstants.UPDATE_URL_DOWNLOAD, params, new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onStart() {
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setTitle("正在下载..");
                mProgressDialog.setProgress(0);
                mProgressDialog.setMax(100);
                mProgressDialog.show();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                mProgressDialog.setMessage(String.format(Locale.CHINA, "大小:%.2f M", 1.0 * totalSize / 1024 / 1024));
                mProgressDialog.setMax((int) totalSize);
                mProgressDialog.setProgress((int) bytesWritten);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Tool.backOnFailure(context, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                mProgressDialog.dismiss();
                if (file.length() == 0) {
                    Toast.makeText(context, "你的题库已经是最新版本", Toast.LENGTH_LONG).show();
                } else {
                    //2.然后把最新题库替换原先的题库
                    replaceDBFile(file, context);
                    //3.删除本地题库相关数据更新版本号
                    Tool.deleteFile(context, MyConstants.CUOTI_FILE_NAME);
                    Tool.deleteFile(context, MyConstants.TIWEN_FILE_NAME);
                    Tool.deleteFile(context, MyConstants.ZHENGQUE_FILE_NAME);
                    Tool.deleteFile(context, MyConstants.FINISHTIWEN_FILE_NAME);
                    sp.edit().putString(MyConstants.TIKU_SP_VERSION, sp.getString(MyConstants.TIKU_SP_LASTVERSION, version)).apply();
                }
            }
        });
        file.delete(); //删除下载的文件
    }

    /**
     * 用新的题库文件替换原先的题库
     * @param file 新的题库文件
     * @param context 上下文
     */
    public static void replaceDBFile(File file, Context context) {
        File dbFile = context.getDatabasePath(MyConstants.DB_NAME);
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(dbFile);
            int count = -1;
            byte[] buffer = new byte[1024];
            while ((count = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
                fos.flush();
            }
            fos.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
