package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前学生的成绩记录
 * Created by weixi on 2016/3/30.
 */
public class RecordFragment extends Fragment{

    List<String> chengjiList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chengjiList= Tool.getListFromFile(getActivity(), MyConstants.CHENGJI_FILE_NAME);
        if (chengjiList.size()==0){
            TextView tv=new TextView(container.getContext());
            tv.setText("现在还没有成绩");
            return tv;
        }
        View view=inflater.inflate(R.layout.fg_record,container,false);
        RecyclerView rl= (RecyclerView) view.findViewById(R.id.rl_record);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rl.setLayoutManager(layoutManager);
        MyRecordAdapter adapter=new MyRecordAdapter();
        rl.setAdapter(adapter);
        return view;
    }

    class MyRecordAdapter extends RecyclerView.Adapter{

        public MyRecordAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder mViewHolder = (MyViewHolder) holder;
            String chengjiStr=chengjiList.get(position);
            String[] strings = chengjiStr.split("##");
            String chengji=strings[0];
            String date=strings[1];
            mViewHolder.tvChengji.setText(chengji);
            mViewHolder.tvdate.setText(date);
        }

        @Override
        public int getItemCount() {
            return chengjiList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvChengji;
        TextView tvdate;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvChengji= (TextView) itemView.findViewById(R.id.tv_chengji_record);
            tvdate= (TextView) itemView.findViewById(R.id.tv_date_record);
        }
    }
}
