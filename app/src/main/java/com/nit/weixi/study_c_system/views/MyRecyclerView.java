package com.nit.weixi.study_c_system.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by weixi on 2016/4/4.
 */
public class MyRecyclerView extends RecyclerView {

    public boolean isTouch=false;

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isTouch){
            return super.onTouchEvent(e);
        }else {
            return false;
        }

    }

    public MyRecyclerView(Context context) {
        super(context);
    }
}