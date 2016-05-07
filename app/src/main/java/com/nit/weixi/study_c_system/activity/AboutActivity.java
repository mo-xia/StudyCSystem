package com.nit.weixi.study_c_system.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.nit.weixi.study_c_system.R;

/**
 * Created by weixi on 2016/5/9.
 */
public class AboutActivity extends MyBackActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = getIntent().getStringExtra("tag");
        setMyTheme(tag);
        setContentView(R.layout.acty_about);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setBackActionBar("关于",mToolbar);
    }

    private void setMyTheme(String tag){
        switch (tag){
            case "data":
                setTheme(R.style.DataTheme_NoActionBar);
                break;
            case "training":
                setTheme(R.style.TrainingTheme_NoActionBar);
                break;
            case "test":
                setTheme(R.style.TestTheme_NoActionBar);
                break;
            case "task":
                setTheme(R.style.TaskTheme_NoActionBar);
                break;
            default:
                setTheme(R.style.AppTheme_NoActionBar);
        }
    }
}
