package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * 项目的关于界面
 * Created by weixi on 2016/5/9.
 */
public class AboutActivity extends MyBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = getIntent().getStringExtra("tag");
        Tool.setMyTheme(this,tag); // 根据tag设置主题样式 要在setContentView调用之前调用
        setContentView(R.layout.acty_about);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView appHtml = (TextView) findViewById(R.id.tv_app_html);
        appHtml.setMovementMethod(LinkMovementMethod.getInstance()); //设置html标签
        setBackActionBar("关于", mToolbar); //激活toolBar返回图标
    }
}
