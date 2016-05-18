package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.TimuActivity;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

/**
 * 当前学生的系统考试
 * Created by weixi on 2016/4/8.
 */
public class TestFragment extends Fragment implements View.OnClickListener {

    EditText etTishu; //题数
    EditText etShijian; //要求完成的时间
    int count=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_test, container, false);

        etTishu = (EditText) view.findViewById(R.id.et_test_tishu);
        etShijian = (EditText) view.findViewById(R.id.et_test_shijian);
        Button btnStart = (Button) view.findViewById(R.id.btn_test_start);
        btnStart.setOnClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = Tool.getDataBase(getActivity()).query(MyConstants.TABLE_TIMU_NAME,
                        null, null, null, null, null, null);
                count = cursor.getCount();
            }
        }).start();
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
        Intent intent = new Intent(getActivity(), TimuActivity.class);
        String tag = "test";
        String tishu = etTishu.getText().toString();
        String shijian = etShijian.getText().toString();
        if (tishu.isEmpty() || shijian.isEmpty()) {
            Toast.makeText(getActivity(), "你输入的题数或时间为空，请填好后再提交", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(tishu)>count/2) {
            Toast.makeText(getActivity(),"请输入不要超过"+count/2+"个题目",Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(shijian)>10) {
            Toast.makeText(getActivity(),"最好不要超过10分钟哦",Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("tag", tag);
            intent.putExtra("tishu", tishu);
            intent.putExtra("shijian", shijian);
            startActivity(intent);
        }
    }
}
