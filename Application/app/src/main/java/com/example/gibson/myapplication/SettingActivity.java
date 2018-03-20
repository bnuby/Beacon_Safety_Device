package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.gibson.myapplication.MainViewPager.mqtt_service;

/**
 * Created by gibson on 20/03/2018.
 */

public class SettingActivity extends Fragment {

  private View _instance;
  private TextView textTV;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_setting, container, false);
    init(rootView);
    _instance = rootView;
    createSchedule();
    return rootView;
  }

  void init(View view) {
    textTV = view.findViewById(R.id.textTV);
  }

  void createSchedule() {
    ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();
    Log.v("schedule","Message");
    schedule.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        updateMessage();
      }
    }, 0 * 1000, 2 * 1000, TimeUnit.MILLISECONDS);
  }

  void updateMessage() {

    List<String> message = mqtt_service.getMessage();
    Log.v("schedule",message.toString());
    for (int i = 0 ; i < message.size(); i++) {
      textTV.setText(textTV.getText() + message.get(i) + "\n");
    }
  }
}
