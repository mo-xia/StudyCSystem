package com.nit.weixi.study_c_system.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.data.TiMuBean;
import com.nit.weixi.study_c_system.data.ZjCursorAdapter;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by weixi on 2016/3/31.
 */
public class ZhangJieActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView lv;
    Context zja;
    TextView tishu;
    TextView wancheng;
    ZjCursorAdapter adapter;
    TextView startTimu;
    MyHandler myHandler;
    int zhangjieId=0;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                Cursor myCursor= (Cursor) msg.obj;
                tishu.setText(myCursor.getCount() + "");

                adapter=new ZjCursorAdapter(zja,myCursor);
                String wanchengNum = adapter.getWanchengNum(myCursor)+"";
                wancheng.setText(wanchengNum);

                lv.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhangjie);
        zhangjieId = init();
        myHandler=new MyHandler();
        obtainData();
        zja=this;
    }

    private int init() {
        tishu= (TextView) findViewById(R.id.tv_zj_zongtishu);
        wancheng= (TextView) findViewById(R.id.tv_zj_yiwancheng);
        lv= (ListView) findViewById(R.id.lv_zhangjie);
        startTimu= (TextView) findViewById(R.id.start_timu);
        startTimu.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("position");
        int dijizhang=pos+1;
        ActionBar actionBar = getSupportActionBar();
        String title="第 " + dijizhang + " 章";
        if (actionBar!=null) {
            actionBar.setTitle(title);
        }
        return pos;
    }

    private void obtainData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = Tool.getDataBase(zja);
                Cursor cursor = db.query(MyConstants.TABLE_TIMU, new String[]{"_id","zhubiaoti"}, "zhangjie_id=?",
                        new String[]{zhangjieId + ""}, null, null, null);
                //Tool.printCursor(cursor);
                Message msg=Message.obtain();
                msg.obj=cursor;
                msg.what=1;
                myHandler.sendMessage(msg);
            }
        }).start();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(this,TimuActivity.class);
        String tag="training";
        intent.putExtra("tag",tag);
        intent.putExtra("position",position);
        intent.putExtra("zhangjieId",zhangjieId);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this,TimuActivity.class);
        String tag="training";
        intent.putExtra("tag",tag);
        intent.putExtra("position",0);
        intent.putExtra("zhangjieId",zhangjieId);
        startActivity(intent);
    }
}
