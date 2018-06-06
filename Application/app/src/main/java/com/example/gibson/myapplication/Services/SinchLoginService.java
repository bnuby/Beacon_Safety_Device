package com.example.gibson.myapplication.Services;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.gibson.myapplication.CallingActivity;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.User;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SinchLoginService extends Service {


  private static final String APP_KEY = "dd3452d3-6c1a-4f37-8deb-1c9878eb6831";
  private static final String APP_SECRET = "lxj2OtDCnE2eA3LgY7ur7g==";
  private static final String ENVIRONMENT = "clientapi.sinch.com";
  private static final String TAG = "SinchLoginService";

  private Call call;
  private SinchClient sinchClient;
  private String callerId;
  private String recipientId;
  static String user;
  private  SinchBinder sinchBinder = new SinchBinder();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    try{
      user = intent.getStringExtra("callerId");
      Log.i(TAG, user);
      start(user);
      Log.i(TAG, user);
    }catch (Exception e){

      DatabaseService databaseService = new DatabaseService(this);
      String user = databaseService.getUser().username;
      start(user);
    }
    return START_STICKY;
  }


  private void start(String callerId) {{
    sinchClient = Sinch.getSinchClientBuilder()
            .context(getApplicationContext())
            .userId(callerId)
            .applicationKey(APP_KEY)
            .applicationSecret(APP_SECRET)
            .environmentHost(ENVIRONMENT)
            .build();

    sinchClient.setSupportCalling(true);
    sinchClient.startListeningOnActiveConnection();
    sinchClient.start();
    Log.i(TAG, "start: ");  
    sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
  }
}

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate: ");
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return sinchBinder;
  }

  private class SinchCallClientListener implements CallClientListener {

    @Override
    public void onIncomingCall(CallClient callClient, Call call) {
      Intent intent = new Intent(getBaseContext(), CallingActivity.class);
//      Intent intent = new Intent();
      intent.putExtra("recipientId", call.getCallId());
      intent.putExtra("fromImcomming",true);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//      intent.setComponent(new ComponentName(getApplicationContext().getPackageName(), CallingActivity.class.getName()));

      PowerManager pm=(PowerManager) getSystemService(getBaseContext().POWER_SERVICE);
      PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
      wl.acquire();
      wl.release();
      KeyguardManager km= (KeyguardManager) getSystemService(getBaseContext().KEYGUARD_SERVICE);
      KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
      kl.disableKeyguard();

      SinchLoginService.this.startActivity(intent);
    }
  }

  public class SinchBinder extends Binder{
    public Call callUserVideo(String userId) {
      return sinchClient.getCallClient().callUserVideo(userId);
    }

    public String getUserName() {
      return callerId;
    }


    public void startClient(String userName) {
      start(userName);
    }


    public Call getCall(String callId) {
      Log.i(TAG, "getCall: "+callId);
      return sinchClient.getCallClient().getCall(callId);
    }

    public VideoController getVideoController() {
      if (sinchClient==null) {
        return null;
      }
      return sinchClient.getVideoController();
    }

    public AudioController getAudioController() {
      if (sinchClient==null) {
        return null;
      }
      return sinchClient.getAudioController();
    }
  }

  @Override
  public void onDestroy() {
    sinchClient.stopListeningOnActiveConnection();

    super.onDestroy();
  }
}
