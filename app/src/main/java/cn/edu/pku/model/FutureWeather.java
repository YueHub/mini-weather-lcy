package cn.edu.pku.model;

/**
 * Created by Yue on 2016/12/10.
 */
public class FutureWeather {

    /**
     * 日期
     */
    private String date;

    /**
     * 最高气温
     */
    private String high;

    /**
     * 最低气温
     */
    private String low;

    /**
     * 白天的天气状况
     */
    private String dayType;

    /**
     * 白天的风向
     */
    private String dayFengXiang;

    /**
     * 白天的风力
     */
    private String dayFengLi;

    /**
     * 晚上的天气状况
     */
    private String nightType;

    /**
     * 晚上的风向
     */
    private String nightFengXiang;

    /**
     * 晚上的风力
     */
    private String nightFengLi;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public String getDayFengXiang() {
        return dayFengXiang;
    }

    public void setDayFengXiang(String dayFengXiang) {
        this.dayFengXiang = dayFengXiang;
    }

    public String getDayFengLi() {
        return dayFengLi;
    }

    public void setDayFengLi(String dayFengLi) {
        this.dayFengLi = dayFengLi;
    }

    public String getNightType() {
        return nightType;
    }

    public void setNightType(String nightType) {
        this.nightType = nightType;
    }

    public String getNightFengXiang() {
        return nightFengXiang;
    }

    public void setNightFengXiang(String nightFengXiang) {
        this.nightFengXiang = nightFengXiang;
    }

    public String getNightFengLi() {
        return nightFengLi;
    }

    public void setNightFengLi(String nightFengLi) {
        this.nightFengLi = nightFengLi;
    }
}
