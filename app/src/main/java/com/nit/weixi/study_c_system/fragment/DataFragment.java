package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.FileTreeActivity;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.views.DownloadConfirmDialog;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;


/**
 * Created by weixi on 2016/4/14.
 */
public class DataFragment extends Fragment implements View.OnClickListener {

    public LinearLayout llDownload; //下载布局
    public ImageView ivDownload;
    public TextView tvDownload;
    public String ROOT_PATH; //应用根路径
    public String DOWNLOAD_PATH; //下载路径
    public String MD_PATH; //markdown文件路径
    private ProgressDialog mProgressDialog; // 下载进度条
    private DownloadConfirmDialog mDownloadConfirmDialog; //下载确认框

    String lang;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_data,container,false);

        llDownload= (LinearLayout) view.findViewById(R.id.ll_download_c);
        ivDownload= (ImageView) view.findViewById(R.id.iv_icon_download);
        tvDownload = (TextView) view.findViewById(R.id.tv_download_c);

        llDownload.setOnClickListener(this);

        // 初始化路径数据
        ROOT_PATH = DownUtils.getRootPath(getActivity());// /Android/data/包名/files
        MD_PATH = DownUtils.getMdPath(getActivity()); // /Android/data/包名/files/md
        DOWNLOAD_PATH = DownUtils.getDownloadPath(getActivity());
        flushBg("c"); //刷新c库下载的背景
        initDialog();

        return view;
    }


    /**
     * 刷新下载布局的背景
     */
    private void flushBg(String lang) {
        if (isFinishDown(lang)){
            ivDownload.setVisibility(View.GONE);
            tvDownload.setVisibility(View.VISIBLE);
        }else {
            ivDownload.setVisibility(View.VISIBLE);
            tvDownload.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化提示框 包括下载进度条 和下载确认提示框
     */
    private void initDialog() {
        //初始化下载进度条 设置为水平样式
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        //初始化确认下载提示框 并添加点击事件
        mDownloadConfirmDialog = new DownloadConfirmDialog(getActivity());
        mDownloadConfirmDialog.setConfirmClickListener(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadLang(mDownloadConfirmDialog.getLang()); //处理确认下载提示框的按钮点击事件
            }
        });

    }

    /**
     * 根据 md/c/下是否存在文件 来判断是否已经下载 有则已下载好
     * @param lang 模块名字
     * @return 是否下载好
     */
    private boolean isFinishDown(String lang){
        return (DownUtils.exists(MD_PATH + File.separator + lang));
    }

    /**
     * 点击c语言按钮的响应事件
     *
     * @param v 当前view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download_c:
                lang = "c";
                if (isFinishDown(lang)) { // 已下载
                    //则 打开文件目录activity
                    Intent intent = new Intent(getActivity(), FileTreeActivity.class);
                    intent.putExtra("LANG_PATH", MD_PATH + File.separator + lang);
                    startActivity(intent);
                } else {  // 未下载
                    // 则 显示确认下载提示框
                    mDownloadConfirmDialog.show(lang);
                }
                break;
        }
    }

    /**
     * 响应确认下载提示框的点击事件
     * new File(DOWNLOAD_PATH, lang + ".zip") : 下载后的完整路径的文件名
     * @param lang 点击的模块
     */
    private void downloadLang(String lang) {
        //下载相应的模块.zip
        DownUtils.downloadLang(lang, new FileAsyncHttpResponseHandler(
                new File(DOWNLOAD_PATH, lang + ".zip")) {

            //FileAsyncHttpResponseHandler的回掉方法

            /**
             * 开始下载的回掉 显示下载进度条
             */
            @Override
            public void onStart() {
                mProgressDialog.setTitle("正在下载..");
                mProgressDialog.setProgress(0);
                mProgressDialog.setMax(100);
                mProgressDialog.show();
            }

            /**
             * 下载成功后开始解压操作
             * @param arg0
             * @param arg1
             * @param file
             */
            @Override
            public void onSuccess(int arg0, Header[] arg1,
                                  File file) {
                // 解压
                new UnzipFileTask(file).execute();
            }

            /**
             * 下载失败时的回掉 吐司出来下载失败的原因 并且关掉下载进度条
             * @param status_code
             * @param arg1
             * @param arg2
             * @param file
             */
            @Override
            public void onFailure(int status_code, Header[] arg1,
                                  Throwable arg2, File file) {
                Tool.backOnFailure(getActivity(),status_code);
                mProgressDialog.dismiss();
            }

            /**
             * 下载过程中的回掉 监控下载进度 调整相应的进度条显示
             * @param bytesWritten
             * @param totalSize
             */
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);

                mProgressDialog.setMessage(String.format("大小:%.2f M", 1.0 * totalSize / 1024 / 1024));
                mProgressDialog.setMax((int) totalSize);
                mProgressDialog.setProgress((int) bytesWritten);
            }

        });
    }


    /**
     * 开启一个异步任务 解压操作
     */
    class UnzipFileTask extends AsyncTask<Void, Void, Boolean> {

        private File downloadFile;

        public UnzipFileTask(File downloadfile) {
            this.downloadFile = downloadfile;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("正在转换数据..");
        }

        /**
         * 进行解压操作
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Tool.unZipFiles(downloadFile.getAbsolutePath(), MD_PATH
                        + File.separator);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * 完成解压后的回掉 先关掉下载进度条 吐司提醒用户 ，解压成功后删除压缩包
         * @param result 任务是否完成 完成为true 未完成为false
         */
        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            if (result) {
                // 解压成功
                flushBg(lang); //刷新c库背景
                if (downloadFile.exists()) {
                    downloadFile.delete();
                }
                Toast.makeText(getActivity(), "操作完成，请点击打开", Toast.LENGTH_SHORT).show();
                return;
            }
            // 解压失败,请求重试
            Toast.makeText(getActivity(), "数据转换失败，请重试!", Toast.LENGTH_SHORT).show();
        }
    }
}
