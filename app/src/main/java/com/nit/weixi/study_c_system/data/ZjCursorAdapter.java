package com.nit.weixi.study_c_system.data;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by weixi on 2016/4/2.
 */
public class ZjCursorAdapter extends CursorAdapter {

    HashSet<String> cuotiSet;
    HashSet<String> zhengqueSet;

    public ZjCursorAdapter(Context context, Cursor c) {
        super(context, c);
        cuotiSet = new HashSet<>();
        zhengqueSet = new HashSet<>();
        Tool.getSet(context,cuotiSet,zhengqueSet);
    }

    public  int getWanchengNum(Cursor cursor) {
        HashSet<String> wanchengSet = new HashSet<>();
        wanchengSet.addAll(cuotiSet);
        wanchengSet.addAll(zhengqueSet);
        int num=0; //该章节中已完成的题数，初始为0
        while (cursor.moveToNext()){
            int timuId = cursor.getInt(0); //该章节拥有的题目的题号
            if (wanchengSet.contains(timuId+"")){
                num++;
            }
        }
        return num;
    }

    long itemId;

    @Override
    public long getItemId(int position) {
        itemId = super.getItemId(position) + 1;
        return itemId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.zhangjie_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.llItem = (LinearLayout) view.findViewById(R.id.ll_zj_item);
        holder.iconNormal = (ImageView) view.findViewById(R.id.iv_icon_item_normal);
        holder.iconRight = (ImageView) view.findViewById(R.id.iv_icon_item_right);
        holder.iconWrong = (ImageView) view.findViewById(R.id.iv_icon_item_wrong);
        holder.tihao = (TextView) view.findViewById(R.id.tv_tihao_zj_item);
        holder.title = (TextView) view.findViewById(R.id.tv_title_zj_item);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        int timuId = cursor.getInt(MyConstants.index_timuid);
        if (cuotiSet.contains(timuId + "")) {
            holder.iconNormal.setVisibility(View.GONE);
            holder.iconWrong.setVisibility(View.VISIBLE);
        }
        if (zhengqueSet.contains(timuId + "")) {
            holder.iconNormal.setVisibility(View.GONE);
            holder.iconWrong.setVisibility(View.GONE);
            holder.iconRight.setVisibility(View.VISIBLE);
        }
        String tihao = cursor.getPosition() + 1 + "、";
        holder.tihao.setText(tihao);
        String title = cursor.getString(1);
        holder.title.setText(title);
        //System.out.println(itemId);
        if (itemId % 2 != 0) {
            holder.llItem.setBackgroundResource(R.color.colorZhangjieBg);
        }
        //System.out.println(tihao+title);
    }

    class ViewHolder {
        private LinearLayout llItem;
        private ImageView iconNormal;
        private ImageView iconWrong;
        private ImageView iconRight;

        private TextView tihao;
        private TextView title;
    }


}
