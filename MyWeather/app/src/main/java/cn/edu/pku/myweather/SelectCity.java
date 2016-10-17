package cn.edu.pku.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import cn.edu.pku.app.MyApplication;
import cn.edu.pku.bean.City;

/**
 * Created by Yue on 2016/9/27.
 */
public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private List<City> mCityList;// 城市列表项


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        // 城市列表
        MyApplication myApplication = (MyApplication)getApplication();
        mCityList = myApplication.getmCityList();
        CityAdapter cityAdapter = new CityAdapter(SelectCity.this, R.layout.city_item, myApplication.getmCityList());
        ListView listView = (ListView) findViewById(R.id.city_list);
        listView.setAdapter(cityAdapter);
        // 为城市设置列表项单击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = mCityList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.title_back :
                Intent intent = new Intent();
                intent.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
