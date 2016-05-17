package com.nit.weixi.study_c_system.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/5/12.
 */
public class FeedbackActivity extends MyBackActivity implements View.OnClickListener {

    private String TYPE_REPORT = "report"; // 报告错误
    private String TYPE_ADVICE = "advice";// 建议
    private int MAX_CONTENT_LENGTH = 400; //反馈内容最长为400
    private EditText et_content;
    private EditText et_contact;
    private RadioGroup rg_type;
    private Button btn_submit;

    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tag = getIntent().getStringExtra("tag");
        Tool.setMyTheme(this,tag);
        initLayout();
    }

    private void initLayout() {
        setContentView(R.layout.acty_feedback);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        setBackActionBar("意见反馈",mToolbar);
        et_content= (EditText) findViewById(R.id.et_feedback_content);
        et_contact= (EditText) findViewById(R.id.et_feedback_contact);
        rg_type= (RadioGroup) findViewById(R.id.rg_feedback_type);
        btn_submit= (Button) findViewById(R.id.btn_feedback_submit);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_feedback_submit:
                mProgressDialog=new ProgressDialog(this);
                mProgressDialog.setTitle("提示");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("正在发送请求.....");
                String content = et_content.getText().toString();
                String contact = et_contact.getText().toString();
                String type = rg_type.getCheckedRadioButtonId() == R.id.rb_type_advice ? TYPE_ADVICE
                        : TYPE_REPORT;
                String system_version = Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT;
                if (content.length() < 5) {
                    Toast.makeText(this,"多写点东西吧~",Toast.LENGTH_SHORT).show();
                    return;
                } else if (content.length() > MAX_CONTENT_LENGTH) {
                    Toast.makeText(this,"内容太多了,文字数应小于" + MAX_CONTENT_LENGTH,Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestParams params=new RequestParams();
                params.put("content",content);
                params.put("contact",contact);
                params.put("type",type);
                params.put("s_version",system_version);
                mProgressDialog.show();
                RestClient.get(MyConstants.FEEDBACK_URL, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        mProgressDialog.dismiss();
                        Toast.makeText(FeedbackActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        mProgressDialog.dismiss();
                        Tool.backOnFailure(FeedbackActivity.this,statusCode);
                    }
                });
                break;
        }
    }
}
