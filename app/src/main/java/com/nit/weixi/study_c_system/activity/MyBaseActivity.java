package com.nit.weixi.study_c_system.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * Created by weixi on 2016/3/30.
 */
public abstract class MyBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String fmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        init();
    }

    static Toolbar toolbar;

    /**
     * 初始化导航抽屉和工具栏
     */
    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNum= (TextView) headerView.findViewById(R.id.usernumber);
        TextView userName= (TextView) headerView.findViewById(R.id.username);
        ImageView userIcon= (ImageView) headerView.findViewById(R.id.usericon);
        SharedPreferences userSP = getSharedPreferences("user", MODE_PRIVATE);
        userName.setText(userSP.getString("username","Studio"));
        userNum.setText(userSP.getString("usernum","20140920"));
        Bitmap bitmap = Tool.getBitmapFromFile(this, "icon.png");
        userIcon.setImageBitmap(bitmap);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 重写返回按钮的点击事件
     * 当抽屉打开时，按返回键则关闭抽屉
     * 当抽屉关闭时，响应父类的返回点击事件
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 创建toolbar上面的菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent intent=new Intent(this,AboutActivity.class);
            intent.putExtra("tag",getTag());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    abstract String getTag();

    /**
     * 处理抽屉上的点击事件，做响应逻辑后关闭抽屉
     *
     * @param item 响应条目点击事件的菜单
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();//点击的条目的资源id

        if (id == R.id.nav_home) { // 点击的是首页
            // 获取当前活动的名字
            // 如果是MainActivity就切换Fragment
            // 如果不是就销毁当前activity并跳转到MainActivity
            String name = Tool.getRunningActivityName(this);
            if (!name.equals("MainActivity")) {
                //finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Tool.setFragment(this, MyConstants.FRAGMENT_HOME);
            }
            fmName=MyConstants.FRAGMENT_HOME;
        } else if (id == R.id.nav_question) {
            fmName = MyConstants.FRAGMENT_WRONG_QUESTION;
            Tool.setFragment(this, fmName);
        } else if (id == R.id.nav_record) {
            fmName = MyConstants.FRAGMENT_TRACK_RECORD;
            Tool.setFragment(this, fmName);
        } else if (id == R.id.nav_answer) {
            fmName = MyConstants.FRAGMENT_TEACHER_ANSWER;
            Tool.setFragment(this, fmName);
        } else if (id == R.id.nav_night) {
            Toast.makeText(this, "作者把我抛弃了555", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_setting) {
            fmName = MyConstants.FRAGMENT_SETTING_HELP;
            Tool.setFragment(this, fmName);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
