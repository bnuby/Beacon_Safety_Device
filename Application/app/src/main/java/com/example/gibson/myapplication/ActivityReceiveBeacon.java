package com.example.gibson.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.gibson.myapplication.Services.BeaconDetectService;

public class ActivityReceiveBeacon extends AppCompatActivity {

  Button cancelBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receivebeacon);

    Intent intent = new Intent(getBaseContext(), BeaconDetectService.class);
    startService(intent);

  }
}
