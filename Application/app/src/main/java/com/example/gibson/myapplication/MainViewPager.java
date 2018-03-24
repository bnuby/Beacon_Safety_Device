package com.example.gibson.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.MQTT_SERVICE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gibson on 20/03/2018.
 */

public class MainViewPager extends AppCompatActivity {

  private final int NUM_PAGES = 4;
  public static MQTT_SERVICE mqtt_service;
  private static DatabaseService databaseService;
  private ViewPager viewPager;
  private PagerAdapter pagerAdapter;
  private TabLayout tabLayout;

  public static DatabaseService getDatabaseService() {
    return databaseService;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_viewpager);
    databaseService = new DatabaseService(this);
    init();
  }

  public void init() {
    JSONArray mqttArray = getDatabaseService().getMqtt();
    if(mqttArray != null && mqttArray.length() == 0) {
      try {
        JSONObject mqttObj = mqttArray.getJSONObject(0);
        String host = "tcp://" + mqttObj.getString("host");
        String topic = mqttObj.getString("topic");
        String username = mqttObj.getString("username");
        String password = mqttObj.getString("password");
        Log.v("host", host);

        mqtt_service = new MQTT_SERVICE(this, host, topic, username, password);
        mqtt_service.setStatusTextView((TextView) findViewById(R.id.mqtt_status));
        mqtt_service.startConnect();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }


    tabLayout = findViewById(R.id.tabLayout);
    viewPager = findViewById(R.id.pager);
    pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.action_beacon:
        intent = new Intent(this, BeaconActivity.class);
        startActivity(intent);
        break;

      case R.id.action_setting:
        intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private class ViewPagerAdapter extends FragmentStatePagerAdapter {

    String[] title = new String[]{
            getResources().getString(R.string.title_beacon) ,
            getResources().getString(R.string.title_contact),
            getResources().getString(R.string.title_manage_beacon),
            getResources().getString(R.string.title_setting)};

    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new MainActivity();
        case 1:
          return new ContactActivity();
        case 2:
          return new BeaconActivity();
        case 3:
          return new SettingActivity();
      }
      return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return title[position];
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }

}
