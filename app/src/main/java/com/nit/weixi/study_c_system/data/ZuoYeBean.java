package com.nit.weixi.study_c_system.data;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by weixi on 2016/4/28.
 */
public class ZuoYeBean {

    private String time;
    private String date;
    private List<Integer> timulist;

   public static ZuoYeBean create(String json){
       return new Gson().fromJson(json, ZuoYeBean.class);
   }

    @Override
    public String toString() {
        return "ZuoYeBean{" +
                "time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", timulist=" + timulist +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Integer> getTimulist() {
        return timulist;
    }

    public void setTimulist(List<Integer> timulist) {
        this.timulist = timulist;
    }
}
