package com.nit.weixi.study_c_system.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.YindaoActivity;

/**
 * Created by weixi on 2016/4/16.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.yindao_login,container,false);
        Button teacherLogin = (Button) view.findViewById(R.id.btn_yindao_teacher);
        Button studentLogin = (Button) view.findViewById(R.id.btn_yindao_student);
        teacherLogin.setOnClickListener(this);
        studentLogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_yindao_teacher:
                SecFragment.tag="teacher";
                YindaoActivity.myHandler.sendEmptyMessage(YindaoActivity.WHAT);
                break;
            case R.id.btn_yindao_student:
                SecFragment.tag="student";
                YindaoActivity.myHandler.sendEmptyMessage(YindaoActivity.WHAT);
                break;
        }
    }
}
