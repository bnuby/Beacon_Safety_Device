package com.example.gibson.myapplication.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.R;
import com.example.gibson.myapplication.Services.BeaconDetectService;
import com.example.gibson.myapplication.Services.RequestManager;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;


public class DogFragment extends Fragment{

  private View _instance;
  static ListView listView;
  ArrayList<Beacon> beacons;
  ArrayList<HashMap<String,Object>> beaconList;
  int interval = 1000;

  Button cancelBtn;
  static Intent intent;
  public BluetoothAdapter mBluetoothAdapter;
  public static final int BluetoothRequestCode = 2;
  static MediaPlayer mediaPlayer;
  public static Context mContext;
  public static Boolean cancel;
  Call call;
  public static ImageView dog;
  public static Drawable sleepDog;
  public static Drawable angeryDog;
  public SwitchCompat recevingmodesw;
  private static DogFragment mainPageFragment;

  public static DogFragment getFragment() {
    if (mainPageFragment == null)
      mainPageFragment = new DogFragment();
    return mainPageFragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    mContext = getContext();
    View view = inflater.inflate(R.layout.activity_receivebeacon, null);
    sleepDog = getResources().getDrawable(R.drawable.dog1);
    angeryDog = getResources().getDrawable(R.drawable.dog2);
    intent = new Intent(getContext(),BeaconDetectService.class);
    cancel = false;
    dog = view.findViewById(R.id.dogImg);
    recevingmodesw = view.findViewById(R.id.recevingmode);
    MainActivity.receiveMode=false;
    recevingmodesw.setChecked(MainActivity.receiveMode);
    recevingmodesw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(ContactFragment.contacts.size() == 0) {
          recevingmodesw.setChecked(MainActivity.receiveMode);
          MainActivity.sendToast("No Contacts");
          return;
        }
        if (isChecked) {
          MainActivity.receiveMode=true;
          startBeaconReceive();
          recevingmodesw.setText("On");
          cancel=false;
        } else {
          recevingmodesw.setText("Off");
          stopBeaconService();
          RequestManager.armAlarm(MainActivity.user,"safe");
          BeaconDetectService.stopscan();
          ContactFragment.endcall();
          stopMedia();
          RequestManager.armAlarm(MainActivity.user,"safe");
          MainActivity.receiveMode=false;
          setSleepDog();
          cancel = true;
          RequestManager.armAlarm(MainActivity.user,"safe");
        }
      }
    });
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//    cancelBtn = view.findViewById(R.id.cancelBtn);
//    cancelBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        stopBeaconService();
//        ContactFragment.endcall();
//        stopMedia();
//        MainActivity.receiveMode=false;
//        cancel = true;
//      }
//    });
    return view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  void checkBluetooth() {
    if (mBluetoothAdapter != null) {
      if (!mBluetoothAdapter.isEnabled()) {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BluetoothRequestCode);
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == BluetoothRequestCode) {
//      if(resultCode == PackageManager.PERMISSION_GRANTED) {
//        startBeaconReceive();
//      }
    }
  }

  public void startBeaconReceive() {
    if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
      startBeaconService();
    }
  }
  public static void startBeaconService() {
    mContext.startService(intent);
  }

  public static void stopBeaconService() {
    Log.v("call", "stop service");
    mContext.stopService(intent);
    Log.v("call", "stop service finished");

  }
  public static void playMedia(int music_source, int volume) {
    AudioManager manager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
    manager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            AudioManager.ADJUST_RAISE);
    stopMedia();

    mediaPlayer = MediaPlayer.create(mContext, music_source);
    mediaPlayer.start();
  }


  @Override
  public void onPause() {
    super.onPause();
//    stopMedia();
//    stopBeaconService();
    recevingmodesw.setChecked(MainActivity.receiveMode);
  }

  @Override
  public void onResume() {
    super.onResume();
    recevingmodesw.setChecked(MainActivity.receiveMode);
    checkBluetooth();
  }

  public static void stopMedia() {
    Log.v("call", "stop media");
    if(mediaPlayer != null && mediaPlayer.isPlaying())
      mediaPlayer.stop();
    Log.v("call", "stop media finished");
  }
  public static void setSleepDog(){
    dog.setImageDrawable(sleepDog);
  }

  public static void setAngryDog(){
    dog.setImageDrawable(angeryDog);
  }
}
