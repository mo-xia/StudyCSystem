package com.nit.weixi.study_c_system.tools;

import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nit.weixi.study_c_system.data.TiMuBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 题目相关工具
 * Created by weixi on 2016/5/6.
 */
public class TimuUtils {
    /**
     * 设置代码块 若有代码块则显示，没有就不显示
     */
    public static void setDaimakuai(TiMuBean timu, TextView daimakuai) {
        if (timu.getDaimakuai() != null) {
            daimakuai.setVisibility(View.VISIBLE);
            String tempCode = timu.getDaimakuai(); // 获得代码块
            String[] split = tempCode.split("##"); //把代码块用“##”分开
            //加入换行符
            StringBuilder sb = new StringBuilder();
            for (String aSplit : split) {
                String sub = aSplit + "\n";
                //System.out.println(sub);
                sb.append(sub);
            }
            //System.out.println("sb: "+sb.toString());
            //显示格式化之后的代码块
            daimakuai.setText(sb.toString());
        }
    }

    /**
     * 把某个list中的数据写到文件中去
     * @param context 上下文
     * @param fileName 文件的名字
     * @param list 某个list
     */
    public static void writeFileFromList(Context context, String fileName, List list){
        String path = DownUtils.getRootPath(context);
        File file = new File(path, fileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            for (int i = 0; i < list.size(); i++) {
                bw.write(list.get(i) + "\r");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.clear();
    }

    /**
     * 获得正确答案的Select选项id
     * @param timu 题目对象
     * @param selectAId 选项A
     * @param selectBId 选项B
     * @param selectCId 选项C
     * @param selectDId 选项D
     * @return 正确答案id
     */
    public static int getDaanId(TiMuBean timu,int selectAId, int selectBId, int selectCId, int selectDId){
        //获得正确答案的id
        String daanStr = timu.getDaan();
        switch (daanStr) {
            case "A":
                return selectAId;
            case "B":
                return selectBId;
            case "C":
                return selectCId;
            case "D":
                return selectDId;
            default:
                return selectAId;
        }
    }
}
