package cn.edu.pku.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.model.City;
import cn.edu.pku.db.CityDB;

/**
 * Created by Yue on 2016/9/27.
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mCityDB = openCityDB();
        initCityList();
    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        if(!db.exists()) {
            String pathFolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathFolder);
            if(!dirFirstFolder.exists()) {
                dirFirstFolder.mkdir();
            }
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private void initCityList() {
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for(City city : mCityList) {
            ++i;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d("TAG", cityCode + ":" + cityName);
        }
        Log.d("TAG", "i="+i);
        return true;
    }

    public List<City> getmCityList() {
        return mCityList;
    }

    public void setmCityList(List<City> mCityList) {
        this.mCityList = mCityList;
    }
}
