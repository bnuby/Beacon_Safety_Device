package com.example.gibson.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.gibson.myapplication.Model.User;
import com.example.gibson.myapplication.Services.DatabaseService;
import com.example.gibson.myapplication.Services.RequestManager;

public class LoginActivity extends AppCompatActivity {

  static RequestQueue queue;
  static AlertDialog dialog;
  static Context mContext;
  EditText usernameET;
  EditText passwordET;
  Button loginBtn;
  Button registerBtn;

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
    mContext = getApplicationContext();

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
          showLoading("Login..");
          RequestManager.loginRequest(usernameET.getText().toString(), passwordET.getText().toString());
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

  public static void sendToast(String message) {
    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
  }

  public void showLoading(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View view = getLayoutInflater().inflate(R.layout.loading_view, null);
    TextView textView = view.findViewById(R.id.message);
    textView.setText(message);
    builder.setCancelable(false);
    builder.setView(view);
    dialog = builder.create();
    dialog.show();
  }

  public static void dissmissLoading() {
    dialog.dismiss();
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
