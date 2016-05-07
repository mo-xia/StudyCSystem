package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.DataAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.OneTimuActivity;
import com.nit.weixi.study_c_system.data.MyRecyclerViewAdapter;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by weixi on 2016/3/30.
 */
public class QuestionFragment extends Fragment {

    List<String> list;
    MyQuestionAdapter adapter;
    Map<String, List<String>> wentiMap;
    String tag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getTag() != null) {
            tag = getTag();
            list = new ArrayList<String>();
            adapter = new MyQuestionAdapter();
            getWentiList();
        } else {
            tag = "";
            list = Tool.getListFromFile(getActivity(), "cuoti.txt");
            getWentiMap();
            if (list.size() == 0) {
                TextView tv = new TextView(container.getContext());
                tv.setText("现在还没有错题");
                return tv;
            }
        }
        adapter = new MyQuestionAdapter();

        View view = inflater.inflate(R.layout.fg_question, container, false);
        RecyclerView rl = (RecyclerView) view.findViewById(R.id.rl_question);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rl.setLayoutManager(lm);
        adapter.setOnItemClickLitener(new MyRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), OneTimuActivity.class);
                intent.putExtra("tihao", list.get(position));
                intent.putExtra("tag", tag);
                startActivity(intent);
            }
        });
        rl.setAdapter(adapter);
        return view;
    }

    /**
     * 老师答疑时 加载学生提交的问题
     */
    private void getWentiList() {
        RequestParams params = new RequestParams();
        params.put("dayitag", "question");
        RestClient.get("/dayi", params, new DataAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String wentiStr = new String(responseBody).replace("[", "");
                wentiStr = wentiStr.replace("]", "");
                if (!wentiStr.equals("")) {
                    String[] strings = wentiStr.split(",");
                    //list = Arrays.asList(strings);
                    for (String s:strings
                         ) {
                        list.add(s.trim());
                    }
                    getWentiMap();
                    adapter.notifyDataSetChanged();  //获得新的list后更新界面
                }else {
                    System.out.println("list为空");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(getActivity(), statusCode);
            }
        });
    }

    /**
     * 从题目list中获得题目的所属章节和题目标题，并存到map里
     */
    private void getWentiMap() {
        wentiMap = new HashMap<String, List<String>>();
        List<String> timuIdList = new ArrayList<String>();
        List<String> timuZJList = new ArrayList<String>();
        List<String> timuTitleList = new ArrayList<String>();
        String selectStr = "(" + list.toString().substring(1, list.toString().length() - 1) + ")";
        String selection = "_id IN " + selectStr;
        SQLiteDatabase dataBase = Tool.getDataBase(getActivity());
        Cursor timu = dataBase.query("timu", new String[]{"_id", "zhangjie_id", "zhubiaoti"}, selection, null, null, null, "_id asc");
        while (timu.moveToNext()) {
            timuIdList.add(timu.getString(0));
            timuTitleList.add(timu.getString(2));
            String zhangjieId = timu.getString(1);
            Cursor zhangjie = dataBase.query("zhangjie", new String[]{"name"}, "_id= ?", new String[]{zhangjieId}, null, null, null);
            zhangjie.moveToFirst();
            timuZJList.add(zhangjie.getString(0));
            zhangjie.close();
        }
        timu.close();
        wentiMap.put("timuzhangjie", timuZJList);
        wentiMap.put("timuid", timuIdList);
        wentiMap.put("timutitle", timuTitleList);
    }

    class MyQuestionAdapter extends MyRecyclerViewAdapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder mViewHolder = (MyViewHolder) holder;
            if (position > 0) {
                if (wentiMap.get("timuzhangjie").get(position).equals(wentiMap.get("timuzhangjie").get(position - 1))) {
                    mViewHolder.tvZhangjie.setVisibility(View.GONE);
                } else {
                    mViewHolder.tvZhangjie.setVisibility(View.VISIBLE);
                }
            }
            mViewHolder.tvZhangjie.setText(wentiMap.get("timuzhangjie").get(position));
            String timuId = wentiMap.get("timuid").get(position);
            String timuTitle = wentiMap.get("timutitle").get(position);
            mViewHolder.tvTimuTitle.setText(timuId + "、" + timuTitle);
            setCallBack(mViewHolder);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvZhangjie;
        TextView tvTimuTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvZhangjie = (TextView) itemView.findViewById(R.id.tv_zhangjie_position);
            tvTimuTitle = (TextView) itemView.findViewById(R.id.tv_timu_position);
        }
    }
}
