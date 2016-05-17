package com.nit.weixi.study_c_system.tools;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by weixi on 2016/5/12.
 */
public class MDFilesFromAssets {

    public static void writeMDFile2Storage(Context context,String fileName){
        String rootPath = DownUtils.getRootPath(context);
        File fileDir=new File(rootPath+"/setting");
        if (!fileDir.exists()){
            fileDir.mkdir();
            Log.d("mkdir","创建成功");
        }
        File mdFile=new File(fileDir,fileName);
        if (mdFile.exists()){
            return;
        }
        try {
            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos=new FileOutputStream(mdFile);
            byte[] buffer=new byte[1024];
            int len=-1;
            while ((len=is.read(buffer))!=-1){
                fos.write(buffer,0,len);
                fos.flush();
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
