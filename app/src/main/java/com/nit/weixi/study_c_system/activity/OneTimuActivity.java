package com.nit.weixi.study_c_system.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.data.TiMuBean;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.TimuUtils;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 单个题目展示
 * Created by weixi on 2016/5/6.
 */
public class OneTimuActivity extends AppCompatActivity implements View.OnClickListener {

    private TiMuBean timuBean;
    private TextView tihao;
    private TextView zhubiaoti;
    private TextView daimakuai;
    private TextView answerText;
    private RadioGroup rg_select;
    private Button daan;
    private Button tiwen;
    private EditText dayi;
    private TextView tijiao;
    private RadioButton rb_select_A;
    private RadioButton rb_select_B;
    private RadioButton rb_select_C;
    private RadioButton rb_select_D;
    private List<String> tiwenList;
    private String tag;
    private String timuId;
    private String answer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        setData();
        tiwenList = new ArrayList<String>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!tag.equals("wenti")) {
            TimuUtils.writeFileFromList(this, MyConstants.TIWEN_FILE_NAME, tiwenList);
        }
    }

    /**
     * 设置控件显示的内容
     */
    private void setData() {
        tihao.setText(timuBean.getTihao() + "、");
        zhubiaoti.setText(timuBean.getZhubiaoti());
        TimuUtils.setDaimakuai(timuBean, daimakuai);
        rb_select_A.setText(timuBean.getSelectA());
        rb_select_B.setText(timuBean.getSelectB());
        rb_select_C.setText(timuBean.getSelectC());
        rb_select_D.setText(timuBean.getSelectD());
        int daanId = TimuUtils.getDaanId(timuBean, rb_select_A.getId(),
                rb_select_B.getId(), rb_select_C.getId(), rb_select_D.getId());
        rg_select.check(daanId);
        rg_select.setEnabled(false);
    }

    /**
     * 根据tag的不同，初始化界面控件设置点击事件
     */
    private void initLayout() {
        setContentView(R.layout.acty_onetime);
        tihao = (TextView) findViewById(R.id.tihao);
        zhubiaoti = (TextView) findViewById(R.id.zhubiaoti);
        daimakuai = (TextView) findViewById(R.id.daimakuai);
        rg_select = (RadioGroup) findViewById(R.id.rg_select);
        rb_select_A = (RadioButton) findViewById(R.id.rb_select_A);
        rb_select_B = (RadioButton) findViewById(R.id.rb_select_B);
        rb_select_C = (RadioButton) findViewById(R.id.rb_select_C);
        rb_select_D = (RadioButton) findViewById(R.id.rb_select_D);
        if (tag.equals("wenti")) { //老师做解答的题目详情界面
            LinearLayout llDayi = (LinearLayout) findViewById(R.id.ll_onetimu_dayi);
            llDayi.setVisibility(View.VISIBLE);
            dayi = (EditText) findViewById(R.id.et_onetimu_dayi);
            tijiao = (TextView) findViewById(R.id.tv_tijiao_dayi);
            tijiao.setOnClickListener(this);
        } else if (tag.equals("dayi")) { //学生提问题目获得答疑结果的界面
            answer = getIntent().getStringExtra("answer");
            ScrollView answerView= (ScrollView) findViewById(R.id.sv_answer);
            answerView.setVisibility(View.VISIBLE);
            answerText= (TextView) findViewById(R.id.tv_answer_text);
            answerText.setText(answer);
        } else { //学生错题本的题目详情界面
            daan = (Button) findViewById(R.id.btn_onetimu_daan);
            tiwen = (Button) findViewById(R.id.btn_onetimu_tiwen);
            daan.setVisibility(View.VISIBLE);
            daan.setText(timuBean.getDaan());
            daan.setOnClickListener(this);
            tiwen.setOnClickListener(this);
        }
    }

    /**
     * 初始化一些数据，包括tag ,题目的题号 题目信息
     */
    private void initData() {
        tag = getIntent().getStringExtra("tag");
        timuId = getIntent().getStringExtra("tihao");
        Cursor timuCursor = Tool.getDataBase(this).query(MyConstants.TABLE_TIMU_NAME, null, "_id = ?", new String[]{timuId}, null, null, null);
        timuBean = Tool.setTiMuBean(timuCursor).get(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onetimu_daan:
                daan.setVisibility(View.GONE);
                tiwen.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_onetimu_tiwen:
                tiwenList.add(timuBean.getTihao() + "");
                Toast.makeText(this, "已将问题提交，请稍后到老师答疑板块寻找详细解答", Toast.LENGTH_SHORT).show();
                tiwen.setVisibility(View.GONE);
                daan.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_tijiao_dayi:
                RequestParams params = new RequestParams();
                params.put("timuid", timuId);
                params.put("dayitag", "tijiaojieda");
                params.put("timutitle", zhubiaoti.getText().toString());
                params.put("answertext", dayi.getText().toString());
                RestClient.get(MyConstants.DAYI_URL, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(OneTimuActivity.this, "提交解答成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Tool.backOnFailure(OneTimuActivity.this, statusCode);
                    }
                });
                break;
        }
    }
}
