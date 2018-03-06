package com.supermart.supermart;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.supermart.supermart.Adapter.DaftarPesananPagerAdapter;

public class DaftarPesananActivity extends AppCompatActivity {

    TabLayout tabLayout;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_pesanan);

        getSupportActionBar().setElevation(0);

        this.tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        this.viewPager = (ViewPager) findViewById(R.id.pager);

        this.initTabLayout();
        this.initViewPager();
        this.initTabPositon();
    }

    private void initTabPositon() {
        int pos = getIntent().getIntExtra("tab", -1);

        if(pos > -1) {
            this.viewPager.setCurrentItem(pos);
        }
    }

    private void initViewPager() {
        PagerAdapter pagerAdapter = new DaftarPesananPagerAdapter(getSupportFragmentManager(), this.viewPager);
        this.viewPager.setAdapter(pagerAdapter);
        this.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initTabLayout() {
        this.tabLayout.addTab(tabLayout.newTab().setText("Diproses"));
        this.tabLayout.addTab(tabLayout.newTab().setText("Dikirim"));
        this.tabLayout.addTab(tabLayout.newTab().setText("Selesai"));
        this.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DaftarPesananActivity.this.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    @Override
    public void onBackPressed() {

        if(getIntent().getBooleanExtra("cleartask", false)) {
            Intent intent;
            intent = new Intent(DaftarPesananActivity.this, DaftarKategoriActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }

}
