package com.example.gibson.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.gibson.myapplication.AbstractClass.BeaconBaseActivity;
import com.example.gibson.myapplication.Services.RequestManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BeaconBaseActivity {

  public static RequestQueue queue;

  EditText nameET;
  EditText usernameET;
  EditText passwordET;
  EditText password_confirmET;
  EditText emailET;
  Button submitBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
//    mContext = getApplicationContext();

    // Setting Request
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

    Network network = new BasicNetwork(new HurlStack());

    queue = new RequestQueue(cache, network);
    queue.start();
    init();
  }

  public void init() {
    nameET = findViewById(R.id.nameET);
    usernameET = findViewById(R.id.usernameET);
    passwordET = findViewById(R.id.passwordET);
    password_confirmET = findViewById(R.id.password_confirmET);
    emailET = findViewById(R.id.emailET);
    submitBtn = findViewById(R.id.submitBtn);
    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String password = passwordET.getText().toString();
        String password_confirm = password_confirmET.getText().toString();
        if (password.equals(password_confirm)) {
          JSONObject object = new JSONObject();
          try {
            object.put("nickname", nameET.getText().toString());
            object.put("username", usernameET.getText().toString());
            object.put("password", passwordET.getText().toString());
            object.put("email", emailET.getText().toString());
            RequestManager.registerRequest(object);
          } catch (JSONException e) {
            e.printStackTrace();
          }
        } else {
          Toast.makeText(getApplicationContext(), "Wrong Password Please Try Again!", Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  public static void dissmissLoading(int status) {
    dialog.dismiss();
    if (status == 201) {
      sendToast("Success");
      ((Activity) mContext).finish();
      return;
    } else if (status == 406) {
      sendToast("User Exists");
      return;
    }
    sendToast("Request Error Please Try Again.");
  }

  @Override
  protected void onPause() {
    super.onPause();
    queue.stop();
  }

  @Override
  protected void onResume() {
    super.onResume();
    queue.start();
  }
}
