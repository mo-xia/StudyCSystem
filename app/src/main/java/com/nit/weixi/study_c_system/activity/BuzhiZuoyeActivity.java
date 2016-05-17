package com.nit.weixi.study_c_system.activity;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * 布置课堂作业模块
 * Created by weixi on 2016/4/27.
 */
public class BuzhiZuoyeActivity extends ExpandableListActivity implements View.OnClickListener {
    SQLiteDatabase db;
    Cursor cursor;
    TextView tvCheckedNum;
    List<Integer> bzzyList;
    EditText etTime;
    MyExpandableListAdapter adapter;
    String strCurDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_buzhicuoti);
        db = Tool.getDataBase(this);

        //toolbar 相关  ExpandableListActivity好像只能用android.widget.Toolbar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getActionBar().setTitle("布置作业");

        if (db != null) {
            cursor = db.query(MyConstants.TABLE_ZHANGJIE_NAME, null, null, null, null, null, null);
        }
        bzzyList = new ArrayList<Integer>();
        adapter = new MyExpandableListAdapter(cursor, this);
        setListAdapter(adapter);

        TextView finishBZ = (TextView) findViewById(R.id.tv_finishi_bzzy);
        finishBZ.setOnClickListener(this);
        tvCheckedNum = (TextView) findViewById(R.id.tv_timu_checked);
        tvCheckedNum.setText(bzzyList.size() + "题");
        etTime = (EditText) findViewById(R.id.et_time_bzzy);
    }

    /**
     * 返回图标的点击事件是否触发
     * @param item 返回图标item
     * @return 显示
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        String reqTime = etTime.getText().toString();
        if (reqTime.isEmpty()) {
            Toast.makeText(this, "请输入要求完成时间后再操作", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject zyJSON = getJsonFile(reqTime, bzzyList);
            RequestParams params = new RequestParams();
            params.put("bzzy", zyJSON.toString());
            params.put("date",strCurDate);
            RestClient.get(MyConstants.BZZY_URL, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    //成功后刷新页面 notify无效 暂时不做
                    /*isFinish = true;
                    adapter.notifyDataSetChanged();
                    bzzyList.clear();
                    etTime.setText("");
                    tvCheckedNum.setText("0题");
                    isFinish = false;*/
                    Toast.makeText(BuzhiZuoyeActivity.this, "布置作业已完成", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Tool.backOnFailure(BuzhiZuoyeActivity.this,statusCode);
                }
            });
        }
    }

    /**
     * 获得一个作业的json文件
     * @param time 要求完成的时间
     * @param timuList 题目列表
     * @return json
     */
    private JSONObject getJsonFile(String time, List<Integer> timuList) {
        JSONObject zuoye = new JSONObject();
        try {
            zuoye.put("time", time);
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss ",Locale.CHINA);
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            strCurDate = formatter.format(curDate);
            zuoye.put("date",strCurDate);
            JSONArray timuListArr=new JSONArray(timuList);
            zuoye.put("timulist", timuListArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return zuoye;
    }

    /**
     * 自定义一个BaseExpandableListAdapter 继承自CursorTreeAdapter
     * 只需要传入一个cursor文件
     */
    class MyExpandableListAdapter extends CursorTreeAdapter implements CompoundButton.OnCheckedChangeListener {
        public MyExpandableListAdapter(Cursor cursor, Context context) {
            super(cursor, context);
        }

        /**
         * 根据父列表的cursor文件获得子列表的cursor文件
         * @param groupCursor 父cursor
         * @return 子cursor
         */
        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            String zhangjieId = groupCursor.getString(0);
            return db.query(MyConstants.TABLE_TIMU_NAME, new String[]{"_id", "zhubiaoti"}, "zhangjie_id= ?", new String[]{zhangjieId}, null, null, null);
        }

        /**
         * 创建一个父列表页面
         * @param context 上下文
         * @param cursor 父cursor
         * @param isExpanded isExpanded
         * @param parent parent
         * @return view
         */
        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.acty_bzzy_group, parent, false);
        }

        /**
         * 给父列表页面设置信息
         * @param view 父列表
         * @param context 上下文
         * @param cursor 父cursor
         * @param isExpanded isExpanded
         */
        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            TextView zjName = (TextView) view.findViewById(R.id.tv_title_zj_bzzy);
            TextView timuNum = (TextView) view.findViewById(R.id.tv_num_zj_bzzy);
            timuNum.setText(cursor.getString(1));
            zjName.setText(cursor.getString(2));
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.acty_bzzy_child, parent, false);
            return view;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            CheckBox cbTitle = (CheckBox) view.findViewById(R.id.cb_timu_bzzy);
            cbTitle.setOnCheckedChangeListener(this);
            cbTitle.setText(cursor.getString(1));
            cbTitle.setTag(cursor.getInt(0)); //把题目id设置给checkbox的tag
        }

        /**
         * 当checkBox状态改变时响应
         * 从未选中到选中时添加到作业列表里
         * 从选中到未选中时根据位置移除
         * @param buttonView checkbox
         * @param isChecked 是否被选中
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int timuId = (int) buttonView.getTag(); //取出题目id
            if (isChecked) {
                bzzyList.add(timuId);
            } else { //根据题目id判断它的所在位置，然后再移除
                int pos = bzzyList.indexOf(timuId);
                bzzyList.remove(pos);
            }
            tvCheckedNum.setText(bzzyList.size() + "题"); //刷新界面显示
        }
    }
}
