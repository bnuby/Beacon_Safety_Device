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

public class ContactActivity extends Fragment {


  static final String APP_ID = "961";
  static final String AUTH_KEY = "PBZxXW3WgGZtFZv";
  static final String AUTH_SECRET = "vvHjRbVFF6mmeyJ";
  static final String ACCOUNT_KEY = "961";
  String login = "login";
  String password = "password";

//  QBRTCSurfaceView surfaceView;
//  EglBase eglContext = QBRTCClient.getInstance(getContext()).getEglContext();
//  final QBUser user = new QBUser(login, password);
//  QBChatService chatService;

  private View _instance;
  private TextView textTV;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_contact, container, false);
    init(rootView);

    _instance = rootView;
    createSchedule();
    return rootView;
  }

  void init(View view) {
    textTV = view.findViewById(R.id.messageTV);
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
