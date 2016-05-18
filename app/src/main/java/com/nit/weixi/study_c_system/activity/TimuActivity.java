package com.nit.weixi.study_c_system.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.nit.weixi.study_c_system.views.MyRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 题目详情界面
 * Created by weixi on 2016/4/2.
 */
public class TimuActivity extends AppCompatActivity {

    MyRecyclerView rl;
    Context context;
    int pos;
    String tag;
    String[] selectionArgs;
    String seletion;
    ArrayList<TiMuBean> timuList;
    ArrayList<String> cuotiList;
    ArrayList<String> tiwenList;
    ArrayList<String> zhengqueList;
    ArrayList<String> chengjiList;
    boolean isTask;
    long haoMiao;
    int finishTime;
    int tishu;
    String zyDate;
    LinearLayout ll_data;
    TextView tv_tishu;
    TextView tv_time;
    int startPos = 0;
    MyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timu);

        //初始化recyclerview的状态
        rl = (MyRecyclerView) findViewById(R.id.rl_timu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rl.setLayoutManager(linearLayoutManager);

        context = this;
        tag = getIntent().getExtras().getString("tag");

        initWithTag(); //初始化

        //获得从上一个activity中传来的章节和位置信息
        //给recyclerview添加adapter
        timuList = new ArrayList<TiMuBean>();
        MyListAdapter adapter = new MyListAdapter();

        rl.setAdapter(adapter);

        //设置一开始显示的位置
        pos = startPos;
        //System.out.println(startPos);
        if (startPos != 0) {
            rl.isTouch = true;
            rl.scrollToPosition(startPos);
            rl.isTouch = false;
        }
        tiwenList = new ArrayList<>();
        cuotiList = new ArrayList<>();
        zhengqueList = new ArrayList<>();
    }

    /**
     * 我的时间倒计时类
     */
    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 每经过 countDownInterval 做什么操作
         * @param millisUntilFinished 总时长
         */
        @Override
        public void onTick(long millisUntilFinished) {
            String mTime=Tool.formatTime(millisUntilFinished-1000);
            tv_time.setText(mTime);
        }

        /**
         * 倒计时完成之后做什么
         */
        @Override
        public void onFinish() {
            clickJiaojuan();
            Toast.makeText(TimuActivity.this,"规定时间到了，系统将为你自动提交",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据传入的tag 做相应的初始化
     */
    private void initWithTag() {
        switch (tag) {
            case "training":
                startPos = getIntent().getExtras().getInt("position");
                int zhangjieId = getIntent().getExtras().getInt("zhangjieId");
                seletion = "zhangjie_id=?";
                selectionArgs = new String[]{zhangjieId + ""};
                break;
            case "test": {

                String selectionStr = null;

                String other = getIntent().getExtras().getString("other");
                finishTime = Integer.parseInt(getIntent().getExtras().getString("shijian"));
                haoMiao=finishTime*60*1000;
                tishu = Integer.parseInt(getIntent().getExtras().getString("tishu"));
                if (other != null) {
                    zyDate = getIntent().getExtras().getString("date");
                    isTask = true;
                    String strList = getIntent().getExtras().getString("timulist");
                    selectionStr = "(" + strList.substring(1, strList.length() - 1) + ")";
                } else {
                    String[] randTimu = Tool.getRandTimu(context, tishu);
                    selectionStr = Tool.getSelectionArgs(randTimu);
                }
                ll_data= (LinearLayout) findViewById(R.id.ll_data);
                tv_tishu= (TextView) findViewById(R.id.tv_timu_tishu);
                tv_time= (TextView) findViewById(R.id.tv_timu_time);
                tv_tishu.setText(tishu+"");
                tv_time.setText(Tool.formatTime(haoMiao));
                ll_data.setVisibility(View.VISIBLE);
                timer=new MyTimer(finishTime*60*1000,1000);
                timer.start();
                seletion = "_id IN " + selectionStr;
                selectionArgs = null;
                break;
            }
            default: {
                String[] cuotitests = getIntent().getExtras().getStringArray("cuotitest");
                String selectionStr = Tool.getSelectionArgs(cuotitests);
                seletion = "_id IN " + selectionStr;
                selectionArgs = null;
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (tag.equals("test")){
            clickJiaojuan();
            timer.cancel();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 题目活动销毁时 退出该页面时 把错题和提问的题号写入文件中
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tag.equals("test")&&chengjiList.size()!=0) {
            TimuUtils.writeFileFromList(this,MyConstants.CHENGJI_FILE_NAME,chengjiList);
        }
        TimuUtils.writeFileFromList(this,MyConstants.CUOTI_FILE_NAME,cuotiList);
        TimuUtils.writeFileFromList(this,MyConstants.TIWEN_FILE_NAME,tiwenList);
        TimuUtils.writeFileFromList(this,MyConstants.ZHENGQUE_FILE_NAME,zhengqueList);
    }

    class MyListAdapter extends RecyclerView.Adapter implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

        /**
         * 构造方法中获得要显示的数据
         */
        public MyListAdapter() {
            timuList = (ArrayList<TiMuBean>) getData();
        }

        TiMuBean timu;  //声明一个题目
        int daanId;  //声明答案的Id
        String daanStr; //声明答案的内容
        MyViewHolder viewHolder;
        boolean ischecked = false;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.timu_list_item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);

            //设置响应事件
            myViewHolder.daan.setOnClickListener(this);
            myViewHolder.next.setOnClickListener(this);
            myViewHolder.shangyiti.setOnClickListener(this);
            myViewHolder.rg_select.setOnCheckedChangeListener(this);
            myViewHolder.woyaotiwen.setOnClickListener(this);
            myViewHolder.jiaojuan.setOnClickListener(this);

            return myViewHolder;
        }

        /**
         * 显示view的内容
         *
         * @param holder   可以服用的viewholder
         * @param position 当前itemview的位置
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            timu = timuList.get(position);
            viewHolder = (MyViewHolder) holder;
            ischecked = true;

            //获得正确答案
            daanStr = timu.getDaan();

            //设置题号
            String timuId = position + 1 + "、";
            viewHolder.tihao.setText(timuId);

            //设置主标题
            viewHolder.zhubiaoti.setText(timu.getZhubiaoti());

            //设置代码块
            TimuUtils.setDaimakuai(timu, viewHolder.daimakuai);

            //设置选项内容
            viewHolder.rb_select_A.setText(timu.getSelectA());
            viewHolder.rb_select_B.setText(timu.getSelectB());
            viewHolder.rb_select_C.setText(timu.getSelectC());
            viewHolder.rb_select_D.setText(timu.getSelectD());

            //获得正确答案的id
            daanId = TimuUtils.getDaanId(timu, viewHolder.rb_select_A.getId(),
                    viewHolder.rb_select_B.getId(),
                    viewHolder.rb_select_C.getId(),
                    viewHolder.rb_select_D.getId());

            //第一题时不显示上一题按钮
            if (position == 0) {
                viewHolder.ll_shangyiti.setVisibility(View.GONE);
            } else {
                viewHolder.ll_shangyiti.setVisibility(View.VISIBLE);
            }

            //最后一题时不显示下一题按钮
            if (position == timuList.size() - 1) {
                viewHolder.ll_next.setVisibility(View.GONE);
            } else {
                viewHolder.ll_next.setVisibility(View.VISIBLE);
            }

            if (tag.equals("test")) {
                viewHolder.ll_daan.setVisibility(View.GONE);
                viewHolder.ll_shangyiti.setVisibility(View.GONE);
                if (position == timuList.size() - 1) {
                    viewHolder.ll_next.setVisibility(View.GONE);
                    viewHolder.ll_jiaojuan.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return timuList.size();
        }


        /**
         * 响应各个按钮的点击事件
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //点击答案的响应事件
                case R.id.btn_daan:
                    clickDaan();
                    break;
                case R.id.btn_next:
                    rl.isTouch = true; // 让item可以响应点击事件
                    rl.scrollToPosition(pos + 1); //让view显示下一个item
                    //System.out.println("下一题: " + pos);
                    pos++; // 按按钮时的位置是这一题的，点击下一题位置应该加1
                    rl.isTouch = false;// 重置item点击事件，禁止点击滑动
                    viewHolder.daan.setText("答案");// 把答案的显示内容还原
                    ischecked = false;
                    viewHolder.rg_select.clearCheck(); //把radiobutton的状态全部置为空
                    ischecked = true;
                    break;
                case R.id.btn_shangyiti:  //和下一题差不多
                    rl.isTouch = true;
                    rl.scrollToPosition(pos - 1);
                    // System.out.println("上一题: " + pos);
                    pos--;
                    rl.isTouch = false;
                    viewHolder.daan.setText("答案");
                    ischecked = false;
                    viewHolder.rg_select.clearCheck();
                    ischecked = true;
                    break;
                case R.id.btn_woyaotiwen:
                    //当点击我要提问时 把当前题目的在数据库中的题号记录到文件中
                    //也就是timu.tihao;
                    String tiwen = timu.getTihao() + "";
                    tiwenList.add(tiwen);
                    Toast.makeText(context, "已将问题提交，请稍后到老师答疑板块寻找详细解答", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_jiaojuan:  // 处理点击交卷的响应事件
                    if (timer!=null){
                        timer.cancel();
                    }
                    clickJiaojuan();
                    break;
            }

        }

        /**
         * 处理点击答案按钮的事件
         */
        public void clickDaan() {
            //如果当前显示文本是答案两个字 把答案的选项显示出来，把我要提问按钮显示出来
            if (viewHolder.daan.getText().equals("答案")) {
                viewHolder.daan.setText(daanStr);
                if (viewHolder.ll_shangyiti.getVisibility() == View.GONE) {
                    viewHolder.ll_woyaotiwen.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ll_shangyiti.setVisibility(View.GONE);
                    viewHolder.ll_woyaotiwen.setVisibility(View.VISIBLE);
                }
            } else {//如果不是显示答案两个字，则把答案两个字显示出来
                //隐藏我要提问按钮，并按原有布局显示上一题
                viewHolder.daan.setText("答案");
                viewHolder.ll_woyaotiwen.setVisibility(View.GONE);
                if (pos == 0) {
                    viewHolder.ll_shangyiti.setVisibility(View.GONE);
                } else {
                    viewHolder.ll_shangyiti.setVisibility(View.VISIBLE);
                }
            }
        }

        /**
         * 选项发生改变时调用
         *
         * @param group     单选按钮组
         * @param checkedId 选择的id
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //System.out.println(checkedId);
            //因为radiogroup源码会执行两次 checkedId动作，故而加上一个布尔变量
            //否则当调用clearCheck()时会执行一次toast事件
            if (checkedId != -1 && ischecked) {
                if (daanId == checkedId) {
                    String zhengque = timu.getTihao() + "";
                    zhengqueList.add(zhengque);
                    if (tag.equals("training")) {
                        Toast.makeText(context, "你答对了", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String cuoti = timu.getTihao() + "";
                    cuotiList.add(cuoti);
                    //System.out.println(cuotiList.toString());
                    if (tag.equals("training")) {
                        Toast.makeText(context, "你答错了,已为你加入错题本", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        /**
         * 初始化布局 控件
         */
        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
                tihao = (TextView) itemView.findViewById(R.id.tihao);
                zhubiaoti = (TextView) itemView.findViewById(R.id.zhubiaoti);
                daimakuai = (TextView) itemView.findViewById(R.id.daimakuai);
                ll_shangyiti = (LinearLayout) itemView.findViewById(R.id.ll_shangyiti);
                ll_next = (LinearLayout) itemView.findViewById(R.id.ll_next);
                ll_woyaotiwen = (LinearLayout) itemView.findViewById(R.id.ll_woyaotiwen);
                ll_jiaojuan = (LinearLayout) itemView.findViewById(R.id.ll_jiaojuan);
                ll_daan = (LinearLayout) itemView.findViewById(R.id.ll_daan);
                rg_select = (RadioGroup) itemView.findViewById(R.id.rg_select);
                rb_select_A = (RadioButton) itemView.findViewById(R.id.rb_select_A);
                rb_select_B = (RadioButton) itemView.findViewById(R.id.rb_select_B);
                rb_select_C = (RadioButton) itemView.findViewById(R.id.rb_select_C);
                rb_select_D = (RadioButton) itemView.findViewById(R.id.rb_select_D);
                daan = (Button) itemView.findViewById(R.id.btn_daan);
                next = (Button) itemView.findViewById(R.id.btn_next);
                woyaotiwen = (Button) itemView.findViewById(R.id.btn_woyaotiwen);
                shangyiti = (Button) itemView.findViewById(R.id.btn_shangyiti);
                jiaojuan = (Button) itemView.findViewById(R.id.btn_jiaojuan);
            }

            TextView zhubiaoti;
            TextView daimakuai;
            LinearLayout ll_shangyiti;
            LinearLayout ll_next;
            LinearLayout ll_woyaotiwen;
            LinearLayout ll_jiaojuan;
            LinearLayout ll_daan;
            RadioGroup rg_select;
            RadioButton rb_select_A;
            RadioButton rb_select_B;
            RadioButton rb_select_C;
            RadioButton rb_select_D;
            Button daan;
            Button next;
            Button shangyiti;
            Button woyaotiwen;
            Button jiaojuan;
            TextView tihao;
        }

        /**
         * 从数据库获取数据，并储存到list中
         */
        public List<TiMuBean> getData() {
            SQLiteDatabase db = Tool.getDataBase(context);
            Cursor cursor = db.query(MyConstants.TABLE_TIMU_NAME, null, seletion, selectionArgs, null, null, null);

            return Tool.setTiMuBean(cursor);
        }
    }

    /**
     * 点击交卷时做的操作
     */
    public void clickJiaojuan() {
        chengjiList = new ArrayList<String>(); // 初始化chengjiList
        //获得成绩
        int fenshu = zhengqueList.size() * 100 / timuList.size();
        String fenshuStr = fenshu + "";
        //将获得的成绩信息添加到chengjiList中
        String currentDate = Tool.getCurrentDate();
        String chengji = fenshuStr + "##" + currentDate;//成绩信息由当前分数和当前时间
        chengjiList.add(chengji);
        String userNum;
        if (isTask) {

            SharedPreferences user = getSharedPreferences(MyConstants.STUDENT_SP, MODE_PRIVATE);
            String userName = user.getString(MyConstants.STUDENT_SP_NAME, "Studio");
            userNum = user.getString(MyConstants.STUDENT_SP_NUMBER, "20140920");
            JSONObject json = new JSONObject();
            try {
                json.put("stuName", userName);
                json.put("stuNum", userNum);
                json.put("stuFen", fenshuStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestParams params = new RequestParams();
            params.put("tag","jiaojuan");
            params.put("date", zyDate);
            params.put("usernum", userNum);
            params.put("userjson", json.toString());
            RestClient.get(MyConstants.CHENGJI_URL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "交卷成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Tool.backOnFailure(context, statusCode);
                }
            });
            Intent intent = new Intent();
            intent.putExtra("fenshu", fenshuStr);
            setResult(Activity.RESULT_OK, intent);
            SharedPreferences.Editor lastfenshu = Tool.getEditor(context, MyConstants.ZUOYE_SP);
            lastfenshu.putString(MyConstants.ZUOYE_SP_FENSHU, fenshuStr).commit();
            finish();
        } else {
            //弹出一个alertDialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            //设置一个自定义的alertDialog布局
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_test, null, false);
            dialog.setView(dialogView);
            doInDialog(dialog, dialogView, fenshu, fenshuStr);
        }
    }

    public void doInDialog(AlertDialog.Builder dialog, View dialogView, int fenshu, String fenshuStr) {
        //初始化alertDialog中的控件
        LinearLayout ll_fenshu = (LinearLayout) dialogView.findViewById(R.id.ll_ad_test);
        ImageView iv_bg = (ImageView) dialogView.findViewById(R.id.iv_ad_bg);
        TextView tv_fenshu = (TextView) dialogView.findViewById(R.id.tv_ad_test);
        //根据获得的分数来判断显示的alertDialog的样式
        if (fenshu >= 70) {
            tv_fenshu.setText(fenshuStr);
        } else if (fenshu >= 60) {
            iv_bg.setImageResource(R.mipmap.jige);
            ll_fenshu.setVisibility(View.GONE);
        } else {
            iv_bg.setImageResource(R.mipmap.guake);
            ll_fenshu.setVisibility(View.GONE);
        }
        //如果不是满分，就显示查看错题按钮
        if (fenshu != 100) {
            dialog.setPositiveButton("查看错题", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //System.out.println("size:"+cuotiList.size());
                    //判断当前cuotiList中是否有题目，防止交白卷的
                    if (cuotiList.size() > 0) {
                        // 将错题作为查询数据库的标记，重新加载当前Activity
                        String[] cuotiTest = cuotiList.toArray(new String[cuotiList.size() - 1]);
                        Intent intent = new Intent(context, TimuActivity.class);
                        intent.putExtra("tag", "cuoti");
                        intent.putExtra("cuotitest", cuotiTest);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "你个熊孩子居然敢交白卷", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        dialog.setNegativeButton("再考一次", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setCancelable(false); //
        dialog.show();
    }
}
