package com.nit.weixi.study_c_system.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nit.weixi.study_c_system.pager.LoginFragment;
import com.nit.weixi.study_c_system.pager.SecFragment;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.DBFromAssets;
import com.nit.weixi.study_c_system.tools.MDFilesFromAssets;
import com.nit.weixi.study_c_system.tools.MyConstants;

/**
 * 引导界面
 * Created by weixi on 2016/4/16.
 */
public class YindaoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    ViewPager pager;
    static MyPagerAdapter adapter;
    public static final int WHAT=11;
    public static MyHandler myHandler;

    ImageView[] imageViews;
    ImageView ivLogin;
    ImageView ivUser;

    int curPos; //当前位置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_yindao);
        SecFragment.tag="student";
        myHandler=new MyHandler();
        initLayout();

        new Thread(new Runnable() { //开启一个线程把assets目录下的资源文件存到应用目录下
            @Override
            public void run() {
                //题库
                DBFromAssets.openDatabase(YindaoActivity.this, MyConstants.DB_NAME);
                //开源支持.md文件
                MDFilesFromAssets.writeMDFile2Storage(YindaoActivity.this, MyConstants.OPENSOURCENAME);
                //使用手册.md文件
                MDFilesFromAssets.writeMDFile2Storage(YindaoActivity.this, MyConstants.HELPBOOKNAME);
            }
        }).start();

    }

    private void initLayout() {
        pager= (ViewPager) findViewById(R.id.pager_yindao);
        ivLogin= (ImageView) findViewById(R.id.iv_point_login);
        ivUser= (ImageView) findViewById(R.id.iv_point_user);
        imageViews=new ImageView[]{ivLogin,ivUser};
        ivLogin.setEnabled(false);
        ivUser.setEnabled(true);
        ivLogin.setTag(0);
        ivUser.setTag(1);
        ivLogin.setOnClickListener(this);
        ivUser.setOnClickListener(this);
        curPos=0;
        FragmentManager fm=getSupportFragmentManager();
        adapter=new MyPagerAdapter(fm);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
    }

    /**
     * 设置下方小点点的状态
     * @param position 点击或移动到的位置
     */
    private void setPoint(int position){

        imageViews[curPos].setEnabled(true);
        imageViews[position].setEnabled(false);
        curPos=position; //设置后，把点击的位置赋值给当前位置
    }

    //根据位置创建fragment
    private Fragment createFragment(int position){
        Fragment f;
        if (position==0){
            f=new LoginFragment(); //选择身份
        }else {
            f=new SecFragment(); //具体信息
        }
        return f;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
       setPoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int pos= (int) v.getTag();
        setPoint(pos);
        pager.setCurrentItem(pos);
    }

    /**
     * 通知adapter刷新状态
     */
    public static class MyHandler extends Handler {
         @Override
         public void handleMessage(Message msg) {
             switch (msg.what){
                 case WHAT:
                     adapter.notifyDataSetChanged();
             }
         }
     }


    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return createFragment(position);
        }


        @Override
        public int getItemPosition(Object object) {
            return MyPagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
