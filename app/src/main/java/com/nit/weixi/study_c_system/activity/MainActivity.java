package com.nit.weixi.study_c_system.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.data.MyApplication;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.tools.UpdateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends MyBaseActivity {

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fmName = MyConstants.FRAGMENT_HOME;
        MyApplication application = (MyApplication) getApplication();
        if (!application.isPushUpdate()){
            mProgressDialog=new ProgressDialog(this);
            UpdateUtils.findTikuUpdate(this,mProgressDialog);
            application.setPushUpdate(true);
        }
        Tool.setFragment(this, MyConstants.FRAGMENT_HOME);
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_HOME;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String filePath= DownUtils.getRootPath(this);
        final File file=new File(filePath,"tiwen.txt");
        final File finishTiwen=new File(filePath,"finishtiwen.txt");
        if (file.length()==0){
            return;
        }
        final List<String> tiwenList = Tool.getListFromFile(this, "tiwen.txt");
        List<String> finishList;
        if (finishTiwen.length()==0){
            finishList=new ArrayList<String>();
        }else {
            finishList = Tool.getListFromFile(this, "finishtiwen.txt");
        }
        if (tiwenList.equals(finishList)){
            return;
        }
        RequestParams params=new RequestParams();
        params.put("tiwen",tiwenList.toString());
        RestClient.get("/tiwen", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    FileOutputStream fos=new FileOutputStream(finishTiwen);
                    fos.write(DownUtils.readFile(file).getBytes());
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                 Tool.backOnFailure(MainActivity.this,statusCode);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&!fmName.equals("home")) {
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
            return super.onKeyDown(keyCode, event);
    }

}
