package com.limbo.chartdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.limbo.chartdemo.fragment.KChartFragment;
import com.limbo.chartdemo.fragment.TSChartFragment;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    private static String[] titles = {"分时","日K","5分","15分","30分","60分"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TSChartFragment());
        fragments.add(new KChartFragment());
        fragments.add(new TSChartFragment());
        fragments.add(new TSChartFragment());
        fragments.add(new TSChartFragment());
        fragments.add(new TSChartFragment());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),fragments,titles));
        viewPager.setOffscreenPageLimit(fragments.size());
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void OpenActivity(Context context){
        Intent intent = new Intent(context,ChartActivity.class);
        context.startActivity(intent);
    }
}
