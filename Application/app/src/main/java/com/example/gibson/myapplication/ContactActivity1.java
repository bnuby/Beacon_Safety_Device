//package com.example.gibson.myapplication;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.example.gibson.myapplication.Services.SinchService;
//import com.sinch.android.rtc.SinchError;
//
//import static android.content.Context.BIND_AUTO_CREATE;
//
///**
// * Created by gibson on 20/03/2018.
// */
//
//public class ContactActivity1 extends Fragment implements SinchService.StartFailedListener, ServiceConnection {
//
//  private Button mLoginButton;
//  private EditText mLoginName;
//  private ProgressDialog mSpinner;
//  private Context mContext;
//
//
//  private static final String TAG = "LoginActivity";
//  private SinchService.SinchServiceInterface mSinchServiceInterface;
//
//  @Nullable
//  @Override
//  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//    View view = inflater.inflate(R.layout.activity_contact, null);
//    mContext = view.getContext();
//    //asking for permissions here
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE},100);
//    }
//    //initializing UI elements
//    mLoginName = (EditText) view.findViewById(R.id.loginName);
//    mLoginButton = (Button) view.findViewById(R.id.loginButton);
//    mLoginButton.setEnabled(false);
//    mLoginButton.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        loginClicked();
//      }
//    });
//    mContext.bindService(new Intent(mContext, SinchService.class), this,
//            BIND_AUTO_CREATE);
//    return view;
//  }
//
//  @Override
//  public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//    if (SinchService.class.getName().equals(componentName.getClassName())) {
//      mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
//      onServiceConnected();
//    }
//  }
//
//  @Override
//  public void onServiceDisconnected(ComponentName componentName) {
//    if (SinchService.class.getName().equals(componentName.getClassName())) {
//      mSinchServiceInterface = null;
////      onServiceDisconnected();
//    }
//  }
//
//  //this method is invoked when the connection is established with the SinchService
//  @Override
//  public void onServiceConnected() {
//    Log.i(TAG, "onServiceConnected: 1");
//    mLoginButton.setEnabled(true);
//    mSinchServiceInterface.setStartListener(this);
//  }
//
//  @Override
//  public void onPause() {
//    if (mSpinner != null) {
//      mSpinner.dismiss();
//    }
//    super.onPause();
//  }
//
//  @Override
//  public void onStartFailed(SinchError error) {
//    Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
//    if (mSpinner != null) {
//      mSpinner.dismiss();
//    }
//  }
//
//  //Invoked when just after the service is connected with Sinch
//  @Override
//  public void onStarted() {
//    openPlaceCallActivity();
//  }
//
//  //Login is Clicked to manually to connect to the Sinch Service
//  private void loginClicked() {
//    Log.i(TAG, "loginClicked: 3");
//    String userName = mLoginName.getText().toString();
//
//    if (userName.isEmpty()) {
//      Toast.makeText(mContext, "Please enter a name", Toast.LENGTH_LONG).show();
//      return;
//    }
//
//    if (!mSinchServiceInterface.isStarted()) {
//      mSinchServiceInterface.startClient(userName);
//      showSpinner();
//    } else {
//      openPlaceCallActivity();
//    }
//  }
//
//
//  //Once the connection is made to the Sinch Service, It takes you to the next activity where you enter the name of the user to whom the call is to be placed
//  private void openPlaceCallActivity() {
////    Intent mainActivity = new Intent(this, PlaceCallActivity.class);
////    startActivity(mainActivity);
//  }
//
//  private void showSpinner() {
//    mSpinner = new ProgressDialog(mContext);
//    mSpinner.setTitle("Logging in");
//    mSpinner.setMessage("Please wait...");
//    mSpinner.show();
//  }
//}
