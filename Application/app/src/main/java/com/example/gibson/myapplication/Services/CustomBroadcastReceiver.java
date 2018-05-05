package com.example.gibson.myapplication.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    switch (intent.getAction()) {
      case "Login":
        if(intent.getBooleanExtra("status", false)) {
          sendToast(context, "Login Success");
        } else {
          sendToast(context, "Login Fail");
        }
        break;
      case "Register":
        if(intent.getBooleanExtra("status", false)) {
          sendToast(context, "Register Success");
        } else {
          sendToast(context, "Register Fail");
        }
    }

  }




  private void sendToast(Context context,String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
