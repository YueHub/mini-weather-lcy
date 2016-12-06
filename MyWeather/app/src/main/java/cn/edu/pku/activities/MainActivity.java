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

import cn.edu.pku.adapter.MyPagerAdapter;
import cn.edu.pku.model.TodayWeather;
import cn.edu.pku.service.AutoUpdateBinder;
import cn.edu.pku.service.AutoUpdateService;
import cn.edu.pku.util.NetUtil;

/**
 * Created by 22253 on 2016/9/18.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView mUpdateBtn;   // 更新按钮
    private ImageView mCityBtn; // 选择城市按钮
    private ProgressBar updateProcess;  // 更新进度

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv;
    private TextView temperatureTv, climateTv, windTv, wenduTv;

    private ImageView weatherImg, pmImg;    // 天气图片

    private static final int UPDATE_TODAY_WEATHER = 1;

    private AutoUpdateBinder autoUpdateBinder;

    // ViewPaper
    private ViewPager mViewPager;
    private MyPagerAdapter myAdapter;
    private List<View> viewList = new ArrayList<View>();

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

    private MainActivity mainActivity = this;

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

        // 测试网络状态
        /*
        int netState = NetUtil.getNetworkState(this);
        if(netState == NetUtil.NETWORK_WIFI) {
            Toast.makeText(MainActivity.this, "WIFI连接", Toast.LENGTH_SHORT).show();
        } else if(netState == NetUtil.NETWORK_MOBILE) {
            Toast.makeText(MainActivity.this, "移动蜂窝网连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "无网络", Toast.LENGTH_SHORT).show();
        }
        */
        // 更新天气数据
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        updateProcess = (ProgressBar) findViewById(R.id.update_process);

        mCityBtn = (ImageView) findViewById(R.id.title_city_manager);
        mCityBtn.setOnClickListener(this);

        // 开始服务
        //startService(new Intent(getBaseContext(), AutoUpdateService.class));
        Intent bindIntent = new Intent(this, AutoUpdateService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);


        // ViewPaper
        LayoutInflater inflater = LayoutInflater.from(this);
        viewList.add(inflater.inflate(R.layout.next_days_1,null));
        viewList.add(inflater.inflate(R.layout.next_days_2,null));
        viewList.add(inflater.inflate(R.layout.next_days_3,null));
        myAdapter = new MyPagerAdapter(viewList);
        mViewPager = (ViewPager) this.findViewById(R.id.mViewPager2);
        mViewPager.setAdapter(myAdapter);
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());
        //mViewPager.setOffscreenPageLimit(viewList.size());

        // 初始化界面
        initView();

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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.title_city_manager) {
            Intent intent = new Intent(this, SelectCity.class);
            //startActivity(intent);
            startActivityForResult(intent, 1);
        }

        if(view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather-cityCode:", cityCode);
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather-netInfo:", "网络连接正常");
                // 设置更新按钮不可见 旋转按钮可见
                mUpdateBtn.setVisibility(View.INVISIBLE);
                updateProcess.setVisibility(View.VISIBLE);
                queryWeatherCode(cityCode);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            // *将现在的城市代码存入文件中
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("main_city_code", newCityCode);
            editor.commit();
            Log.d("myWeather-City-Select:" , "选择城市代码为:" + newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather-NetInfo:" , "网络连接正常");
                queryWeatherCode(newCityCode); // **刷新页面
            } else {
                Log.d("myWeather-NetInfo:", "网络连接异常");
                Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_SHORT);
            }
        }
    }

    public void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather-URL:", address);
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
                    Log.d("myWeather", responseStr);
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
            Log.d("myapp2", "parserXML");
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().contentEquals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if(xmlPullParser.getName().contentEquals("city")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                            Log.d("myapp2", "city:" + xmlPullParser.getText());
                            } else if(xmlPullParser.getName().contentEquals("updatetime")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setUdpateTime(xmlPullParser.getText());
                            Log.d("myapp2", "updateTime:" + xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("shidu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                            Log.d("myapp2", "humidity:" + xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("wendu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                            Log.d("myapp2", "temperature:" + xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("pm25")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                            Log.d("myapp2", "pm2.5:" + xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("quality")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                            Log.d("myapp2", "quality:" + xmlPullParser.getText());
                        } else if(xmlPullParser.getName().contentEquals("fengxiang") && windDirectionCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            Log.d("myapp2", "windDirection:" + xmlPullParser.getText());
                            ++windDirectionCount;
                        } else if(xmlPullParser.getName().contentEquals("fengli") && windForceCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            Log.d("myapp2", "windForce:" + xmlPullParser.getText());
                            ++windForceCount;
                        } else if(xmlPullParser.getName().contentEquals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            Log.d("myapp2", "date:" + xmlPullParser.getText());
                            ++dateCount;
                        } else if(xmlPullParser.getName().contentEquals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText());
                            Log.d("myapp2", "high:" + xmlPullParser.getText());
                            ++highCount;
                        } else if(xmlPullParser.getName().contentEquals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText());
                            Log.d("myapp2", "low:" + xmlPullParser.getText());
                            ++lowCount;
                        } else if(xmlPullParser.getName().contentEquals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            Log.d("myapp2", "type:" + xmlPullParser.getText());
                            ++typeCount;
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
        // 开始服务




    }

    public void updateTodayWeather(TodayWeather todayWeather) {
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUdpateTime() + "发布");
        humidityTv.setText("湿度" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow().split("低温")[1] + "~" + todayWeather.getHigh().split("高温")[1]);
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力" + todayWeather.getFengli());
        wenduTv.setText(todayWeather.getWendu());
        Toast.makeText(this, "数据更新成功", Toast.LENGTH_SHORT).show();
    }
}
