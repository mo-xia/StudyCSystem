package com.nit.weixi.study_c_system.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by weixi on 2016/5/8.
 */
public abstract class MyRecyclerViewAdapter extends RecyclerView.Adapter{

    private OnItemClickLitener mOnItemClickListener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    protected void setCallBack(final RecyclerView.ViewHolder holder){
        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView,holder.getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
