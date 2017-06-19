package cn.edu.pku.model;

/**
 * Created by Yue on 2016/9/27.
 */
public class City {

    /**
     * 身份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 代号
     */
    private String number;

    /**
     * 拼音
     */
    private String allPY;

    /**
     * 所有首字母拼音组合
     */
    private String allFirstPY;

    /**
     * 第一个首字母
     */
    private String firstPY;

    public City(String province, String city, String number, String firstPY, String allPY, String allFirstPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFirstPY() {
        return allFirstPY;
    }

    public void setAllFirstPY(String allFirstPY) {
        this.allFirstPY = allFirstPY;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }
}
