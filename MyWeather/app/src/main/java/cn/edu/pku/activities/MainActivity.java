package cn.edu.pku.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.adapter.MyViewPagerAdapter;
import cn.edu.pku.model.FutureWeather;
import cn.edu.pku.model.TodayWeather;
import cn.edu.pku.service.AutoUpdateBinder;
import cn.edu.pku.service.AutoUpdateService;
import cn.edu.pku.util.NetUtil;

/**
 * Created by 22253 on 2016/9/18.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * 选择城市按钮
     */
    private ImageView mCityBtn;

    /**
     * 城市定位按钮
     */
    private ImageView locationBtn;

    /**
     * 更新按钮
     */
    private ImageView mUpdateBtn;



    /**
     * 更新进度
     */
    private ProgressBar updateProcess;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv;
    private TextView temperatureTv, climateTv, windTv, wenduTv;

    /**
     * 未来的日期
     */
    private List<TextView> dates;

    /**
     * 未来的温度
     */
    private List<TextView> temperatures;

    /**
     * 未来白天的天气
     */
    private List<TextView> dayTypes;

    /**
     * 未来白天的风力
     */
    private List<TextView> fengLis;

    /**
     * 天气图片
     */
    private ImageView weatherImg, pmImg;

    /**
     * 百度定位客户端
     */
    public LocationClient mLocationClient = null;

    /**
     * 百度定位监听
     */
    public BDLocationListener myListener = new MyLocationListener();

    private static final int UPDATE_TODAY_WEATHER = 1;

    private AutoUpdateBinder autoUpdateBinder;

    private MainActivity mainActivity = this;

    private ViewPager myViewPager;   // ViewPaper
    private MyViewPagerAdapter myViewPagerAdapter;
    private List<View> viewList = new ArrayList<View>();    // ViewPaper的ViewList


    /**
     * 接受子线程数据，配合主线程更新UI
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case UPDATE_TODAY_WEATHER :
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };



    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            autoUpdateBinder = (AutoUpdateBinder) service;

            autoUpdateBinder.autoUpdate(mainActivity);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        // 选择城市
        mCityBtn = (ImageView) findViewById(R.id.title_city_manager);
        mCityBtn.setOnClickListener(this);

        // 城市定位
        locationBtn = (ImageView) findViewById(R.id.title_location);
        locationBtn.setOnClickListener(this);
        mLocationClient = new LocationClient(getApplicationContext());     // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    // 注册监听函数
        initLocation(); // 配置定位参数
        mLocationClient.start();    // 开始定位

        // 更新天气数据
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        updateProcess = (ProgressBar) findViewById(R.id.update_process);    // 按钮更新动画
        //startService(new Intent(getBaseContext(), AutoUpdateService.class));
        Intent bindIntent = new Intent(this, AutoUpdateService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);  // 绑定后台自动更新天气的服务

        // 未来6天天气
        LayoutInflater inflater = LayoutInflater.from(this);
        viewList.add(inflater.inflate(R.layout.next_days_1, null));    // 添加滑动页面列表
        viewList.add(inflater.inflate(R.layout.next_days_2, null));
        myViewPagerAdapter = new MyViewPagerAdapter(viewList);
        myViewPager = (ViewPager) this.findViewById(R.id.mViewPager2);
        myViewPager.setAdapter(myViewPagerAdapter); // 设置适配器

        // 初始化界面
        initView();

    }

    /**
     * 点击事件监听
     * @param view
     */
    @Override
    public void onClick(View view) {
        // 点击城市选择按钮
        if(view.getId() == R.id.title_city_manager) {
            Intent intent = new Intent(this, SelectCity.class);
            startActivityForResult(intent, 1);
        }
        // 天气更新按钮
        if(view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            // 网络连接是否正常
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather-netInfo:", "网络连接正常");
                // 设置更新按钮不可见，旋转按钮可见
                mUpdateBtn.setVisibility(View.INVISIBLE);
                updateProcess.setVisibility(View.VISIBLE);
                queryWeather(cityCode);
                // 设置更新按钮可见，旋转按钮不可见
                mUpdateBtn.setVisibility(View.VISIBLE);
                updateProcess.setVisibility(View.INVISIBLE);
            } else {
                mUpdateBtn.setVisibility(View.INVISIBLE);
                updateProcess.setVisibility(View.VISIBLE);
                Log.d("myWeather-netInfo", "网络连接异常");
                Toast.makeText(this, "网络连接异常,请检查网络", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 配置定位参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 选择城市的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            // 将现在的城市代码存入文件中
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("main_city_code", newCityCode);
            editor.commit();
            Log.d("myWeather-City-Select:" , "选择城市代码为:" + newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather-NetInfo:" , "网络连接正常");
                queryWeather(newCityCode); // **刷新页面
            } else {
                Log.d("myWeather-NetInfo:", "网络连接异常");
                Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 通过城市代码查询天气（包括未来几天的天气）
     * @param cityCode
     */
    public void queryWeather(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null) {
                        response.append(str);
                    }
                    String responseStr = response.toString();
                    TodayWeather todayWeather = parseXML(responseStr);
                    if(todayWeather != null) {
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 解析天气数据XML文件
     * @param xmlData
     * @return
     */
    public TodayWeather parseXML(String xmlData) {
        TodayWeather todayWeather = null;
        int windDirectionCount = 0;  // 风向
        int windForceCount = 0;  // 风力
        int dateCount =  0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().contentEquals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(xmlPullParser.getName().contentEquals("city")) {
                            xmlPullParser.next();
                            todayWeather.setCityName(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("updatetime")) {
                            xmlPullParser.next();
                            todayWeather.setUdpateTime(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("shidu")) {
                            xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("wendu")) {
                            xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("pm25")) {
                            xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("quality")) {
                            xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("fengxiang") && windDirectionCount == 0) {
                            xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            ++windDirectionCount;
                        } else if(xmlPullParser.getName().contentEquals("fengli") && windForceCount == 0) {
                            xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            ++windForceCount;
                        } else if(xmlPullParser.getName().contentEquals("date") && dateCount == 0) {
                            xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            ++dateCount;
                        } else if(xmlPullParser.getName().contentEquals("forecast")) {
                            List<FutureWeather> futureWeathers = new ArrayList<FutureWeather>();
                            // 未来天气
                            xmlPullParser.next();
                            FutureWeather futureWeather = null;
                            for(xmlPullParser.next();xmlPullParser.getName() != null && !xmlPullParser.getName().contentEquals("zhishus");xmlPullParser.next()) {
                                if(xmlPullParser.getName().contentEquals("date")) {
                                    futureWeather = new FutureWeather();
                                }
                                if(xmlPullParser.getName().contentEquals("date")) {
                                    xmlPullParser.next();
                                    futureWeather.setDate(xmlPullParser.getText());
                                    xmlPullParser.next();
                                } else if(xmlPullParser.getName().contentEquals("high")) {
                                    xmlPullParser.next();
                                    futureWeather.setHigh(xmlPullParser.getText());
                                    xmlPullParser.next();
                                } else if(xmlPullParser.getName().contentEquals("low")) {
                                    xmlPullParser.next();
                                    futureWeather.setLow(xmlPullParser.getText());
                                    xmlPullParser.next();
                                } else if(xmlPullParser.getName().contentEquals("day")) {
                                    int count = 0;
                                    for(xmlPullParser.next();!xmlPullParser.getName().contentEquals("weather") && !xmlPullParser.getName().contentEquals("zhishu"); xmlPullParser.next()) {
                                        if(xmlPullParser.getName().contentEquals("type")) {
                                            xmlPullParser.next();
                                            Log.d("test",xmlPullParser.getText());
                                            if(count < 3) {
                                                futureWeather.setDayType(xmlPullParser.getText());
                                            } else {
                                                futureWeather.setNightType(xmlPullParser.getText());
                                            }

                                        } else if(xmlPullParser.getName().contentEquals("fengxiang")) {
                                            xmlPullParser.next();
                                            Log.d("test",xmlPullParser.getText());
                                            if(count < 3) {
                                                futureWeather.setDayFengXiang(xmlPullParser.getText());
                                            } else {
                                                futureWeather.setNightFengXiang(xmlPullParser.getText());
                                            }
                                        } else if(xmlPullParser.getName().contentEquals("fengli")) {
                                            xmlPullParser.next();
                                            Log.d("test",xmlPullParser.getText());
                                            if(count < 3) {
                                                futureWeather.setDayFengLi(xmlPullParser.getText());
                                            } else {
                                                futureWeather.setNightFengLi(xmlPullParser.getText());
                                            }

                                        }
                                        xmlPullParser.next();
                                        ++count;
                                    }
                                }
                                if(xmlPullParser.getName().contentEquals("weather")) {
                                    futureWeathers.add(futureWeather);
                                }
                            }
                            todayWeather.setFutureWeathers(futureWeathers);
                        }
                        break;
                }

                eventType = xmlPullParser.next();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;

    }

    /**
     * 初始化界面
     */
    public void initView() {
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        wenduTv = (TextView) findViewById(R.id.temp_now);


        // 未来天气
        dates = new ArrayList<TextView>();
        temperatures = new ArrayList<TextView>();
        dayTypes = new ArrayList<TextView>();
        fengLis = new ArrayList<TextView>();
        dates.add(weekTv);
        dates.add((TextView)findViewById(R.id.week_today1));
        dates.add((TextView)findViewById(R.id.week_today2));
        dates.add((TextView)findViewById(R.id.week_today3));
        dates.add((TextView)findViewById(R.id.week_today4));

        temperatures.add(temperatureTv);
        temperatures.add((TextView)findViewById(R.id.temperature1));
        temperatures.add((TextView)findViewById(R.id.temperature2));
        temperatures.add((TextView)findViewById(R.id.temperature3));
        temperatures.add((TextView)findViewById(R.id.temperature4));

        dayTypes.add(climateTv);
        dayTypes.add((TextView)findViewById(R.id.climate1));
        dayTypes.add((TextView)findViewById(R.id.climate2));
        dayTypes.add((TextView)findViewById(R.id.climate3));
        dayTypes.add((TextView)findViewById(R.id.climate4));

        fengLis.add((TextView) findViewById(R.id.wind1));
        fengLis.add((TextView) findViewById(R.id.wind2));
        fengLis.add((TextView) findViewById(R.id.wind3));
        fengLis.add((TextView) findViewById(R.id.wind4));

        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        wenduTv.setText("N/A");
    }

    /**
     * 更新天气界面
     * @param todayWeather
     */
    public void updateTodayWeather(TodayWeather todayWeather) {
        this.initView();
        cityTv.setText(todayWeather.getCityName());
        timeTv.setText(todayWeather.getUdpateTime() + "发布");
        humidityTv.setText("湿度" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        //temperatureTv.setText(todayWeather.getLow().split("低温")[1] + "~" + todayWeather.getHigh().split("高温")[1]);
        //climateTv.setText(todayWeather.getType());
        windTv.setText("风力" + todayWeather.getFengli());
        wenduTv.setText(todayWeather.getWendu()+"℃");

        int index = 0;
        for(FutureWeather futureWeather : todayWeather.getFutureWeathers()) {
            dates.get(index).setText(futureWeather.getDate());
            temperatures.get(index).setText(futureWeather.getLow().split("低温")[1] + "~" + futureWeather.getHigh().split("高温")[1]);
            dayTypes.get(index).setText(futureWeather.getDayType());
            fengLis.get(index).setText(futureWeather.getDayFengLi());
            ++index;
        }
        Toast.makeText(this, "数据更新成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.aciont_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
