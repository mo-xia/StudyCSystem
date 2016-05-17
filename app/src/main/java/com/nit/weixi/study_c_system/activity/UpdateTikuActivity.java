package com.nit.weixi.study_c_system.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.tools.UpdateUtils;
import com.nit.weixi.study_c_system.views.UpdateTikuDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * 更新题库模块
 * Created by weixi on 2016/5/10.
 */
public class UpdateTikuActivity extends MyBackActivity implements View.OnClickListener {

    private final int INFILE_CODE = 456;
    private TextView selectFile; //选择文件
    private TextView uploadTiku; //更新
    private String mFilePath;
    private File file;
    private Context context;
    private ProgressDialog mProgressDialog; //上传进度条

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.acty_updatetiku);
        selectFile= (TextView) findViewById(R.id.tv_select_file);
        uploadTiku= (TextView) findViewById(R.id.tv_upload_tiku);
        selectFile.setOnClickListener(this);
        uploadTiku.setOnClickListener(this);
        mFilePath="";
        setBackActionBar("更新题库",null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(context,"请重新选择要上传的题库文件",Toast.LENGTH_LONG).show();
        } else if (requestCode == INFILE_CODE) {
            //解决中文路径乱码，暂未发现
            mFilePath = Uri.decode(data.getDataString());
            mFilePath = mFilePath.substring(7, mFilePath.length());

            file=new File(mFilePath);
            selectFile.setText(file.getName()); //把文件名设置给选择文件文本框
            selectFile.setEnabled(false); //选择成功后不能再重新选择
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_select_file:
                // 打开系统文件浏览器
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,INFILE_CODE);
                break;
            case R.id.tv_upload_tiku:
                if (mFilePath.isEmpty()){
                    Toast.makeText(context,"你还没有选择要上传的文件",Toast.LENGTH_SHORT).show();
                }else {
                    UpdateTikuDialog dialog=new UpdateTikuDialog(context);
                    dialog.setIcon(R.drawable.ic_cloud_upload_light_blue_100_36dp);
                    dialog.setTitle("确认上传最新题库");
                    dialog.setUpdateTikuInterface(new UpdateTikuDialog.UpdateTikuInterface() {
                        @Override
                        public void doUpdate() {
                            //获得当前题库版本 +1后上传
                            SharedPreferences sp = context.getSharedPreferences(MyConstants.TIKU_SP, MODE_PRIVATE);
                            int verInt=Integer.parseInt(sp.getString(MyConstants.TIKU_SP_VERSION, "0"));
                            String version = verInt+1+"";
                            RequestParams params=new RequestParams();
                            params.put("version",version);
                            try {
                                params.put("tiku",file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            RestClient.post(MyConstants.UPDATE_URL_UPLOAD, params, new AsyncHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    mProgressDialog = new ProgressDialog(context);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mProgressDialog.setTitle("正在上传..");
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
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    //上传成功后刷新界面 并更新自己的题库
                                    mProgressDialog.dismiss();
                                    selectFile.setText("选择文件");
                                    selectFile.setEnabled(true);
                                    mFilePath="";
                                    UpdateUtils.replaceDBFile(file,context); //更新老师自己的题库文件
                                    Toast.makeText(context,"已成功上传新的题库，可以提醒学生更新",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Tool.backOnFailure(context,statusCode);
                                }
                            });
                        }
                    });
                    dialog.show();
                }
                break;
        }
    }
}
