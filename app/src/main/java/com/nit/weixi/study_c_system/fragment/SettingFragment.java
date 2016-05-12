package com.nit.weixi.study_c_system.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.AboutActivity;
import com.nit.weixi.study_c_system.activity.DataActivity;
import com.nit.weixi.study_c_system.activity.MyBaseActivity;
import com.nit.weixi.study_c_system.activity.TaskActivity;
import com.nit.weixi.study_c_system.activity.TestActivity;
import com.nit.weixi.study_c_system.activity.TrainingActivity;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.tools.UpdateUtils;
import com.nit.weixi.study_c_system.views.UpdateTikuDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * 设置与帮助界面
 * Created by weixi on 2016/3/30.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private LinearLayout llHelp;
    private LinearLayout llFeedback;
    private LinearLayout llUpdate;
    private LinearLayout llOpensource;
    private LinearLayout llAccout;
    private LinearLayout llAbout;
    private Context context;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_setting, container, false);
        initLayout(view);
        setOnClick();
        context = getActivity();
        return view;
    }

    public void setOnClick() {
        llHelp.setOnClickListener(this);
        llFeedback.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llOpensource.setOnClickListener(this);
        llAccout.setOnClickListener(this);
        llAbout.setOnClickListener(this);
    }

    public void initLayout(View view) {
        llHelp = (LinearLayout) view.findViewById(R.id.ll_help);
        llFeedback = (LinearLayout) view.findViewById(R.id.ll_feedback);
        llUpdate = (LinearLayout) view.findViewById(R.id.ll_update);
        llOpensource = (LinearLayout) view.findViewById(R.id.ll_opensource);
        llAccout = (LinearLayout) view.findViewById(R.id.ll_account);
        llAbout = (LinearLayout) view.findViewById(R.id.ll_about);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_help:
                break;
            case R.id.ll_feedback:
                break;
            case R.id.ll_update:
                mProgressDialog=new ProgressDialog(context);
                UpdateTikuDialog dialog = new UpdateTikuDialog(getActivity());
                dialog.setIcon(R.drawable.ic_cloud_download_grey_700_36dp);
                dialog.setTitle("你确定要更新题库吗？");
                dialog.setUpdateTikuInterface(new UpdateTikuDialog.UpdateTikuInterface() {
                    @Override
                    public void doUpdate() {
                        //1.首先下载最新的题库
                        SharedPreferences sp = getActivity().getSharedPreferences("tikuversion", Context.MODE_PRIVATE);
                        UpdateUtils.downloadTiku(context,mProgressDialog,sp);
                    }
                });
                dialog.show();
                break;
            case R.id.ll_opensource:
                break;
            case R.id.ll_account:
                break;
            case R.id.ll_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                intent.putExtra("tag",  getActivityTag());
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取当前fragment所属Activity的tag
     *
     * @return tag
     */
    @NonNull
    public String getActivityTag() {
        String tag;
        Class<? extends Activity> rootClass = getActivity().getClass();
        if (rootClass == DataActivity.class) {
            tag = MyConstants.FRAGMENT_DATA;
        } else if (rootClass == TestActivity.class) {
            tag = MyConstants.FRAGMENT_TEST;
        } else if (rootClass == TaskActivity.class) {
            tag = MyConstants.FRAGMENT_TASK;
        } else if (rootClass == TrainingActivity.class) {
            tag = MyConstants.FRAGMENT_TRAINING;
        } else {
            tag = "";
        }
        return tag;
    }
}
