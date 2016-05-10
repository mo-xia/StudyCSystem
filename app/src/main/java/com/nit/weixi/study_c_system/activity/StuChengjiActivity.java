package com.nit.weixi.study_c_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.nit.weixi.study_c_system.data.AnswerBean;
import com.nit.weixi.study_c_system.data.MyRecyclerViewAdapter;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/5/9.
 */
public class StuChengjiActivity extends MyBackActivity{

    List<String> timeList;
    StuChengjiAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fg_record);
        setBackActionBar("学生成绩",null);
        RecyclerView view= (RecyclerView) findViewById(R.id.rl_record);
        view.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        timeList=new ArrayList<String>();
        adapter = new StuChengjiAdapter();
        loadtimeList();
        adapter.setOnItemClickLitener(new MyRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(StuChengjiActivity.this,DetailChengjiActivity.class);
                intent.putExtra("title",timeList.get(position));
                startActivity(intent);
            }
        });
        view.setAdapter(adapter);
    }

    private void loadtimeList(){
        RequestParams params=new RequestParams();
        params.put("tag","timelist");
        RestClient.get("/chengji", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String times=new String(responseBody);
                if (!times.equals("")) {
                    timeList = Tool.getListFromString(times);
                    adapter.notifyDataSetChanged();
                }else {
                    System.out.println("list为空");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(StuChengjiActivity.this,statusCode);
            }
        });
    }

    class StuChengjiAdapter extends MyRecyclerViewAdapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.stucj_list_item,parent,false);
            StuChengjiHolder mViewHolder=new StuChengjiHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            StuChengjiHolder viewHolder= (StuChengjiHolder) holder;
            viewHolder.cjTime.setText(timeList.get(position));
            setCallBack(viewHolder);
        }

        @Override
        public int getItemCount() {
            return timeList.size();
        }

        class StuChengjiHolder extends RecyclerView.ViewHolder {
            private TextView cjTime;
            public StuChengjiHolder(View itemView) {
                super(itemView);
                cjTime= (TextView) itemView.findViewById(R.id.tv_cj_time);
            }
        }
    }
}
