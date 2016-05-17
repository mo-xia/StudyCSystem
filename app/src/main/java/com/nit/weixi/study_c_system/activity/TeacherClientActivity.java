package com.nit.weixi.study_c_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.fragment.HomeFragment;
import com.nit.weixi.study_c_system.tools.TeacherUtils;

/**
 * 老师端
 * Created by weixi on 2016/4/14.
 */
public class TeacherClientActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //根据tag 设置不同的fragment
        TeacherUtils.setTeacherFragment(this,new HomeFragment(),"teacher");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 跳转到关于界面
     * @param item about
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.about){
            Intent intent=new Intent(this,AboutActivity.class);
            intent.putExtra("tag","teacher");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
