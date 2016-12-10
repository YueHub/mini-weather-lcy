package cn.edu.pku.model;

import java.util.List;

/**
 * Created by Yue on 2016/9/27.
 */
public class TodayWeather {

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 更新时间
     */
    private String udpateTime;

    /**
     * 温度
     */
    private String wendu;

    /**
     * 湿度
     */
    private String shidu;

    /**
     * pm2.5数值
     */
    private String pm25;

    /**
     * 空气质量
     */
    private String quality;

    /**
     * 风向
     */
    private String fengxiang;

    /**
     * 风力
     */
    private String fengli;

    /**
     * 日期
     */
    private String date;

    /**
     * 最高温度
     */
    private String high;

    /**
     * 最低温度
     */
    private String low;

    /**
     * 天气（晴朗、雨天..）
     */
    private String type;

    /**
     * 未来几天天气
     */
    private List<FutureWeather> futureWeathers;


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUdpateTime() {
        return udpateTime;
    }

    public void setUdpateTime(String udpateTime) {
        this.udpateTime = udpateTime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FutureWeather> getFutureWeathers() {
        return futureWeathers;
    }

    public void setFutureWeathers(List<FutureWeather> futureWeathers) {
        this.futureWeathers = futureWeathers;
    }
}
