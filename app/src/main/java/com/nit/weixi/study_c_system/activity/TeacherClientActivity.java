package com.nit.weixi.study_c_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;

/**
 * Created by weixi on 2016/4/14.
 */
public class TeacherClientActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_teacher);
        CardView cvAnswer= (CardView) findViewById(R.id.cv_answer);
        CardView cvTask= (CardView) findViewById(R.id.cv_task);
        cvAnswer.setOnClickListener(this);
        cvTask.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.acty_teacher,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_answer:
                break;
            case R.id.cv_task:
                startActivity(new Intent(this,BuzhiCuotiActivity.class));
                break;
        }
    }
}
