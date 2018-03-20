package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by gibson on 20/03/2018.
 */

public class MainViewPager extends AppCompatActivity implements ViewPager.OnAdapterChangeListener {

  private final int NUM_PAGES = 2;
  public static MQTT_SERVICE mqtt_service;
  private ViewPager viewPager;
  private PagerAdapter pagerAdapter;


  // MQTT Client DEFAULT PARAMETER
  private String myTopic = "user/topic";
  private String host = "tcp://10.22.20.93:1337";
  private String username = "bun";
  private String password = "123";


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewpager);
    init();
  }

  public void init() {
    mqtt_service = new MQTT_SERVICE(this, host, myTopic, username, password);
    mqtt_service.subscribeMessage("test");

    viewPager = findViewById(R.id.pager);
    pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);
  }

  @Override
  public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

  }

  private class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new MainActivity();
        case 1:
          return new SettingActivity();
      }
      return null;
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }

}
