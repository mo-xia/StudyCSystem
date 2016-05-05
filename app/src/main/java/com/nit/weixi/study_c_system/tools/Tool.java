package com.nit.weixi.study_c_system.tools;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.nit.weixi.study_c_system.activity.BuzhiCuotiActivity;
import com.nit.weixi.study_c_system.data.MySqliteHelper;
import com.nit.weixi.study_c_system.data.TiMuBean;
import com.nit.weixi.study_c_system.fragment.FragmentFactory;
import com.nit.weixi.study_c_system.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by weixi on 2016/3/29.
 */
public class Tool {

    /**
     * 下拉刷新
     *
     * @param swipeRefreshLayout 刷新控件
     * @param isRefreshing       是否刷新
     */
    public static void setRefreshing(
            final SwipeRefreshLayout swipeRefreshLayout,
            final boolean isRefreshing) {
        if (swipeRefreshLayout == null)
            return;
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }


    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     *
     * @param source
     * @param descDir
     */
    public static void unZipFiles(File source, String descDir) throws IOException {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(descDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得正确和错题集合
     * @param context 上下文
     * @param cuotiSet 错题集合
     * @param zhengqueSet  正确集合
     */
    public static void getSet(Context context, HashSet<String> cuotiSet, HashSet<String> zhengqueSet) {
        File cuotiFile = new File(DownUtils.getRootPath(context), "cuoti.txt");
        File zhengqueFile = new File(DownUtils.getRootPath(context), "zhengque.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(cuotiFile));
            BufferedReader br1 = new BufferedReader(new FileReader(zhengqueFile));
            boolean hasRead = true;
            while (hasRead) {
                String cuoti = br.readLine();
                // System.out.println("cuoti: " + cuoti);
                if (cuoti == null) {
                    hasRead = false;
                } else {
                    cuotiSet.add(cuoti);
                }
            }
            boolean hasRead1 = true;
            while (hasRead1) {
                String zhengque = br1.readLine();
                // System.out.println("zhengque: " + zhengque);
                if (zhengque == null) {
                    hasRead1 = false;
                } else {
                    zhengqueSet.add(zhengque);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<String> getListFromFile(Context context,String name){
        List<String> myList=new ArrayList<String>();
        String path= DownUtils.getRootPath(context);
        File file=new File(path,name);
        try {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String read;
            while ((read=br.readLine())!=null) {
                myList.add(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myList;
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 得到查询数据库的参数
     *
     * @return 一个题目数的集合 符合sql语法的字符串
     */
    public static String getSelectionArgs(String[] randTimu) {
        String selectionStr = "(";
        for (int i = 0; i < randTimu.length; i++) {
            if (i != randTimu.length - 1) {
                selectionStr += randTimu[i] + ",";
            } else {
                selectionStr += randTimu[i] + ")";
            }
        }
        return selectionStr;
    }

    /**
     * 生成一个用户输入长度的随机题目数组
     *
     * @param context 上下文
     * @param tishu   用户输入的长度
     * @return 题目数组
     */
    public static String[] getRandTimu(Context context, int tishu) {
        int timuMax = getTimuCount(context) - 1;
        HashSet<String> randTimuSet = new HashSet<String>();
        while (randTimuSet.size() < tishu) {
            String timu = getRandInt(timuMax) + "";
            randTimuSet.add(timu);
            //System.out.println("timu: "+timu);
        }
        return randTimuSet.toArray(new String[tishu - 1]);
    }

    /**
     * 生成一个随机数
     *
     * @param count 最大值
     * @return
     */
    public static int getRandInt(int count) {
        Random myRandom = new Random();
        return myRandom.nextInt(count);
    }

    /**
     * 获取当前数据库中所有的题目数量
     *
     * @param context 当前上下文
     * @return 题目总数
     */
    public static int getTimuCount(Context context) {
        SQLiteDatabase db = getDataBase(context);
        Cursor cursor = db.query(MyConstants.TABLE_TIMU, new String[]{"_id"}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * 获取数据库
     *
     * @param context 当前应用的上下文
     * @return 当前数据库
     */
    public static SQLiteDatabase getDataBase(Context context) {
        MySqliteHelper helper = new MySqliteHelper(context, MyConstants.dbName, null, 1);
        return helper.getWritableDatabase();
    }

    /**
     * 设置一个fragment
     *
     * @param activity 该fragment依附于那个activity
     * @param fg_name  该fragment的标记
     */
    public static void setFragment(Activity activity, String fg_name) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = FragmentFactory.createFragment(fg_name);
        ft.replace(R.id.re_fragment, fragment).commit();
        String title=getTitleFromTag(fg_name);
        if (title!=null){
            activity.setTitle(title);
        }
    }

    /**
     * 请求网络失败时返回
     * @param context 上下文
     * @param statusCode  状态码
     */
    public static void backOnFailure(Context context,int statusCode) {
        if (statusCode == 404) {
            //404 说明申请的severlet不存在
            Toast.makeText(context, "该功能暂未实现", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "无法连接到网络，请联网后再操作", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getFlieNameFromDate(){
        SimpleDateFormat timesdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String FileTime = timesdf.format(new Date());//获取系统时间
        return FileTime.replace(":", "");
    }

    public static String getTitleFromTag(String tag) {
        switch (tag) {
            case MyConstants.FRAGMENT_TRACK_RECORD:
                return MyConstants.TITLE_RECORD;
            case MyConstants.FRAGMENT_WRONG_QUESTION:
                return MyConstants.TITLE_QUESTION;
            case MyConstants.FRAGMENT_TEACHER_ANSWER:
                return MyConstants.TITLE_ANSWER;
            case MyConstants.FRAGMENT_SETTING_HELP:
                return MyConstants.TITLE_SETTING;
            case MyConstants.FRAGMENT_HOME:
                return MyConstants.TITLE_APP;
        }
        return null;
    }

    /**
     * 转换图片成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 获取当前活动的名字
     *
     * @param context 当前上下文
     * @return 当前的Activity
     */
    public static String getRunningActivityName(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    /**
     * 将指定bitmap存入应用的data路径下
     * @param context 上下文
     * @param bitmap 指定的bitmap
     */
    public static void saveBitmap(Context context,Bitmap bitmap){
        String path=DownUtils.getRootPath(context);
        String fileName="icon.png";
        File file=new File(path,fileName);
        try {
            FileOutputStream fos=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromFile(Context context,String fileName){
        String path = DownUtils.getRootPath(context);
        File file=new File(path,fileName);
        try {
            FileInputStream fis=new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得一个sharedPreference的editor对象
     * @param context 上下文
     * @param name sp的对象
     * @return editor对象
     */
    public static SharedPreferences.Editor getEditor(Context context, String name){
        SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return sp.edit();
    }

    /**
     * 讲查询到的题目封装成timubean对象
     *
     * @param cursor 查询得到的cursor
     * @return 一个timu的list集合
     */
    public static ArrayList<TiMuBean> setTiMuBean(Cursor cursor) {
        ArrayList<TiMuBean> timuList = new ArrayList<TiMuBean>();
        while (cursor.moveToNext()) {
            int tiHao = cursor.getInt(MyConstants.index_timuid);
            String zhubiaoti = cursor.getString(MyConstants.index_zhubiaoti);
            String daimakuai = cursor.getString(MyConstants.index_daimakuai);
            String selectA = cursor.getString(MyConstants.index_selectA);
            String selectB = cursor.getString(MyConstants.index_selectB);
            String selectC = cursor.getString(MyConstants.index_selectC);
            String selectD = cursor.getString(MyConstants.index_selectD);
            String daAn = cursor.getString(MyConstants.index_daan);
            int pinglunId = cursor.getInt(MyConstants.index_pinglun_id);
            TiMuBean tiMu = new TiMuBean(tiHao, pinglunId, zhubiaoti, daimakuai,
                    selectA, selectB, selectC, selectD, daAn);
            timuList.add(tiMu);
        }
        cursor.close();
        return timuList;
    }

    public static void printCursor(Cursor cursor) {
        while (cursor.moveToNext()) {
            int position = cursor.getPosition();
            int count = cursor.getColumnCount();
            System.out.println("当前是第 " + position + " 行");
            String[] columnName = new String[count];
            String[] columnValue = new String[count];
            for (int i = 0; i < count; i++) {
                columnName[i] = cursor.getColumnName(i);
                columnValue[i] = cursor.getString(i);
                System.out.println(columnName[i] + ": " + columnValue[i]);
            }
        }
        cursor.close();
    }
}
