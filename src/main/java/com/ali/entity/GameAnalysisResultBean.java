package com.ali.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写数据库是封装实体类
 * 先将entity也就是bean层创建好
 * 然后去创建dao层下面先创建一个相关接口如 iGameAnalysisResult，
 * 然年后在在dao层下创建一个相关的impl的包里面有一个相关的操作类如 GameAnalysisResultImpl
 */
@Data
@NoArgsConstructor
public class GameAnalysisResultBean {
    //基准日
    private String data;
//    新增用户
    private Long newAdduserCnt;
//    活跃用户
    private Long ActiveUserCnt;
    //次日留存率
    private String nextdayRate;
    //二日留存率
    private String twodayRate;
    private String threedayRate;
    private String fourdayRate;
    private String fivedayRate;

    public GameAnalysisResultBean(String data, Long newAdduserCnt, Long activeUserCnt, String nextdayRate, String twodayRate, String threedayRate, String fourdayRate, String fivedayRate) {
        this.data = data;
        this.newAdduserCnt = newAdduserCnt;
        ActiveUserCnt = activeUserCnt;
        this.nextdayRate = nextdayRate;
        this.twodayRate = twodayRate;
        this.threedayRate = threedayRate;
        this.fourdayRate = fourdayRate;
        this.fivedayRate = fivedayRate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getNewAdduserCnt() {
        return newAdduserCnt;
    }

    public void setNewAdduserCnt(Long newAdduserCnt) {
        this.newAdduserCnt = newAdduserCnt;
    }

    public Long getActiveUserCnt() {
        return ActiveUserCnt;
    }

    public void setActiveUserCnt(Long activeUserCnt) {
        ActiveUserCnt = activeUserCnt;
    }

    public String getNextdayRate() {
        return nextdayRate;
    }

    public void setNextdayRate(String nextdayRate) {
        this.nextdayRate = nextdayRate;
    }

    public String getTwodayRate() {
        return twodayRate;
    }

    public void setTwodayRate(String twodayRate) {
        this.twodayRate = twodayRate;
    }

    public String getThreedayRate() {
        return threedayRate;
    }

    public void setThreedayRate(String threedayRate) {
        this.threedayRate = threedayRate;
    }

    public String getFourdayRate() {
        return fourdayRate;
    }

    public void setFourdayRate(String fourdayRate) {
        this.fourdayRate = fourdayRate;
    }

    public String getFivedayRate() {
        return fivedayRate;
    }

    public void setFivedayRate(String fivedayRate) {
        this.fivedayRate = fivedayRate;
    }
}
