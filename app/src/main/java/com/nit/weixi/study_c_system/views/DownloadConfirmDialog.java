package com.nit.weixi.study_c_system.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * 下载/更新确认框
 */
public class DownloadConfirmDialog extends AlertDialog {
    private String lang;
    private String msg_format = "准备下载 %s 数据包,这会消耗你的数据流量";

    public DownloadConfirmDialog(Context context) {
        super(context);
        this.setTitle("提醒");
        this.setCanceledOnTouchOutside(false);

        this.setCancleListener(new OnClickListener() {

            @Override public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }

    public void setConfirmClickListener(OnClickListener confirmListener) {
        this.setButton(BUTTON_POSITIVE, "下载", confirmListener);
    }

    public void setCancleListener(OnClickListener cancleListener) {
        this.setButton(BUTTON_NEGATIVE, "取消", cancleListener);
    }

    public void show(String lang) {
        this.lang = lang;
        this.setMessage(String.format(msg_format, lang));
        super.show();
    }

    public String getLang() {
        return lang;
    }

}
