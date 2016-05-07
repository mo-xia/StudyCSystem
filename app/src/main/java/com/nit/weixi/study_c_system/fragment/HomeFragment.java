package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nit.weixi.study_c_system.activity.BuzhiCuotiActivity;
import com.nit.weixi.study_c_system.activity.DataActivity;
import com.nit.weixi.study_c_system.activity.TaskActivity;
import com.nit.weixi.study_c_system.activity.TestActivity;
import com.nit.weixi.study_c_system.activity.TrainingActivity;
import com.nit.weixi.study_c_system.R;

/**
 * 主页Fragment
 * Created by weixi on 2016/3/30.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private String tag; //当前Fragment的tag

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fg_home,container,false);
        if (getTag()==null){
            tag="";
        }else {
            tag=getTag();
        }
        initLayout(view);
        setData();
        return view;
    }

    private void setData() {
        if (tag.equals("teacher")){
            textView1.setText("开始答疑");
            textView2.setText("布置作业");
            textView3.setText("学生成绩");
            textView4.setText("更新题库");
        }else {
            textView1.setText("章节练习");
            textView2.setText("系统考试");
            textView3.setText("课堂作业");
            textView4.setText("学习资料");
        }
    }

    private void initLayout(View v) {
        textView1 = (TextView) v.findViewById(R.id.tv1);
        textView2 = (TextView) v.findViewById(R.id.tv2);
        textView3 = (TextView) v.findViewById(R.id.tv3);
        textView4 = (TextView) v.findViewById(R.id.tv4);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
    }

    private void startActivityFromHome(Class<?> cls){
        Intent trainingIntent = new Intent(this.getActivity(),cls );
        startActivity(trainingIntent);
    }

    @Override
    public void onClick(View v) {
        Class<?> cls;
        switch (v.getId()){
            case R.id.tv1:
                cls=tag.equals("teacher")?null:TrainingActivity.class;
                startActivityFromHome(cls);
                break;
            case R.id.tv2:
                cls=tag.equals("teacher")?BuzhiCuotiActivity.class:TestActivity.class;
                startActivityFromHome(cls);
                break;
            case R.id.tv3:
                cls=tag.equals("teacher")?null:TaskActivity.class;
                startActivityFromHome(cls);
                break;
            case R.id.tv4:
                cls=tag.equals("teacher")?null:DataActivity.class;
                startActivityFromHome(cls);
                break;
        }
    }
}
