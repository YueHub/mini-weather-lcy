package cn.edu.pku.dto;

/**
 * Created by Yue on 2016/12/21.
 */
public class WeatherDTO {

    /**
     * 当前城市的名称
     */
    private String currentCityName;

    /**
     * 当前城市的代码
     */
    private String currentCityCode;

    private volatile static WeatherDTO singleInstance;

    private WeatherDTO(){}

    public static WeatherDTO getSingleInstance() {
        if(singleInstance == null) {
            synchronized (WeatherDTO.class) {
                if(singleInstance == null) {
                    singleInstance = new WeatherDTO();
                }
            }
        }
        return singleInstance;
    }

    public String getCurrentCityName() {
        return currentCityName;
    }

    public void setCurrentCityName(String currentCityName) {
        this.currentCityName = currentCityName;
    }

    public String getCurrentCityCode() {
        return currentCityCode;
    }

    public void setCurrentCityCode(String currentCityCode) {
        this.currentCityCode = currentCityCode;
    }
}
