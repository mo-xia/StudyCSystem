package com.nit.weixi.study_c_system.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by weixi on 2016/5/10.
 */
public class UpdateTikuDialog extends AlertDialog{
    UpdateTikuInterface mUpdateTikuInterface;
    public void setUpdateTikuInterface(UpdateTikuInterface mUpdateTikuInterface){
        this.mUpdateTikuInterface=mUpdateTikuInterface;
    }
    public UpdateTikuDialog(Context context) {
        super(context);
        this.setMessage("一旦更新完题库，为避免冲突，同题库相关的所有已存文件将全部清除。");
        this.setCanceledOnTouchOutside(false);
        this.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        this.setButton(AlertDialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpdateTikuInterface.doUpdate();
            }
        });
    }

    public interface UpdateTikuInterface{
        void doUpdate();
    }
}
