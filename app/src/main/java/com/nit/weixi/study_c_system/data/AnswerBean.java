package com.nit.weixi.study_c_system.data;

/**
 * Created by weixi on 2016/5/9.
 */
public class AnswerBean {


    /**
     * timuId : 题目id
     * timuTitle : 题目标题
     * answerText : 答疑文本
     */

    private String timuId;
    private String timuTitle;
    private String answerText;

    public String getTimuId() {
        return timuId;
    }

    public void setTimuId(String timuId) {
        this.timuId = timuId;
    }

    public String getTimuTitle() {
        return timuTitle;
    }

    public void setTimuTitle(String timuTitle) {
        this.timuTitle = timuTitle;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
