package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.TimuActivity;

/**
 * Created by weixi on 2016/4/8.
 */
public class TestFragment extends Fragment implements View.OnClickListener {

    EditText etTishu;
    EditText etShijian;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_test,container,false);

        etTishu = (EditText) view.findViewById(R.id.et_test_tishu);
        etShijian = (EditText) view.findViewById(R.id.et_test_shijian);
        Button btnStart= (Button) view.findViewById(R.id.btn_test_start);
        btnStart.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        etTishu.setText("");
        etShijian.setText("");
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getActivity(), TimuActivity.class);
        String tag="test";
        String tishu=etTishu.getText().toString();
        String shijian=etShijian.getText().toString();
        intent.putExtra("tag",tag);
        intent.putExtra("tishu",tishu);
        intent.putExtra("shijian",shijian);
        startActivity(intent);
    }
}
