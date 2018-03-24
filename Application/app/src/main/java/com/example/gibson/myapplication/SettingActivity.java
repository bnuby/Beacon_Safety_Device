package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.MQTT_SERVICE;

import static com.example.gibson.myapplication.MainViewPager.mqtt_service;

/**
 * Created by gibson on 21/03/2018.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

  TextView mqtt_status;
  EditText mqtt_host;
  EditText mqtt_topic;
  EditText mqtt_username;
  EditText mqtt_password;
  Button loginBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    init();

    Toolbar toolbar = findViewById(R.id.settingTB);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  void init() {
    mqtt_status = findViewById(R.id.mqtt_status);
    mqtt_host = findViewById(R.id.mqtt_host);
    mqtt_topic = findViewById(R.id.mqtt_topic);
    mqtt_username = findViewById(R.id.mqtt_username);
    mqtt_password = findViewById(R.id.mqtt_password);
    loginBtn = findViewById(R.id.mqtt_login);
    loginBtn.setOnClickListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.mqtt_login:
        Button btn = (Button) view;
        if(btn.getText() == getResources().getString(R.string.login)) {
          DatabaseService service = MainViewPager.getDatabaseService();
          service.deleteMqtt();
          String host = "tcp://" + mqtt_host.getText().toString();
          String topic = mqtt_topic.getText().toString();
          String username = mqtt_username.getText().toString();
          String password = mqtt_password.getText().toString();
          if(host.equals("tcp://") || username.equals("")) {
            Toast.makeText(this, "Host and Username must not be empty", Toast.LENGTH_SHORT).show();
            return;
          }
          service.insertMqtt(host, topic, username, password);

          mqtt_service = new MQTT_SERVICE(this, host, topic, username, password);
          mqtt_service.setStatusTextView(mqtt_status);
          mqtt_service.startConnect();
          btn.setText(getResources().getString(R.string.cancel));
        } else {
          btn.setText(getResources().getString(R.string.login));
          mqtt_service.cancelConnect();
        }
    }
  }
}
