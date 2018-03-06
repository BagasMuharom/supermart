package com.supermart.supermart.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.supermart.supermart.Fragment.Dikirim;
import com.supermart.supermart.Fragment.Diproses;
import com.supermart.supermart.Fragment.Selesai;

public class DaftarPesananPagerAdapter extends FragmentStatePagerAdapter {

    ViewPager viewPager;

    public DaftarPesananPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);
        this.viewPager = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Diproses(this.viewPager);
            case 1:
                return new Dikirim(this.viewPager);
            case 2:
                return new Selesai(this.viewPager);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
