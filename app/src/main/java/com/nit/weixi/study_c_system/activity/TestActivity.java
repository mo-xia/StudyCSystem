package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * Created by weixi on 2016/3/30.
 */
public class TestActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.setFragment(this, MyConstants.FRAGMENT_TEST);
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_TEST;
    }

}
