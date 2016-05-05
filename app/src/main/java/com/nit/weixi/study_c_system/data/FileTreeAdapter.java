package com.nit.weixi.study_c_system.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.tools.DownUtils;

import java.util.List;

public class FileTreeAdapter extends BaseAdapter {
    List<FileTreeEntity> data;
    Context context;

    public FileTreeAdapter(Context context, List<FileTreeEntity> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_filetree, null);
            h = new ViewHolder(convertView);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        FileTreeEntity item = data.get(position);
        h.title.setText(DownUtils.getFileNameWithouExtAndNumber(item.getRname()));
        // 根据目录信息的模式给条目的icon设置不同的图标
        if (item.getMode().equals(FileTreeEntity.MODE_FILE)) {
            h.icon.setBackgroundResource(R.drawable.ic_icon_file); // 文件
        } else {
            h.icon.setBackgroundResource(R.drawable.ic_icon_document); // 文件夹
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(View view) {
            title= (TextView) view.findViewById(R.id.tv_title);
            icon= (ImageView) view.findViewById(R.id.iv_icon);
        }
    }

}
