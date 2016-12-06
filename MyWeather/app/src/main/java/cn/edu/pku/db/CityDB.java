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
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> cities = new ArrayList<City>();
        Cursor c =db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
        while(c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            City cityItem = new City(province, city, number, firstPY, allPY, allFirstPY);
            cities.add(cityItem);
        }
        return cities;
    }
}
