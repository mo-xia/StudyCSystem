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
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.views.MyRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
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
    int finishTime;
    String zyDate;

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
        int startPos = 0;
        if (tag.equals("training")) {
            startPos = getIntent().getExtras().getInt("position");
            int zhangjieId = getIntent().getExtras().getInt("zhangjieId");
            seletion = "zhangjie_id=?";
            selectionArgs = new String[]{zhangjieId + ""};
        } else if (tag.equals("test")) {

            String selectionStr = null;
            String other = getIntent().getExtras().getString("other");
            if (other != null) {
                zyDate = getIntent().getExtras().getString("date");
                int finishTime = Integer.parseInt(getIntent().getExtras().getString("shijian"));
                isTask = true;
                String strList = getIntent().getExtras().getString("timulist");
                selectionStr = "(" + strList.substring(1, strList.length() - 1) + ")";
            } else {
                //shijian = Integer.parseInt(getIntent().getExtras().getString("shijian"));
                int tishu = Integer.parseInt(getIntent().getExtras().getString("tishu"));
                String[] randTimu = Tool.getRandTimu(context, tishu);
                selectionStr = Tool.getSelectionArgs(randTimu);
            }
            seletion = "_id IN " + selectionStr;
            selectionArgs = null;
        } else {
            String[] cuotitests = getIntent().getExtras().getStringArray("cuotitest");
            String selectionStr = Tool.getSelectionArgs(cuotitests);
            seletion = "_id IN " + selectionStr;
            selectionArgs = null;
        }

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
     * 题目活动销毁时 退出该页面时 把错题和提问的题号写入文件中
     */
    @Override
    protected void onDestroy() {
        String path= DownUtils.getRootPath(this);
        super.onDestroy();
        if (tag.equals("test")) {
            if (chengjiList != null) {
                File file3 = new File(path, "chengji.txt");
                try {
                    BufferedWriter bw3 = new BufferedWriter(new FileWriter(file3, true));
                    for (int i = 0; i < chengjiList.size(); i++) {
                        bw3.write(chengjiList.get(i) + "\r");
                    }
                    chengjiList.clear();
                    bw3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            File file = new File(path, "cuoti.txt");
            File file1 = new File(path, "tiwen.txt");
            File file2 = new File(path, "zhengque.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1, true));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2, true));

            //把相应的题目状态写进文件中
            for (int j = 0; j < tiwenList.size(); j++) {
                bw1.write(tiwenList.get(j) + "\r");
            }
            for (int a = 0; a < zhengqueList.size(); a++) {
                bw2.write(zhengqueList.get(a) + "\r");
            }
            for (int i = 0; i < cuotiList.size(); i++) {
//                System.out.println(cuotiList.size());
//                System.out.println(cuotiList.get(i));
                bw.write(cuotiList.get(i) + "\r");
            }

            //清空缓冲区和集合
            bw.close();
            bw1.close();
            bw2.close();
            cuotiList.clear();
            tiwenList.clear();
            zhengqueList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class MyListAdapter extends RecyclerView.Adapter implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

        /**
         * 构造方法中获得要显示的数据
         */
        public MyListAdapter() {
            timuList = getData();
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

            //设置题号
            String timuId = position + 1 + "、";
            viewHolder.tihao.setText(timuId);

            //设置主标题
            viewHolder.zhubiaoti.setText(timu.getZhubiaoti());

            //设置代码块
            setDaimakuai();

            //设置选项内容
            viewHolder.rb_select_A.setText(timu.getSelectA());
            viewHolder.rb_select_B.setText(timu.getSelectB());
            viewHolder.rb_select_C.setText(timu.getSelectC());
            viewHolder.rb_select_D.setText(timu.getSelectD());

            //获得正确答案的id
            daanStr = timu.getDaan();
            daanId = zhengqueDaan(daanStr, viewHolder.rb_select_A, viewHolder.rb_select_B,
                    viewHolder.rb_select_C, viewHolder.rb_select_D);

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

        /**
         * 设置代码块 若有代码块则显示，没有就不显示
         */
        public void setDaimakuai() {
            if (timu.getDaimakuai() != null) {
                viewHolder.daimakuai.setVisibility(View.VISIBLE);
                String tempCode = timu.getDaimakuai(); // 获得代码块
                //System.out.println(tempCode);
                String[] split = tempCode.split("##"); //把代码块用“##”分开
                //加入换行符
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < split.length; i++) {
                    String sub = split[i] + "\n";
                    //System.out.println(sub);
                    sb.append(sub);
                }
                //System.out.println("sb: "+sb.toString());
                //显示格式化之后的代码块
                viewHolder.daimakuai.setText(sb.toString());
            }
        }

        /**
         * @param daanStr 正确答案 比如“A”
         * @param selectA 选项A
         * @param selectB 选项B
         * @param selectC 选项C
         * @param selectD 选项D
         * @return 正确答案的选项的资源id
         */
        public int zhengqueDaan(String daanStr, RadioButton selectA, RadioButton selectB, RadioButton selectC, RadioButton selectD) {
            if (selectA.getTag().equals(daanStr)) {
                return selectA.getId();
            } else if (selectB.getTag().equals(daanStr)) {
                return selectB.getId();
            } else if (selectC.getTag().equals(daanStr)) {
                return selectC.getId();
            } else {
                return selectD.getId();
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
                        SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                        String userName = user.getString("username", "Studio");
                        userNum = user.getString("usernum", "20140920");
                        JSONObject json = new JSONObject();
                        try {
                            json.put("stuName", userName);
                            json.put("stuNum", userNum);
                            json.put("stuFen", fenshuStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RequestParams params = new RequestParams();
                        params.put("date", zyDate);
                        params.put("usernum", userNum);
                        params.put("userjson", json.toString());
                        RestClient.get("/chengji", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(context,"交卷成功",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Tool.backOnFailure(context, statusCode);
                            }
                        });
                        Intent intent=new Intent();
                        intent.putExtra("fenshu",fenshuStr);
                        setResult(Activity.RESULT_OK,intent);
                        SharedPreferences.Editor lastfenshu = Tool.getEditor(context, "lastfenshu");
                        lastfenshu.putString("fenshu",fenshuStr).commit();
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
                rb_select_A.setTag("A");
                rb_select_B.setTag("B");
                rb_select_C.setTag("C");
                rb_select_D.setTag("D");
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
        public ArrayList<TiMuBean> getData() {
            SQLiteDatabase db = Tool.getDataBase(context);
            Cursor cursor = db.query(MyConstants.TABLE_TIMU, null, seletion, selectionArgs, null, null, null);

            return Tool.setTiMuBean(cursor);
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
                    if (cuotiList.size() > 0) { // 将错题作为查询数据库的标记，重新加载当前Activity
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
