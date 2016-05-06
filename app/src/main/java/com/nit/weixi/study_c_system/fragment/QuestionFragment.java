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
import android.widget.AdapterView;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.OneTimuActivity;
import com.nit.weixi.study_c_system.tools.DownUtils;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weixi on 2016/3/30.
 */
public class QuestionFragment extends Fragment {

    List<String> cuotiList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cuotiList= Tool.getListFromFile(getActivity(),"cuoti.txt");
        if (cuotiList.size()==0){
            TextView tv=new TextView(container.getContext());
            tv.setText("现在还没有错题");
            return tv;
        }
        View view = inflater.inflate(R.layout.fg_question, container, false);
        RecyclerView rl= (RecyclerView) view.findViewById(R.id.rl_question);
        RecyclerView.LayoutManager lm=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rl.setLayoutManager(lm);

        MyQuestionAdapter adapter=new MyQuestionAdapter();

        adapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), OneTimuActivity.class);
                intent.putExtra("tihao",cuotiList.get(position));
                startActivity(intent);
            }
        });
        rl.setAdapter(adapter);
        return view;
    }

    class MyQuestionAdapter extends RecyclerView.Adapter{
        Map<String,List<String>> cuotiMap;

        private OnItemClickLitener mOnItemClickListener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
        {
            this.mOnItemClickListener = mOnItemClickLitener;
        }


        public MyQuestionAdapter() {
            cuotiMap= new HashMap<String, List<String>>();
            List<String> timuIdList=new ArrayList<String>();
            List<String> timuZJList=new ArrayList<String>();
            List<String> timuTitleList=new ArrayList<String>();
            String selectStr="(" + cuotiList.toString().substring(1, cuotiList.toString().length() - 1) + ")";
            String selection="_id IN " + selectStr;
            SQLiteDatabase dataBase = Tool.getDataBase(getActivity());
            Cursor timu = dataBase.query("timu", new String[]{"_id", "zhangjie_id","zhubiaoti"}, selection, null, null, null,"_id asc");
            while (timu.moveToNext()){
                timuIdList.add(timu.getString(0));
                timuTitleList.add(timu.getString(2));
                String zhangjieId=timu.getString(1);
                Cursor zhangjie = dataBase.query("zhangjie", new String[]{"name"}, "_id= ?", new String[]{zhangjieId}, null, null, null);
                zhangjie.moveToFirst();
                timuZJList.add(zhangjie.getString(0));
                zhangjie.close();
            }
            timu.close();
            cuotiMap.put("timuzhangjie",timuZJList);
            cuotiMap.put("timuid",timuIdList);
            cuotiMap.put("timutitle",timuTitleList);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyViewHolder mViewHolder= (MyViewHolder) holder;
            if (position>0){
                if (cuotiMap.get("timuzhangjie").get(position).equals(cuotiMap.get("timuzhangjie").get(position-1))){
                    mViewHolder.tvZhangjie.setVisibility(View.GONE);
                }else {
                    mViewHolder.tvZhangjie.setVisibility(View.VISIBLE);

                }
            }
            mViewHolder.tvZhangjie.setText(cuotiMap.get("timuzhangjie").get(position));
            String timuId=cuotiMap.get("timuid").get(position);
            String timuTitle=cuotiMap.get("timutitle").get(position);
            mViewHolder.tvTimuTitle.setText(timuId+"、"+timuTitle);

            //如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mViewHolder.itemView, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return cuotiList.size();
        }

    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvZhangjie;
        TextView tvTimuTitle;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvZhangjie= (TextView) itemView.findViewById(R.id.tv_zhangjie_position);
            tvTimuTitle= (TextView) itemView.findViewById(R.id.tv_timu_position);
        }
    }
}
