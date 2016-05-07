package com.nit.weixi.study_c_system.data;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nit.weixi.study_c_system.activity.WelcomeActivity;
import com.nit.weixi.study_c_system.tools.DBFromAssets;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;

/**
 * Created by weixi on 2016/4/29.
 */
public class MyApplication extends Application {

    static String studentName;
    static String studentNum;
    static Bitmap studentIcon;

    public static Bitmap getStudentIcon() {
        return studentIcon;
    }

    public static void setStudentIcon(Bitmap studentIcon) {
        MyApplication.studentIcon = studentIcon;
    }

    public static String getStudentName(){
        return studentName;
    }

    public static String getStudentNum(){
        return studentNum;
    }

    public static void setStudentName(String studentName) {
        MyApplication.studentName = studentName;
    }

    public static void setStudentNum(String studentNum) {
        MyApplication.studentNum = studentNum;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RestClient.init(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBFromAssets.openDatabase(MyApplication.this, MyConstants.dbName);
            }
        }).start();
    }

}
