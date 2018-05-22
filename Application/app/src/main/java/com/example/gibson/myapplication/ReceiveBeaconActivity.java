package com.example.gibson.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.gibson.myapplication.Services.BeaconDetectService;

import java.security.Permissions;
import java.util.HashMap;

public class ReceiveBeaconActivity extends AppCompatActivity {

  Button cancelBtn;
  Intent intent;
  public BluetoothAdapter mBluetoothAdapter;
  public static final int BluetoothRequestCode = 2;
  static MediaStore.Audio.Media media;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receivebeacon);

    cancelBtn = findViewById(R.id.cancelBtn);
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        intent = new Intent(ReceiveBeaconActivity.this, BeaconDetectService.class);
        stopService(intent);
        finish();
      }
    });
    startBeaconReceive();


    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    checkBluetooth();
  }


  void checkBluetooth() {
    if (mBluetoothAdapter != null) {
      if (!mBluetoothAdapter.isEnabled()) {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BluetoothRequestCode);
      }
    } else {
      startBeaconReceive();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == BluetoothRequestCode) {
      if(resultCode == PackageManager.PERMISSION_GRANTED) {
        startBeaconReceive();
      }
    }
  }

  public void startBeaconReceive() {
    intent = new Intent(this, BeaconDetectService.class);
    startService(intent);
  }

}
