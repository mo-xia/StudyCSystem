package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * 课堂作业activity
 * Created by weixi on 2016/3/30.
 */
public class TaskActivity extends MyBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.setFragment(this, MyConstants.FRAGMENT_TASK);
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_TASK;
    }

}
