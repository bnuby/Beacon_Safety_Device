package com.example.gibson.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.gibson.myapplication.Services.BeaconDetectService;

public class ReceiveBeaconActivity extends AppCompatActivity {

  Button cancelBtn;
  Intent intent;
  public BluetoothAdapter mBluetoothAdapter;
  public static final int BluetoothRequestCode = 2;
  static MediaPlayer mediaPlayer;
  static Context mContext;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receivebeacon);

    mContext = this;

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
  }

  public static void playMedia(int music_source, int volume) {
    AudioManager manager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
    manager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            AudioManager.ADJUST_RAISE);
    stopMedia();

    mediaPlayer = MediaPlayer.create(mContext, music_source);
    mediaPlayer.start();
  }

  public static void stopMedia() {
    if(mediaPlayer != null && mediaPlayer.isPlaying())
      mediaPlayer.stop();
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
    if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
      intent = new Intent(this, BeaconDetectService.class);
      startService(intent);
    }
  }

  @Override
  protected void onResume() {
    checkBluetooth();
    startBeaconReceive();
    super.onResume();
  }

  @Override
  protected void onPause() {
    stopService(new Intent(ReceiveBeaconActivity.this, BeaconDetectService.class));
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stopMedia();
    MainActivity.receiveMode = false;
  }
}
