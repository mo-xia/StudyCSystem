package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.TimuActivity;
import com.nit.weixi.study_c_system.data.ZuoYeBean;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/4/28.
 */
public class TaskFragment extends Fragment implements View.OnClickListener {

    TextView tvCurDate;  //课堂作业的时刻
    TextView tvTime;   //课堂作业要求多久完成的时间
    TextView tvNumber;   //题目数量
    TextView tvGo;  //跳转到做题详情
    ZuoYeBean zuoYeBean=null;

    int CODETIMU =1000;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_task, container, false);
        tvCurDate = (TextView) view.findViewById(R.id.tv_date_task_);
        tvTime = (TextView) view.findViewById(R.id.tv_time_task);
        tvNumber = (TextView) view.findViewById(R.id.tv_number_task);
        tvGo = (TextView) view.findViewById(R.id.tv_go_task);
        tvGo.setOnClickListener(this);
        getJsonFile();
        return view;
    }

    private void getJsonFile() {
        String rootPath = DownUtils.getRootPath(getActivity());
        final String zuoyePath = rootPath + "/zuoye";
        if (!new File(zuoyePath).exists()) {
            if (new File(zuoyePath).mkdir()) {
                System.out.println("本地zuoye文件夹创建成功");
            } else {
                System.out.println("本地zuoye文件夹创建失败");
            }
        }
        final String fileName = "zuoye.json";
        String bacFileName = "bac" + fileName;
        final File bacFile = new File(zuoyePath, bacFileName);
        String studate = getActivity().getSharedPreferences("zuoyeSP",
                Context.MODE_PRIVATE).getString("studate", "defValue");
        RequestParams params = new RequestParams();
        params.put("date", studate);
        RestClient.get("/ktzy", params, new FileAsyncHttpResponseHandler(bacFile) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Tool.backOnFailure(getActivity(), statusCode);
                SharedPreferences lastfenshuSP = getActivity().getSharedPreferences("lastfenshu", Context.MODE_PRIVATE);
                String lastFenshu = lastfenshuSP.getString("fenshu", "默认分数");
                tvGo.setText("本次作业 "+lastFenshu+"分");
                tvGo.setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                SharedPreferences.Editor editor = null;
                File zyFile = new File(zuoyePath, fileName);
                if (file.length() != 0) {
                    try {
                        FileOutputStream fos = new FileOutputStream(zyFile);
                        fos.write(DownUtils.readFile(file).getBytes());
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editor = Tool.getEditor(getActivity(), "zuoyeSP");
                } else {
                    SharedPreferences lastfenshuSP = getActivity().getSharedPreferences("lastfenshu", Context.MODE_PRIVATE);
                    String lastFenshu = lastfenshuSP.getString("fenshu", "默认分数");
                    tvGo.setText("本次作业 "+lastFenshu+"分");
                    tvGo.setEnabled(false);
                    Toast.makeText(getActivity(), "已是最新的课堂作业", Toast.LENGTH_SHORT).show();
                }
                bacFile.delete();
                zuoYeBean = ZuoYeBean.create(DownUtils.readFile(zyFile));
                if (editor != null) {
                    editor.putString("studate", zuoYeBean.getDate()).commit();
                }
                tvCurDate.setText(zuoYeBean.getDate());
                tvTime.setText(zuoYeBean.getTime());
                tvNumber.setText(zuoYeBean.getTimulist().size() + "");

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String fenshu = data.getExtras().getString("fenshu");
        tvGo.setText("本次作业 "+fenshu+"分");
        tvGo.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getActivity(), TimuActivity.class);
        String tag="test";
        String other="task";
        intent.putExtra("tag",tag);
        intent.putExtra("other",other);
        intent.putExtra("timulist",zuoYeBean.getTimulist().toString());
        intent.putExtra("shijian",zuoYeBean.getTime());
        intent.putExtra("date",zuoYeBean.getDate());
        startActivityForResult(intent,CODETIMU);
        //startActivity(intent);
    }
}
