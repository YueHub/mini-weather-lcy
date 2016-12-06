package cn.edu.pku.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Yue on 2016/11/15.
 */
public class AutoUpdateService extends Service {

    public static final String TAG = "AutoUpdateService";

    private AutoUpdateBinder autoUpdateBinder = new AutoUpdateBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return autoUpdateBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
//                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
//                while(true) {
//                    Log.d("Service", "This is a Service");
//                    Looper.prepare();
//                    //.queryWeatherCode(cityCode);
//                    Looper.loop();
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
