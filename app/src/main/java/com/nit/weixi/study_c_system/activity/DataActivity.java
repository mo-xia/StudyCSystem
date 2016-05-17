package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;

import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * 学习资料模块
 * Created by weixi on 2016/3/30.
 */
public class DataActivity extends MyBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.setFragment(this, MyConstants.FRAGMENT_DATA);
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_DATA;
    }

}
