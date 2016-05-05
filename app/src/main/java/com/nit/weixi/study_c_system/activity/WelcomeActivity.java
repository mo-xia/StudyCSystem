package com.nit.weixi.study_c_system.activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.DBFromAssets;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by weixi on 2016/4/15.
 */

public class WelcomeActivity extends AppCompatActivity {

    static final String DEFSHENFEN="weizhi";
    static final String TEACHERSHENFEN="teacher";
    static final String STUDENTSHENFEN="student";
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_welcome);

        sp=getSharedPreferences(MyConstants.SPNAME,MODE_PRIVATE);

        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {

                if (sp.getString("shenfen","weizhi").equals(DEFSHENFEN)){
                    startActivity(new Intent(WelcomeActivity.this,YindaoActivity.class));
                    finish();
                }else if (sp.getString("shenfen","weizhi").equals(TEACHERSHENFEN)){
                    startActivity(new Intent(WelcomeActivity.this,TeacherClientActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();
                }
            }
        };
        timer.schedule(task,3000); //三秒后执行task
    }
}
