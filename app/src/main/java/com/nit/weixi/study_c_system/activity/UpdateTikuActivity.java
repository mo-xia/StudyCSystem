package com.nit.weixi.study_c_system.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.views.UpdateTikuDialog;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/5/10.
 */
public class UpdateTikuActivity extends MyBackActivity implements View.OnClickListener {

    private static final int INFILE_CODE = 456;
    private TextView selectFile;
    private TextView uploadTiku;
    private String mFilePath;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Toast.makeText(this,"请重新选择要上传的题库文件",Toast.LENGTH_LONG).show();
        } else if (requestCode == INFILE_CODE) {
            mFilePath = Uri.decode(data.getDataString());
            //通过data.getDataString()得到的路径如果包含中文路径，则会出现乱码现象，经过Uri.decode()函数进行解码，得到正确的路径。但是此时路径为Uri路径，必须转换为String路径，网上有很多方法，本人通过对比发现，Uri路径里多了file：//字符串，所以采用以下方法将前边带的字符串截取掉，获得String路径，可能通用性不够好，下一步会学习更好的方法。
            mFilePath = mFilePath.substring(7, mFilePath.length());
            file=new File(mFilePath);
            System.out.println(file.getName());
            selectFile.setText(file.getName());
            selectFile.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_select_file:
                // 打开系统文件浏览功能
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/DB");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,INFILE_CODE);
                break;
            case R.id.tv_upload_tiku:
                if (mFilePath.isEmpty()){
                    Toast.makeText(this,"你还没有选择要上传的文件",Toast.LENGTH_SHORT).show();
                }else {
                    UpdateTikuDialog dialog=new UpdateTikuDialog(this);
                    dialog.setIcon(R.drawable.ic_cloud_upload_light_blue_100_36dp);
                    dialog.setTitle("确认上传最新题库");
                    dialog.setUpdateTikuInterface(new UpdateTikuDialog.UpdateTikuInterface() {
                        @Override
                        public void doUpdate() {
//                            Toast.makeText(UpdateTikuActivity.this,mFilePath,Toast.LENGTH_SHORT).show();
                            RequestParams params=new RequestParams();
                            params.put("updatetag","upload");
                            try {
                                params.put("tiku",file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            RestClient.get("updatetiku", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    //上传成功后刷新界面
                                    selectFile.setText("选择文件");
                                    selectFile.setEnabled(true);
                                    mFilePath="";
                                    Toast.makeText(UpdateTikuActivity.this,"已成功上传新的题库，可以提醒学生更新",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Tool.backOnFailure(UpdateTikuActivity.this,statusCode);
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
