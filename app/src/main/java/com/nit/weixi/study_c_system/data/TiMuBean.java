package com.nit.weixi.study_c_system.data;

/**
 * Created by weixi on 2016/3/31.
 */
public class TiMuBean {
    private int tihao;
    private int pinglun;
    private String zhubiaoti;
    private String daimakuai;
    private String selectA;
    private String selectB;
    private String selectC;
    private String selectD;
    private String daan;

    public TiMuBean(int tihao, int pinglun, String zhubiaoti,
                    String daimakuai, String selectA,
                    String selectB, String selectC,
                    String selectD, String daan) {
        this.tihao = tihao;
        this.pinglun = pinglun;
        this.zhubiaoti = zhubiaoti;
        this.daimakuai = daimakuai;
        this.selectA = selectA;
        this.selectB = selectB;
        this.selectC = selectC;
        this.selectD = selectD;
        this.daan = daan;
    }

    public int getTihao() {
        return tihao;
    }

    public void setTihao(int tihao) {
        this.tihao = tihao;
    }

    public int getPinglun() {
        return pinglun;
    }

    public void setPinglun(int pinglun) {
        this.pinglun = pinglun;
    }

    public String getZhubiaoti() {
        return zhubiaoti;
    }

    public void setZhubiaoti(String zhubiaoti) {
        this.zhubiaoti = zhubiaoti;
    }

    public String getDaimakuai() {
        return daimakuai;
    }

    public void setDaimakuai(String daimakuai) {
        this.daimakuai = daimakuai;
    }

    public String getSelectA() {
        return selectA;
    }

    public void setSelectA(String selectA) {
        this.selectA = selectA;
    }

    public String getSelectB() {
        return selectB;
    }

    public void setSelectB(String selectB) {
        this.selectB = selectB;
    }

    public String getSelectC() {
        return selectC;
    }

    public void setSelectC(String selectC) {
        this.selectC = selectC;
    }

    public String getSelectD() {
        return selectD;
    }

    public void setSelectD(String selectD) {
        this.selectD = selectD;
    }

    public String getDaan() {
        return daan;
    }

    public void setDaan(String daan) {
        this.daan = daan;
    }

    @Override
    public String toString() {
        return "TiMuBean{" +
                "tihao=" + tihao +
                ", pinglun=" + pinglun +
                ", zhubiaoti='" + zhubiaoti + '\'' +
                ", daimakuai='" + daimakuai + '\'' +
                ", selectA='" + selectA + '\'' +
                ", selectB='" + selectB + '\'' +
                ", selectC='" + selectC + '\'' +
                ", selectD='" + selectD + '\'' +
                ", daan='" + daan + '\'' +
                '}';
    }
}
