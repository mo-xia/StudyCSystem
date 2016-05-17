package com.nit.weixi.study_c_system.activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎页
 * Created by weixi on 2016/4/15.
 */

public class WelcomeActivity extends AppCompatActivity {

    /**
     * 默认身份
     */
    static final String DEFSHENFEN="weizhi";

    /**
     * 老师
     */
    static final String TEACHERSHENFEN="teacher";

    /**
     * 学生
     */
    static final String STUDENTSHENFEN="student";
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_welcome);

        sp=getSharedPreferences(MyConstants.LOGIN_SP,MODE_PRIVATE);

        Timer timer=new Timer(); //开启一个计时任务
        TimerTask task=new TimerTask() {
            @Override
            public void run() {

                if (sp.getString(MyConstants.LOGIN_SP_SHENFEN,"weizhi").equals(DEFSHENFEN)){
                    // 第一次进入，程序从引导程序开始
                    startActivity(new Intent(WelcomeActivity.this,YindaoActivity.class));
                }else if (sp.getString(MyConstants.LOGIN_SP_SHENFEN,"weizhi").equals(TEACHERSHENFEN)){
                    // 非首次进入 跳过引导页 老师则进入老师端
                    startActivity(new Intent(WelcomeActivity.this,TeacherClientActivity.class));
                }else {
                    //否则进入学生端
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                }
                finish();
            }
        };
        timer.schedule(task,3000); //三秒后执行task
    }
}
