package cn.edu.pku.service;

import android.os.Binder;

import cn.edu.pku.activities.MainActivity;
import cn.edu.pku.dto.WeatherDTO;

/**
 * Created by Yue on 2016/11/15.
 */
public class AutoUpdateBinder extends Binder {

    private WeatherDTO weatherDTO = WeatherDTO.getSingleInstance();

    public void autoUpdate(final MainActivity mainActivity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    // Looper.prepare();
                    mainActivity.queryWeather(weatherDTO.getCurrentCityCode());
                    //  Looper.loop();
                    try {
                        Thread.sleep(900000);  // 每隔15分钟 后台服务自动更新一次天气
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

}
