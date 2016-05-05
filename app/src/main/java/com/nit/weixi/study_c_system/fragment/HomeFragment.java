package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nit.weixi.study_c_system.activity.DataActivity;
import com.nit.weixi.study_c_system.activity.TaskActivity;
import com.nit.weixi.study_c_system.activity.TestActivity;
import com.nit.weixi.study_c_system.activity.TrainingActivity;
import com.nit.weixi.study_c_system.R;

/**
 * Created by weixi on 2016/3/30.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    private TextView training;
    private TextView test;
    private TextView task;
    private TextView data;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fg_home,container,false);
        init(view);

        return view;
    }

    private void init(View v) {
        training= (TextView) v.findViewById(R.id.tv_training);
        test= (TextView) v.findViewById(R.id.tv_test);
        task= (TextView) v.findViewById(R.id.tv_task);
        data= (TextView) v.findViewById(R.id.tv_data);
        training.setOnClickListener(this);
        test.setOnClickListener(this);
        task.setOnClickListener(this);
        data.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_training:
                Intent trainingIntent=new Intent(this.getActivity(), TrainingActivity.class);
                startActivity(trainingIntent);
                break;
            case R.id.tv_test:
                Intent testIntent=new Intent(this.getActivity(), TestActivity.class);
                startActivity(testIntent);
                break;
            case R.id.tv_task:
                Intent taskIntent=new Intent(this.getActivity(), TaskActivity.class);
                startActivity(taskIntent);
                break;
            case R.id.tv_data:
                Intent dataIntent=new Intent(this.getActivity(), DataActivity.class);
                startActivity(dataIntent);
                break;
        }
    }
}
