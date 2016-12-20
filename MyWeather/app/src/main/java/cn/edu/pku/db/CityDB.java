package cn.edu.pku.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.model.City;

/**
 * Created by Yue on 2016/9/27.
 */
public class CityDB {

    /**
     * 数据库名称
     */
    public static final String CITY_DB_NAME = "city.db";

    /**
     * 表名称
     */
    private static final String CITY_TABLE_NAME = "city";

    /**
     * SQLiteDatabase Object
     */
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    /**
     * 获取所有的城市列表
     * @return
     */
    public List<City> getAllCity() {
        List<City> cities = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while(c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));    // 省份
            String city = c.getString(c.getColumnIndex("city"));    // 城市
            String number = c.getString(c.getColumnIndex("number"));    // 代号
            String allPY = c.getString(c.getColumnIndex("allpy"));  // 拼音
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));    // 所有首字母拼音组合
            String firstPY = c.getString(c.getColumnIndex("firstpy"));  // 第一个首字母
            City cityItem = new City(province, city, number, firstPY, allPY, allFirstPY);
            cities.add(cityItem);
        }
        return cities;
    }
}
