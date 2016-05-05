package com.nit.weixi.study_c_system.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;

/**
 * Created by weixi on 2016/3/30.
 */
public class MyCursorAdapter extends CursorAdapter {

    TextView tv;

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view=View.inflate(context, R.layout.training_list_item,null);
        tv= (TextView) view.findViewById(R.id.tv_training_item);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String name = cursor.getString(1);
        //System.out.println(name);
        tv.setText(name);
    }


}
