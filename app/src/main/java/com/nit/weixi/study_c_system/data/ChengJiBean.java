package com.nit.weixi.study_c_system.data;

import com.google.gson.Gson;

/**
 * Created by weixi on 2016/4/29.
 */
public class ChengJiBean {

    private String stuName;
    private String stuNum;
    private String stuFen;

    public static ChengJiBean create(String json){
        return new Gson().fromJson(json, ChengJiBean.class);
    }

    @Override
    public String toString() {
        return "ChengJiBean{" +
                "stuName='" + stuName + '\'' +
                ", stuNum='" + stuNum + '\'' +
                ", stuFen='" + stuFen + '\'' +
                '}';
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getStuFen() {
        return stuFen;
    }

    public void setStuFen(String stuFen) {
        this.stuFen = stuFen;
    }
}
