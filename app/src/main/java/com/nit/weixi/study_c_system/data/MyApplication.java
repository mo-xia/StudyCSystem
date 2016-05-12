package com.nit.weixi.study_c_system.data;

import android.app.Application;

import com.nit.weixi.study_c_system.tools.RestClient;

/**
 * Created by weixi on 2016/4/29.
 */
public class MyApplication extends Application {

    private boolean isPushUpdate=false;

    public  boolean isPushUpdate() {
        return isPushUpdate;
    }

    public void setPushUpdate(boolean pushUpdate) {
        isPushUpdate = pushUpdate;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RestClient.init(this);
    }

}
