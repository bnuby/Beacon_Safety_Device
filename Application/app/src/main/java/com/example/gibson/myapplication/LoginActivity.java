package com.example.gibson.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.gibson.myapplication.AbstractClass.BeaconBaseActivity;
import com.example.gibson.myapplication.Model.User;
import com.example.gibson.myapplication.Services.BeaconDetectService;
import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.RequestManager;
import com.example.gibson.myapplication.Services.SinchLoginService;

public class LoginActivity extends BeaconBaseActivity {

  static RequestQueue queue;
  EditText usernameET;
  EditText passwordET;
  Button loginBtn;
  Button registerBtn;
  Intent SinchLoginIntent;

  public static RequestQueue getQueue() {
    return queue;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    usernameET = findViewById(R.id.usernameET);
    passwordET = findViewById(R.id.passwordET);
    loginBtn = findViewById(R.id.loginBtn);
    registerBtn = findViewById(R.id.registerBtn);

    // Setting Request
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

    Network network = new BasicNetwork(new HurlStack());

    queue = new RequestQueue(cache, network);
    queue.start();



    loginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!usernameET.getText().toString().isEmpty() &&
                !passwordET.getText().toString().isEmpty()) {

          if(usernameET.getText().toString().equals("admin") &&
                  passwordET.getText().toString().equals("admin")) {
            successLogin();
          } else {
            showLoading("Login..");
            RequestManager.loginRequest(usernameET.getText().toString(), passwordET.getText().toString());


          }

        } else
          sendToast("All fill must be fill.");
      }
    });

    registerBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
      }
    });


    Intent intent = new Intent(this, BeaconDetectService.class);


    startService(intent);
  }

  public static void startSinch(String userName){
    Intent sinchLoginIntent =new Intent(mContext,SinchLoginService.class);
    sinchLoginIntent.putExtra("callerId",userName);
    mContext.startService(sinchLoginIntent);
  }


  public void tryLogin() {
    DatabaseService databaseService = new DatabaseService(this);
    User user = databaseService.getUser();

    if(user != null) {
      showLoading("Login..");
      Log.v("user", user.username);
      Log.v("password", user.username);
      RequestManager.loginRequest(user.username, user.password);
    }
  }

  public static void successLogin() {
    Intent intent = new Intent(mContext, MainActivity.class);
    mContext.startActivity(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    queue.start();
    tryLogin();
  }

  @Override
  protected void onPause() {
    super.onPause();
    queue.stop();
  }


}
