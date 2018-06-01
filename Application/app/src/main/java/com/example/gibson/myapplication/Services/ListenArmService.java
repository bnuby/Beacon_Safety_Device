package com.example.gibson.myapplication.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ListenArmService extends Service {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Binder binder = new Binder();
    Log.v("Service", "123");
    final Context mContext = getApplicationContext();
    Toast.makeText(getApplicationContext(), "Service", Toast.LENGTH_SHORT).show();
    final Handler handler = new Handler();
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(mContext, "Service", Toast.LENGTH_SHORT).show();

            handler.postDelayed(this, 1000);
          }
        }, 1000);
      }
    });
    t.start();

    return binder;
  }

}
