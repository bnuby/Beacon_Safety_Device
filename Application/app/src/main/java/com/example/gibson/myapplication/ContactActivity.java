package com.example.gibson.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sinch.android.rtc.SinchClient;

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


//    setContentView(R.layout.activity_calling);
//    Intent intent = getActivity().getIntent();
//    callerId = intent.getStringExtra("callerId");
//    recipientId = intent.getStringExtra("recipientId");
//    sinchClient = Sinch.getSinchClientBuilder()
//            .context(getContext())
//            .userId(callerId)
//            .applicationKey(APP_KEY)
//            .applicationSecret(APP_SECRET)
//            .environmentHost(ENVIRONMENT)
//            .build();
//
//    sinchClient.setSupportCalling(true);
//    sinchClient.startListeningOnActiveConnection();
//    sinchClient.start();
//
//    sinchClient.getCallClient().addCallClientListener(new CallingActivity.SinchCallClientListener());
//
//    button = (Button) findViewById(R.id.testbutton);
//    callState = (TextView) findViewById(R.id.testtextView);
//    buttonLogout =findViewById(R.id.testlogout);

//    button.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        if (call == null) {
//          call = sinchClient.getCallClient().callUserVideo(recipientId);
//          call.addCallListener(new CallingActivity.SinchCallListener());
//          button.setText("Hang Up");
//          Log.i(TAG, "onClick: cickbtn");
//        } else {
//          call.hangup();
//        }
//      }
//    });
//    buttonLogout.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        sinchClient.stop();
//        finish();
//      }
//    });

    return view;
  }


  @Override
  public void onClick(View v) {
    Intent intent = new Intent(getContext(),CallingActivity.class);
    intent.putExtra("callerId",callerId);
    intent.putExtra("recipientId",recipientId);
    startActivity(intent);
  }
}
