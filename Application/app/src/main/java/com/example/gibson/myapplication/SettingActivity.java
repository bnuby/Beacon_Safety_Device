package com.example.gibson.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.MQTT_SERVICE;
import com.example.gibson.myapplication.Services.RequestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.gibson.myapplication.MainActivity.getDatabaseService;
import static com.example.gibson.myapplication.MainActivity.mqtt_service;

/**
 * Created by gibson on 21/03/2018.
 */

public class SettingActivity extends Fragment implements View.OnClickListener {

  private View _instance;
  TextView mqtt_status;
  EditText mqtt_host;
  EditText mqtt_topic;
  EditText mqtt_username;
  EditText mqtt_password;

  // User Login Configure
  EditText usernameET;
  EditText passwordET;
  Button loginBtn;
  Button registerBtn;

  Button mqtt_loginBtn;

//  @Override
//  protected void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_setting);
//
//    init();
//
//    Toolbar toolbar = findViewById(R.id.settingTB);
//    setSupportActionBar(toolbar);
//    ActionBar actionBar = getSupportActionBar();
//    actionBar.setDisplayHomeAsUpEnabled(true);
//  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    _instance = inflater.inflate(R.layout.activity_setting, null);
    init();
    return _instance;
  }

  void init() {

    // Setting mqtt
    mqtt_status = _instance.findViewById(R.id.mqtt_status);
    mqtt_host = _instance.findViewById(R.id.mqtt_host);
    mqtt_topic = _instance.findViewById(R.id.mqtt_topic);
    mqtt_username = _instance.findViewById(R.id.mqtt_username);
    mqtt_password = _instance.findViewById(R.id.mqtt_password);
    mqtt_loginBtn = _instance.findViewById(R.id.mqtt_login);
    mqtt_loginBtn.setOnClickListener(this);


    // Setting user
    usernameET = _instance.findViewById(R.id.usernameET);
    passwordET = _instance.findViewById(R.id.passwordET);
    loginBtn = _instance.findViewById(R.id.loginBtn);
    loginBtn.setOnClickListener(this);
    registerBtn = _instance.findViewById(R.id.registerBtn);
    registerBtn.setOnClickListener(this);

    JSONArray array = getDatabaseService().getMqtt();
    if(array.length() != 0) {
      try {
        JSONObject object = array.getJSONObject(0);
        mqtt_host.setText(object.getString("host"));
        mqtt_topic.setText(object.getString("topic"));
        mqtt_username.setText(object.getString("username"));
        mqtt_password.setText(object.getString("password"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }


    if(mqtt_service!=null) {
      mqtt_service.setStatusTextView(mqtt_status);
      mqtt_loginBtn.setText(R.string.cancel);
    }
  }

//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    switch(item.getItemId()) {
//      case android.R.id.home:
//        finish();
//    }
//    return super.onOptionsItemSelected(item);
//  }

  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.mqtt_login:
        Button btn = (Button) view;
        if(btn.getText() == getResources().getString(R.string.login)) {
          DatabaseService service = MainActivity.getDatabaseService();
          service.deleteMqtt();
          String host = "tcp://" + mqtt_host.getText().toString();
          String topic = mqtt_topic.getText().toString();
          String username = mqtt_username.getText().toString();
          String password = mqtt_password.getText().toString();
          if(host.equals("tcp://")) {
            Toast.makeText(_instance.getContext(), "Host and Username must not be empty", Toast.LENGTH_SHORT).show();
            return;
          }
          service.insertMqtt(mqtt_host.getText().toString(), topic, username, password);

          mqtt_service = new MQTT_SERVICE(_instance.getContext(), host, topic, username, password);
          mqtt_service.setStatusTextView(mqtt_status);
          mqtt_service.startConnect();
          btn.setText(getResources().getString(R.string.cancel));
        } else {

          btn.setText(getResources().getString(R.string.login));
          mqtt_service.cancelConnect();
        }
        break;

      case R.id.loginBtn:
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        RequestService.loginRequest(username, password);
        break;

      case R.id.registerBtn:

        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
        break;

    }
  }
}
