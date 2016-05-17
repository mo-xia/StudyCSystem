package com.nit.weixi.study_c_system.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.data.MyApplication;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.tools.UpdateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends MyBaseActivity {

    private ProgressDialog mProgressDialog; //发现题库 ，点击更新时的进度条
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fmName = MyConstants.FRAGMENT_HOME;
        MyApplication application = (MyApplication) getApplication();
        Tool.setFragment(this, MyConstants.FRAGMENT_HOME);
        if (!application.isPushUpdate()){
            mProgressDialog=new ProgressDialog(this);
            UpdateUtils.findTikuUpdate(this,mProgressDialog);
            application.setPushUpdate(true);
        }
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_HOME;
    }

    @Override
    protected void onDestroy() { //MainActivity 销毁时上传要提问的问题列表
        super.onDestroy();
        String filePath= DownUtils.getRootPath(this);
        final File file=new File(filePath,MyConstants.TIWEN_FILE_NAME);
        final File finishTiwen=new File(filePath,MyConstants.FINISHTIWEN_FILE_NAME);
        if (file.length()==0){
            return;
        }
        final List<String> tiwenList = Tool.getListFromFile(this, MyConstants.TIWEN_FILE_NAME);
        List<String> finishList;
        if (finishTiwen.length()==0){
            finishList=new ArrayList<String>();
        }else {
            finishList = Tool.getListFromFile(this, MyConstants.FINISHTIWEN_FILE_NAME);
        }
        if (tiwenList.equals(finishList)){
            return;
        }
        RequestParams params=new RequestParams();
        params.put("tiwen",tiwenList.toString());
        RestClient.get(MyConstants.TIWEN_URL, params, new AsyncHttpResponseHandler() {
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

    /**
     * 当前activity 为mainActivity且不为主页fragment时，点击back键回到主页
     * @param keyCode
     * @param event
     * @return
     */
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
