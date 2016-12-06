package cn.edu.pku.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Looper;
import android.util.Log;

import cn.edu.pku.activities.MainActivity;

/**
 * Created by Yue on 2016/11/15.
 */
public class AutoUpdateBinder extends Binder {

    public void autoUpdate(final MainActivity mainActivity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    Log.d("Service", "This is a Service");
                    // Looper.prepare();
                    mainActivity.queryWeatherCode("101010100");
                    //  Looper.loop();
                    try {
                        Thread.sleep(5000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

}
