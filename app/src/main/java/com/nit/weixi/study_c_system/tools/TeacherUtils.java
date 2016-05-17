package com.nit.weixi.study_c_system.tools;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.fragment.HomeFragment;

/**
 * 设置老师端的fragment
 * Created by weixi on 2016/5/8.
 */
public class TeacherUtils {

    public static void setTeacherFragment(Activity activity,Fragment fragment,String tag){
        activity.setContentView(R.layout.acty_teacher);
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_home_fragment,fragment,tag).commit();
    }

}
