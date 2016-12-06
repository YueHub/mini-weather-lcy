package cn.edu.pku.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cn.edu.pku.fragment.VPFragment1;
import cn.edu.pku.fragment.VPFragment2;
import cn.edu.pku.fragment.VPFragment3;


/**
 * @Author: pyz
 * @Package: com.pyz.viewpagerdemo.adapter
 * @Description: TODO
 * @Project: ViewPagerDemo
 * @Date: 2016/8/18 11:49
 */
public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private String[] tabTilte;

    public MyFragmentStatePagerAdapter(FragmentManager fragmentManager, String[] tabTitle) {
        super(fragmentManager);
        this.tabTilte = tabTitle;
    }

    public MyFragmentStatePagerAdapter(android.app.FragmentManager fragmentManager, String[] tabTitle) {
        super(null);
        this.tabTilte = tabTitle;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VPFragment1();
            case 1:
                return new VPFragment2();
            case 2:
                return new VPFragment3();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }
}
