package com.nit.weixi.study_c_system.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.AboutActivity;
import com.nit.weixi.study_c_system.activity.DataActivity;
import com.nit.weixi.study_c_system.activity.DetailDataActivity;
import com.nit.weixi.study_c_system.activity.FeedbackActivity;
import com.nit.weixi.study_c_system.activity.TaskActivity;
import com.nit.weixi.study_c_system.activity.TestActivity;
import com.nit.weixi.study_c_system.activity.TrainingActivity;
import com.nit.weixi.study_c_system.activity.YindaoActivity;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.tools.UpdateUtils;
import com.nit.weixi.study_c_system.views.UpdateTikuDialog;

import java.io.File;

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
    private String settingPath;
    private Intent mdIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_setting, container, false);
        initLayout(view);
        context = getActivity();
        settingPath=DownUtils.getRootPath(context)+"/setting/";
        mdIntent=new Intent(context, DetailDataActivity.class);
        setOnClick();
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
                String helpbookPath=settingPath+ MyConstants.HELPBOOKNAME;
                startDetailDataMD(mdIntent,helpbookPath,"使用帮助");
                break;
            case R.id.ll_feedback:
                Intent feedbackIntent=new Intent(getActivity(), FeedbackActivity.class);
                feedbackIntent.putExtra("tag",  getActivityTag());
                startActivity(feedbackIntent);
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
                        SharedPreferences sp = getActivity().getSharedPreferences(MyConstants.TIKU_SP, Context.MODE_PRIVATE);
                        UpdateUtils.downloadTiku(context,mProgressDialog,sp);
                    }
                });
                dialog.show();
                break;
            case R.id.ll_opensource:
                String opensourcePath=settingPath+MyConstants.OPENSOURCENAME;
                startDetailDataMD(mdIntent,opensourcePath,"开源支持");
                break;
            case R.id.ll_account:
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                AlertDialog alertDialog = builder.create();
                alertDialog.setIcon(R.drawable.ic_warning_grey_700_36dp);
                alertDialog.setTitle("警告");
                alertDialog.setMessage("你确定切换账户吗？如果切换，当前用户的所有相关信息将会被删除！！！");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"你取消了切换账户操作",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent yindaoIntent=new Intent(context, YindaoActivity.class);
                        Tool.cleanSharedPreference(context);
                        File zuoye=new File(DownUtils.getRootPath(context)+"/zuoye");
                        Tool.deleteFilesByDirectory(zuoye);
                        Tool.deleteFilesByDirectory(context.getCacheDir());
                        Tool.deleteFile(context, MyConstants.CUOTI_FILE_NAME);
                        Tool.deleteFile(context, MyConstants.TIWEN_FILE_NAME);
                        Tool.deleteFile(context, MyConstants.ZHENGQUE_FILE_NAME);
                        Tool.deleteFile(context, MyConstants.FINISHTIWEN_FILE_NAME);
                        Tool.deleteFile(context,MyConstants.CHENGJI_FILE_NAME);
                        getActivity().finish();
                        startActivity(yindaoIntent);
                    }
                });
                alertDialog.show();
                break;
            case R.id.ll_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                intent.putExtra("tag",  getActivityTag());
                startActivity(intent);
                break;
        }
    }

    /**
     * 开启md的DetailData界面
     * @param intent intent
     * @param filePath 文件路径
     * @param title 标题
     */
    private void startDetailDataMD(Intent intent,String filePath,String title) {
        intent.putExtra("tag",getActivityTag());
        intent.putExtra("link",filePath);
        intent.putExtra("title",title);
        startActivity(intent);
    }

    /**
     * 获取当前fragment所属Activity的tag
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
