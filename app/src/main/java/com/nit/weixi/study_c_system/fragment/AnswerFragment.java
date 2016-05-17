package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.OneTimuActivity;
import com.nit.weixi.study_c_system.data.AnswerBean;
import com.nit.weixi.study_c_system.data.MyRecyclerViewAdapter;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;
import com.nit.weixi.study_c_system.views.CircleTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * 当前学生提问所对应的回答
 * Created by weixi on 2016/3/30.
 */
public class AnswerFragment extends Fragment {

    List<AnswerBean> answerList;
    MyAnswerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_answer,container,false);

        RecyclerView rl= (RecyclerView) view.findViewById(R.id.rl_answer);
        rl.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        answerList=new ArrayList<AnswerBean>();
        adapter=new MyAnswerAdapter();

        loadAnswer();
        adapter.setOnItemClickLitener(new MyRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), OneTimuActivity.class);
                String answerText=answerList.get(position).getAnswerText();
                intent.putExtra("tag","dayi");
                intent.putExtra("tihao",answerList.get(position).getTimuId());
                intent.putExtra("answer",answerText);
                startActivity(intent);
            }
        });
        rl.setAdapter(adapter);
        return view;
    }

    /**
     * 加载老师的answer
     */
    private void loadAnswer(){
        RequestParams params=new RequestParams();
        params.put("dayitag","getanswer");
        List<String> list = Tool.getListFromFile(getActivity(), MyConstants.FINISHTIWEN_FILE_NAME);
        params.put("timulist",list.toString());
        RestClient.get(MyConstants.DAYI_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String answerJson=new String(responseBody);
                if (!answerJson.equals("")) {
                    AnswerBean[] answers=new Gson().fromJson(answerJson,AnswerBean[].class);
                    answerList=Arrays.asList(answers);
                    adapter.notifyDataSetChanged();
                }else {
                   Tool.displayEmpty4Server(getActivity());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                 Tool.backOnFailure(getActivity(),statusCode);
            }
        });
    }

    class MyAnswerAdapter extends MyRecyclerViewAdapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_item, parent, false);
            AnswerViewHolder viewHolder=new AnswerViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AnswerViewHolder mViewHolder= (AnswerViewHolder) holder;

            setCallBack(mViewHolder); // 设置onClick回调

            mViewHolder.tvTimuId.setText(answerList.get(position).getTimuId());
            int color = Color.parseColor("#" + Tool.getRandColorCode(20));
            mViewHolder.tvTimuId.setBackgroundColor(color);

            mViewHolder.tvTimuTitle.setText(answerList.get(position).getTimuTitle());
        }

        @Override
        public int getItemCount() {
            return answerList.size();
        }

        class AnswerViewHolder extends RecyclerView.ViewHolder {
            private CircleTextView tvTimuId;
            private TextView tvTimuTitle;
            public AnswerViewHolder(View itemView) {
                super(itemView);
                tvTimuId= (CircleTextView) itemView.findViewById(R.id.tv_answer_id);
                tvTimuTitle= (TextView) itemView.findViewById(R.id.tv_answer_title);
            }
        }
    }
}
