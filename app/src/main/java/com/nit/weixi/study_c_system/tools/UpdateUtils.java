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
import com.nit.weixi.study_c_system.activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/5/11.
 */
public class UpdateUtils {

    public static void findTikuUpdate(final Context context,final ProgressDialog mProgressDialog){
        final SharedPreferences sp = context.getSharedPreferences("tikuversion", Context.MODE_PRIVATE);
        final String version = sp.getString("version", "0");
        RequestParams params = new RequestParams();
        params.put("downloadtag","islast");
        params.put("version", version);
        RestClient.get("/downloadtiku", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String lastVersion=new String(responseBody);
                sp.edit().putString("lastversion",lastVersion).apply();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                AlertDialog alertDialog = builder.create();
                alertDialog.setIcon(R.drawable.ic_warning_grey_700_36dp);
                alertDialog.setTitle("请更新题库");
                alertDialog.setMessage("发现有新的题库，如果不更新将会导致各种问题。");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"你取消了更新，请尽快去设置界面下手动更新",Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateUtils.downloadTiku(context,mProgressDialog,sp);
                    }
                });
                alertDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(context,statusCode);
            }
        });
    }

    public static void downloadTiku(final Context context, final ProgressDialog mProgressDialog, final SharedPreferences sp){
        //1.首先下载最新的题库
        File file = new File(DownUtils.getRootPath(context), "updatetiku.db");
        final String version = sp.getString("version", "0");
        RequestParams params = new RequestParams();
        params.put("downloadtag","download");
        params.put("version", version);
        RestClient.get("/downloadtiku", params, new FileAsyncHttpResponseHandler(file) {

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
                mProgressDialog.setMessage(String.format(Locale.CHINA,"大小:%.2f M", 1.0 * totalSize / 1024 / 1024));
                mProgressDialog.setMax((int) totalSize);
                mProgressDialog.setProgress((int) bytesWritten);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Tool.backOnFailure(context,statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                mProgressDialog.dismiss();
                if (file.length()==0){
                    Toast.makeText(context,"你的题库已经是最新版本",Toast.LENGTH_LONG).show();
                }else {
                    //2.然后把最新题库替换原先的题库
                    File dbFile=context.getDatabasePath(MyConstants.dbName);
                    try {
                        FileInputStream fis=new FileInputStream(file);
                        FileOutputStream fos=new FileOutputStream(dbFile);
                        int count=-1;
                        byte[] buffer=new byte[1024];
                        while ((count=fis.read(buffer))!=-1){
                            fos.write(buffer,0,count);
                            fos.flush();
                        }

                        fos.close();
                        fis.close();
                        //3.删除本地题库相关数据更新版本号
                        Tool.deleteFile(context,"cuoti.txt");
                        Tool.deleteFile(context,"tiwen.txt");
                        Tool.deleteFile(context,"zhengque.txt");
                        Tool.deleteFile(context,"finishtiwen.txt");
                        sp.edit().putString("version",sp.getString("lastversion",version)).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        file.delete();
    }
}
