package cn.edu.pku.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * Created by Yue on 2016/11/29.
 */
public class StartActivity extends Activity {

    private boolean isFirstStart = false;   // 是否为第一次打开APP
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_activity_layout);
        initData();
    }
    private void initData() {
        SharedPreferences sp = getSharedPreferences("StartActivity", 0);
        isFirstStart = sp.getBoolean("isFirst",true);
        /* 判断是否第一次打开App,如果是则跳转至引导页，否则跳转到主页 */
        if (isFirstStart) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoStartActivity();
                }
            }, 2000);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoMainActivity();
                }
            }, 2000);
        }
    }
    private void gotoMainActivity() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoStartActivity() {
        Intent intent = new Intent(StartActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }
}
