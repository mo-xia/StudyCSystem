package com.nit.weixi.study_c_system.tools;

/**
 * 常量
 * Created by weixi on 2016/3/29.
 */
public class MyConstants {

    /**
     * 应用 SharedPreference 的名字
     */
    public static final String LOGIN_SP = "login"; //登陆相关
    public static final String LOGIN_SP_SHENFEN="shenfen"; //登陆身份
    public static final String STUDENT_SP="student"; //学生相关
    public static final String STUDENT_SP_NAME="stuname"; //学生姓名
    public static final String STUDENT_SP_NUMBER="stunum"; //学生学号
    public static final String TIKU_SP="tiku"; //题库相关
    public static final String TIKU_SP_VERSION="tiku"; //题库当前版本
    public static final String TIKU_SP_LASTVERSION="lastversion"; //题库最新版本
    public static final String ZUOYE_SP="zuoye"; //课堂作业相关
    public static final String ZUOYE_SP_FENSHU="fenshu"; //课堂作业相关
    public static final String ZUOYE_SP_LASTFENSHU="lastfenshu"; //课堂作业最后分数

    public static final String STUDENT_ICON_NAME="icon.png"; //学生头像文件的名字

    /**
     * 数据库 表 相关
     */
    public static final String TABLE_ZHANGJIE_NAME = "zhangjie"; //章节表的名字
    public static final String TABLE_TIMU_NAME = "timu"; //题目表的名字
    public static final String DB_NAME = "Study_C_Lau.db"; //数据库名字

    /**
     * 文件名相关
     */
    public static final String CHENGJI_FILE_NAME="chengji.txt"; //成绩文件
    public static final String CUOTI_FILE_NAME="cuoti.txt"; //错题文件
    public static final String TIWEN_FILE_NAME="tiwen.txt"; //提问文件
    public static final String FINISHTIWEN_FILE_NAME="finishtiwen.txt"; //已经提问的文件
    public static final String ZHENGQUE_FILE_NAME="zhengque.txt"; //正确题目的文件

    public static final String OPENSOURCENAME="opensource.md"; //assets开源支持md文件名
    public static final String HELPBOOKNAME="helpbook.md"; //assets 帮助手册md文件名

    /**
     * 网络请求的url
     */
    public static final String UPDATE_URL_UPLOAD="/uploadtiku"; //上传题库
    public static final String UPDATE_URL_DOWNLOAD="/downloadtiku"; //下载题库
    public static final String BZZY_URL="/bzzy"; //布置课堂作业
    public static final String DAYI_URL="/dayi"; //答疑模块
    public static final String TIWEN_URL="/tiwen"; //提问模块
    public static final String LOGIN_URL="/login"; //登陆模块
    public static final String KTZY_URL="/ktzy"; //课堂作业模块
    public static final String CHENGJI_URL="/chengji"; //成绩模块
    public static final String FEEDBACK_URL="/chengji"; //问题反馈模块


    /**
     * fragment 相关
     */
    public static final String FRAGMENT_HOME= "home";
    public static final String TITLE_APP="C语言学习APP";
    public static final String FRAGMENT_TRACK_RECORD= "record";
    public static final String TITLE_RECORD="成绩记录";
    public static final String FRAGMENT_WRONG_QUESTION= "question";
    public static final String TITLE_QUESTION="错题本";
    public static final String FRAGMENT_TEACHER_ANSWER= "answer";
    public static final String TITLE_ANSWER="老师答疑";
    public static final String FRAGMENT_SETTING_HELP= "setting";
    public static final String TITLE_SETTING="设置与帮助";
    public static final String FRAGMENT_DATA= "data";
    public static final String FRAGMENT_TRAINING= "training";
    public static final String FRAGMENT_TEST= "test";
    public static final String FRAGMENT_TASK= "task";

    /**
     * 题目的对应列的索引
     */
    public static final int index_timuid=0;
    public static final int index_zhubiaoti=2;
    public static final int index_daimakuai=3;
    public static final int index_selectA=4;
    public static final int index_selectB=5;
    public static final int index_selectC=6;
    public static final int index_selectD=7;
    public static final int index_daan=8;
    public static final int index_pinglun_id=9;

}
