package cn.edu.pku.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.pku.adapter.CityAdapter;
import cn.edu.pku.adapter.SortAdapter;
import cn.edu.pku.app.MyApplication;
import cn.edu.pku.model.City;
import cn.edu.pku.model.SortModel;
import cn.edu.pku.ui.ClearEditText;
import cn.edu.pku.ui.SideBar;
import cn.edu.pku.util.CharacterParser;

/**
 * Created by Yue on 2016/9/27.
 */
public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private List<City> mCityList;// 城市列表项

    /***搜索框***/
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /***搜索框***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initViews();
//        // 城市列表
//        MyApplication myApplication = (MyApplication)getApplication();
//        mCityList = myApplication.getmCityList();
//        CityAdapter cityAdapter = new CityAdapter(SelectCity.this, R.layout.city_item, mCityList);
//        ListView listView = (ListView) findViewById(R.id.city_list);
//        listView.setAdapter(cityAdapter);
//        // 为城市设置列表项单击事件
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                City city = mCityList.get(position);
//                Intent intent = new Intent();
//                intent.putExtra("cityCode", city.getNumber());
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }

    /*******搜索框********/
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.city_list);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        /****返回****/
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = mCityList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", city.getNumber());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        /****返回****/

        MyApplication myApplication = (MyApplication)getApplication();
        mCityList = myApplication.getmCityList();
        String[] cities = new String[mCityList.size()];
        int i = 0;
        for(City city : mCityList) {
            cities[i++] = city.getCity();
        }

        SourceDateList = filledData(cities);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    /*******搜索框********/

    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<SortModel> filledData(String [] date){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<date.length; i++){
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.title_back :
                Intent intent = new Intent();
                // 方法一：从数据库中查询目前所选择的城市
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
                intent.putExtra("cityCode", cityCode);
                // 方法二：直接注释掉intent.putExtra方法

                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
