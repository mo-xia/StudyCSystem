package com.nit.weixi.study_c_system.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.data.ChengJiBean;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 学生成绩详情模块
 * Created by weixi on 2016/5/10.
 */
public class DetailChengjiActivity extends MyBackActivity {

    List<ChengJiBean> chengjiList; //一个成绩列表
    MyChengjiAdapter adapter;
    String title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fg_record);

        // 设置显示的标题
        title=getIntent().getStringExtra("title");
        setBackActionBar(title,null);

        RecyclerView rl= (RecyclerView) findViewById(R.id.rl_record);
        rl.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        chengjiList=new ArrayList<ChengJiBean>();
        adapter=new MyChengjiAdapter();
        loadChengjiList();
        rl.setAdapter(adapter);
    }

    /**
     * 加载一个详细的成绩列表
     */
    private void loadChengjiList(){
        RequestParams params=new RequestParams();
        params.put("tag","chengjilist");
        params.put("title",title);
        RestClient.get(MyConstants.CHENGJI_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String chengjiJson=new String(responseBody);
                if (!chengjiJson.equals("")) {
                    ChengJiBean[] chengjis=new Gson().fromJson(chengjiJson,ChengJiBean[].class);
                    chengjiList= Arrays.asList(chengjis);
                    adapter.notifyDataSetChanged();
                }else {
                    Tool.displayEmpty4Server(DetailChengjiActivity.this);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(DetailChengjiActivity.this,statusCode);
            }
        });
    }

    class MyChengjiAdapter extends RecyclerView.Adapter {

        Context context;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context=parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.cjdetail_list_item, parent, false);
            ChengjiListHolder viewHolder=new ChengjiListHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChengjiListHolder mViewHolder= (ChengjiListHolder) holder;
            int color = Tool.getColor(context, R.color.colorPrimary);
            if (position==0){
                mViewHolder.tvStuId.setBackgroundColor(color);
                mViewHolder.tvStuName.setBackgroundColor(color);
                mViewHolder.tvStuFenshu.setBackgroundColor(color);
                mViewHolder.tvStuId.setText("学号");
                mViewHolder.tvStuName.setText("姓名");
                mViewHolder.tvStuFenshu.setText("分数");
            }else {
                if (position%2!=0){
                    mViewHolder.itemView.setBackgroundColor(Tool.getColor(context,R.color.colorZhangjieBg));
                }
                mViewHolder.tvStuFenshu.setTextColor(color);
                mViewHolder.tvStuId.setText(chengjiList.get(position-1).getStuNum());
                mViewHolder.tvStuName.setText(chengjiList.get(position-1).getStuName());
                mViewHolder.tvStuFenshu.setText(chengjiList.get(position-1).getStuFen());
            }
        }

        @Override
        public int getItemCount() {
            return chengjiList.size()+1;
        }

        class ChengjiListHolder extends RecyclerView.ViewHolder {
            private TextView tvStuId;
            private TextView tvStuName;
            private TextView tvStuFenshu;
            public ChengjiListHolder(View itemView) {
                super(itemView);
                tvStuId= (TextView) itemView.findViewById(R.id.tv_stu_id);
                tvStuName= (TextView) itemView.findViewById(R.id.tv_stu_name);
                tvStuFenshu= (TextView) itemView.findViewById(R.id.tv_stu_fenshu);
            }
        }
    }
}
