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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.pku.adapter.SortAdapter;
import cn.edu.pku.app.MyApplication;
import cn.edu.pku.model.City;
import cn.edu.pku.model.SortModel;
import cn.edu.pku.ui.ClearEditText;
import cn.edu.pku.ui.SideBar;
import cn.edu.pku.util.CharacterParser;
import cn.edu.pku.util.PinyinComparator;

/**
 * Created by Yue on 2016/9/27.
 */
public class SelectCityActivity extends Activity implements View.OnClickListener{

    /**
     * 返回按钮
     */
    private ImageView mBackBtn;

    /**
     * 城市列表
     */
    private List<City> mCityList;

    /**
     * 城市列表UI
     */
    private ListView sortListView;

    /**
     * a-z字母框
     */
    private SideBar sideBar;

    /**
     * 当前所选字母提示框
     */
    private TextView dialog;

    /**
     * 排序适配器
     */
    private SortAdapter adapter;

    /**
     * 清空文本内容的按钮
     */
    private ClearEditText mClearEditText;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    private List<SortModel> sourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        initViews();
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();     // 实例化汉字转拼音类
        pinyinComparator = new PinyinComparator();          // 拼音排序

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.city_list);
       /* sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });*/

        /**** 返回 ****/
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel sortCity = sourceDateList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cityCode", sortCity.getCode());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        /**** 返回 ****/

        MyApplication myApplication = (MyApplication)getApplication();
        mCityList = myApplication.getmCityList();
//        String[] cities = new String[mCityList.size()];
//        int i = 0;
//        for(City city : mCityList) {
//            cities[i++] = city.getCity();
//        }

        sourceDateList = filledData(mCityList);

        // 根据a-z进行排序源数据
        Collections.sort(sourceDateList, pinyinComparator); // 排序
        adapter = new SortAdapter(this, sourceDateList);
        sortListView.setAdapter(adapter);   // 更新排序后的listView

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
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
     * @param mCityList
     * @return
     */
    private List<SortModel> filledData(List<City> mCityList){
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(City city : mCityList) {
            SortModel sortModel = new SortModel();
            sortModel.setName(city.getCity()); // 设置名称
            sortModel.setCode(city.getNumber()); // 设置城市代码
            String pinyin = characterParser.getSelling(city.getCity()); // 汉字转换成拼音
            String sortString = pinyin.substring(0, 1).toUpperCase(); // 取出首字母
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase()); // 设置首字母
            }else{
                sortModel.setSortLetters("#");  // 设置首字母
            }
            mSortList.add(sortModel);   // 添加到列表中
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
            filterDateList = sourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : sourceDateList){
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
