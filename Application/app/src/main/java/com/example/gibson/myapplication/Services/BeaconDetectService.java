package com.example.gibson.myapplication.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.gibson.myapplication.R;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class BeaconDetectService extends Service {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  @Override
  public void onCreate() {
    super.onCreate();
    Log.v("asd", "qwe");

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    final NotificationCompat.Builder builder;
    final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    builder = new NotificationCompat.Builder(getBaseContext(), "channel")
              .setSmallIcon(R.drawable.ic_launcher_background);


    builder.setContentTitle("Alarm Beacon");
    builder.setContentText("Alarm Beacon");
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            new Runnable() {
              @Override
              public void run() {
                notificationManager.notify(1, builder.build());
              }
            }, 1000, 1000, MILLISECONDS);

    Log.v("asd", "qwe");

    return Service.START_NOT_STICKY;
  }


}
