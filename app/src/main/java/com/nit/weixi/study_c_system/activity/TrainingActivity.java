package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * 章节练习 章节列表
 * Created by weixi on 2016/3/30.
 */
public class TrainingActivity  extends MyBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.setFragment(this, MyConstants.FRAGMENT_TRAINING);
    }

    @Override
    String getTag() {
        return MyConstants.FRAGMENT_TRAINING;
    }

}
