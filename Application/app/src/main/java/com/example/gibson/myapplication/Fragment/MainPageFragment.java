package com.example.gibson.myapplication.Fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.R;
import com.example.gibson.myapplication.Services.RequestManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainPageFragment extends Fragment implements View.OnClickListener {

  private View _instance;
  ScheduledExecutorService checkLocation;

//  private MQTT_SERVICE mqtt_service;

//  TextView mqttStatusTV;
  static ListView listView;
//  JSONArray beaconArray;
  ArrayList<Beacon> beacons;
  ArrayList<HashMap<String,Object>> beaconList;
  int interval = 1000;

  private static MainPageFragment mainPageFragment;

  public static MainPageFragment getFragment() {
    if (mainPageFragment == null)
      mainPageFragment = new MainPageFragment();
    return mainPageFragment;
  }


  public static void reloadListView() {
    listView.invalidateViews();

  }

  public static byte[] getIdAsByte(java.util.UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_main, container, false);
    initialize(rootview);

    checkLocation = Executors.newSingleThreadScheduledExecutor();
    checkLocation.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
//        Log.v("test", "asd");
        if (ContextCompat.checkSelfPermission(rootview.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
          checkLocation.shutdownNow();
        }
      }
    }, 0, 4000, TimeUnit.MILLISECONDS);

    Executors.newScheduledThreadPool(1);

    // MQTT SERVICE
//    mqtt_service.setStatusTextView(mqttStatusTV);
//    mqtt_service.startConnect();

    // Bluetooth Get Adapter

    return rootview;
  }

  void initialize(ViewGroup group) {
//    mqttStatusTV = group.findViewById(R.id.mqttStatus);
//    publishET = group.findViewById(R.id.publishET);
//    scanBtn = group.findViewById(R.id.scanBtn);
//    sendBtn = group.findViewById(R.id.publishBtn);
//    scanBtn.setOnClickListener(this);
//    sendBtn.setOnClickListener(this);

    _instance = group.getRootView();


    // List View
    listView = group.findViewById(R.id.listView);

//    beaconArray = MainActivity.getDatabaseService().getBeacons();
    beacons = MainActivity.getDatabaseService().getBeacons();
    beaconList = new ArrayList<>();

    for (int i = 0; i < beacons.size(); i++) {
        Beacon beacon = beacons.get(i);
        HashMap<String, Object> dict = new HashMap();
        dict.put("name",beacon.name);
        dict.put("mac",beacon.MAC);
        dict.put("distance",0);
        beaconList.add(dict);
    }

    SimpleAdapter adapter = new SimpleAdapter(
            _instance.getContext(),
            beaconList,
            R.layout.beacon_list_row,
            new String[]{"name", "distance"},
            new int[] {R.id.beacon_nameTV, R.id.beacon_distanceTV}
    );
    listView.setAdapter(adapter);
  }

  void checkValue(String name, String text) {
    if (text != null) {
      Log.v(name, text);
    } else {
      Log.v(name, "none");
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
//      case R.id.scanBtn:
//        Button scanBtn = _instance.findViewById(R.id.scanBtn);
//        if(scanBtn.getText().equals("Scan")) {
//          Log.v("scanner", "Button");
//          mBluetoothLeScanner.startScan(mScanCallback);
//          scanBtn.setText("Stopped Scan");
//        } else {
//          mBluetoothLeScanner.stopScan(mScanCallback);
//          scanBtn.setText("Scan");
//        }
//        break;
//      case R.id.publishBtn:
//        mqtt_service.publishMessage("test", publishET.getText().toString());
//        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      Log.v("result", "qwe");
  }

  // OLD

//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    setContentView(R.layout.activity_main);
//    initialize();
//    mqtt_service = new MQTT_SERVICE(this, host, myTopic, username, password);
//    mqtt_service.setStatusTextView(mqttStatusTV);
//    mqtt_service.startConnect();
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 2);
//    }
//    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//  }

//  void initialize() {
//    statusTV = findViewById(R.id.statusTV);
//    rssiTV = findViewById(R.id.rssiTV);
//    mqttStatusTV = findViewById(R.id.mqttStatus);
//    distanceET = findViewById(R.id.distanceET);
//    accuracyTV = findViewById(R.id.accuracyTV);
//    txPowerTV = findViewById(R.id.txPowerTV);
//    publishET = findViewById(R.id.publishET);
//    macET = new EditText[6];
//    macET[0] = findViewById(R.id.macET1);
//    macET[1] = findViewById(R.id.macET2);
//    macET[2] = findViewById(R.id.macET3);
//    macET[3] = findViewById(R.id.macET4);
//    macET[4] = findViewById(R.id.macET5);
//    macET[5] = findViewById(R.id.macET6);
//
//    for(EditText i : macET) {
//      i.setOnKeyListener(this);
//      i.addTextChangedListener(this);
//    }
//
//  }

//  String getMacAddress(EditText[] mac) {
//    StringBuffer macAddress = new StringBuffer();
//    for(int i = 0; i < mac.length - 1; i ++) {
//      macAddress.append(mac[i].getText()+":");
//    }
//    macAddress.append(mac[5].getText());
//
//    return macAddress.toString();
//  }
//
//  void checkValue(String name, String text) {
//    if (text != null) {
//      Log.v(name, text);
//    } else {
//      Log.v(name, "none");
//    }
//  }
//
//  public double calculateAccuracy(int txPower, double rssi) {
//    if (rssi == 0) {
//      return -1.0; // if we cannot determine accuracy, return -1.
//    }
//    double ratio = rssi * 1.0 / txPower;
//    if (ratio < 1.0) {
//      return Math.pow(ratio, 10);
//    } else {
//      double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
//      return accuracy;
//    }
//  }
//
//  public double calculateDistance(int txPower, double rssi) {
//    double iRssi = Math.abs(rssi);
//    double power = (iRssi - 59) / (10 * 2.0);
//    return Math.pow(10, power);
//  }
//
//  private String getDistance(Double accuracy) {
//    if (accuracy == -1.0) {
//      return "Unknown";
//    } else if (accuracy < 1) {
//      return "Immediate";
//    } else if (accuracy < 3) {
//      return "Near";
//    } else {
//      return "Far";
//    }
//  }
//
//  @Override
//  public void onClick(View view) {
//    switch (view.getId()) {
//      case R.id.scanBtn:
//        Button scanBtn = findViewById(R.id.scanBtn);
//        if(scanBtn.getText().equals("Scan")) {
//          Log.v("scanner", "Button");
//          mBluetoothLeScanner.startScan(mScanCallback);
//          scanBtn.setText("Stopped Scan");
//        } else {
//          statusTV.setText("no");
//          rssiTV.setText("");
//          txPowerTV.setText("");
//          accuracyTV.setText("");
//          distanceET.setText("");
//          mBluetoothLeScanner.stopScan(mScanCallback);
//          scanBtn.setText("Scan");
//        }
//        break;
//      case R.id.publishBtn:
//        mqtt_service.publishMessage("test", publishET.getText().toString());
//        break;
//    }
//  }
//
//  @SuppressLint("ResourceType")
//  @Override
//  public boolean onKey(View view, int i, KeyEvent keyEvent) {
//
//    switch(view.getId()) {
//      case R.id.macET2:
//      case R.id.macET3:
//      case R.id.macET4:
//      case R.id.macET5:
//      case R.id.macET6:
//        EditText editText = (EditText) view;
//        if(editText.getText().length() == 0 && i == keyEvent.KEYCODE_DEL)
//          findViewById(view.getId() - 1).requestFocus();
//    }
//    return false;
//  }
//
//  @Override
//  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//  }
//
//  @Override
//  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//  }
//
//  @Override
//  public void afterTextChanged(Editable editable) {
//    Log.v("editable text length", editable.length() + "");
//    if(editable.length() >= 2) {
//      if(macET[0].hasFocus()) {
//        macET[1].requestFocus();
//      } else if(macET[1].hasFocus()) {
//        macET[2].requestFocus();
//      } else if(macET[2].hasFocus()) {
//        macET[3].requestFocus();
//      } else if(macET[3].hasFocus()) {
//        macET[4].requestFocus();
//      } else if(macET[4].hasFocus()) {
//        macET[5].requestFocus();
//      } else if(macET[5].hasFocus()) {
//        View view = this.getCurrentFocus();
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//      }
//    }
//  }
}
