package com.example.gibson.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gibson.myapplication.Services.SinchService;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by gibson on 20/03/2018.
 */

public class ContactActivity extends Fragment implements View.OnClickListener{

  private static final String APP_KEY = "dd3452d3-6c1a-4f37-8deb-1c9878eb6831";
  private static final String APP_SECRET = "lxj2OtDCnE2eA3LgY7ur7g==";
  private static final String ENVIRONMENT = "clientapi.sinch.com";

  private Context mContext;
  private SinchClient sinchClient;
  private Button mLoginButton;
  private String callerId = "callerId";
  private String recipientId = "recipientId";

  private static final String TAG = "LoginActivity";

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_contact, null);
    mLoginButton = (Button) view.findViewById(R.id.loginButton);
    mLoginButton.setOnClickListener(this);
    return view;
  }


  @Override
  public void onClick(View v) {
    Intent intent = new Intent(getContext(),Calling.class);
    intent.putExtra("callerId",callerId);
    intent.putExtra("recipientId",recipientId);
    startActivity(intent);
  }
}
