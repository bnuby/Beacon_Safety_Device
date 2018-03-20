package com.example.gibson.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.ByteBuffer;

import static com.example.gibson.myapplication.MainViewPager.mqtt_service;


public class MainActivity extends Fragment implements View.OnClickListener, EditText.OnKeyListener, TextWatcher {

  private View _instance;
  public BluetoothAdapter mBluetoothAdapter;
  public BluetoothLeScanner mBluetoothLeScanner;

//  private MQTT_SERVICE mqtt_service;

  TextView rssiTV;
  TextView statusTV;
  TextView distanceTV;
  TextView accuracyTV;
  TextView txPowerTV;
  TextView mqttStatusTV;
  EditText publishET;
  EditText []macET;
  Button scanBtn;
  Button sendBtn;

  protected ScanCallback mScanCallback = new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      ScanRecord mScanRecord = result.getScanRecord();
      byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
      int mRssi = result.getRssi();
      int txPower = result.getScanRecord().getTxPowerLevel();
      if(result.getDevice().getAddress().equalsIgnoreCase(getMacAddress(macET))) {
        statusTV.setText("yes");
        rssiTV.setText(String.valueOf(mRssi));
        txPowerTV.setText(String.valueOf(txPower));
        accuracyTV.setText(String.valueOf(calculateAccuracy(txPower, mRssi)));
        distanceTV.setText(String.valueOf(calculateDistance(txPower, mRssi)));
      }
    }
  };

  public static byte[] getIdAsByte(java.util.UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_main, container, false);
    initialize(rootview);

    // MQTT SERVICE
    mqtt_service.setStatusTextView(mqttStatusTV);
    mqtt_service.startConnect();

    // Bluetooth Get Adapter
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 2);
    }
    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

    return rootview;
  }

  void initialize(ViewGroup group) {
    statusTV = group.findViewById(R.id.statusTV);
    rssiTV = group.findViewById(R.id.rssiTV);
    mqttStatusTV = group.findViewById(R.id.mqttStatus);
    distanceTV = group.findViewById(R.id.distanceTV);
    accuracyTV = group.findViewById(R.id.accuracyTV);
    txPowerTV = group.findViewById(R.id.txPowerTV);
    publishET = group.findViewById(R.id.publishET);
    scanBtn = group.findViewById(R.id.scanBtn);
    sendBtn = group.findViewById(R.id.publishBtn);

    scanBtn.setOnClickListener(this);
    sendBtn.setOnClickListener(this);

    macET = new EditText[6];
    macET[0] = group.findViewById(R.id.macET1);
    macET[1] = group.findViewById(R.id.macET2);
    macET[2] = group.findViewById(R.id.macET3);
    macET[3] = group.findViewById(R.id.macET4);
    macET[4] = group.findViewById(R.id.macET5);
    macET[5] = group.findViewById(R.id.macET6);

    _instance = group.getRootView();
    for(EditText i : macET) {
      i.setOnKeyListener(this);
      i.addTextChangedListener(this);
    }
  }


  String getMacAddress(EditText[] mac) {
    StringBuffer macAddress = new StringBuffer();
    for(int i = 0; i < mac.length - 1; i ++) {
      macAddress.append(mac[i].getText()+":");
    }
    macAddress.append(mac[5].getText());

    return macAddress.toString();
  }

  void checkValue(String name, String text) {
    if (text != null) {
      Log.v(name, text);
    } else {
      Log.v(name, "none");
    }
  }

  public double calculateAccuracy(int txPower, double rssi) {
    if (rssi == 0) {
      return -1.0; // if we cannot determine accuracy, return -1.
    }
    double ratio = rssi * 1.0 / txPower;
    if (ratio < 1.0) {
      return Math.pow(ratio, 10);
    } else {
      double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
      return accuracy;
    }
  }

  public double calculateDistance(int txPower, double rssi) {
    double iRssi = Math.abs(rssi);
    double power = (iRssi - 59) / (10 * 2.0);
    return Math.pow(10, power);
  }

  private String getDistance(Double accuracy) {
    if (accuracy == -1.0) {
      return "Unknown";
    } else if (accuracy < 1) {
      return "Immediate";
    } else if (accuracy < 3) {
      return "Near";
    } else {
      return "Far";
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.scanBtn:
        Button scanBtn = _instance.findViewById(R.id.scanBtn);
        if(scanBtn.getText().equals("Scan")) {
          Log.v("scanner", "Button");
          mBluetoothLeScanner.startScan(mScanCallback);
          scanBtn.setText("Stopped Scan");
        } else {
          statusTV.setText("no");
          rssiTV.setText("");
          txPowerTV.setText("");
          accuracyTV.setText("");
          distanceTV.setText("");
          mBluetoothLeScanner.stopScan(mScanCallback);
          scanBtn.setText("Scan");
        }
        break;
      case R.id.publishBtn:
        mqtt_service.publishMessage("test", publishET.getText().toString());
        break;
    }
  }

  @SuppressLint("ResourceType")
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    switch(view.getId()) {
      case R.id.macET2:
      case R.id.macET3:
      case R.id.macET4:
      case R.id.macET5:
      case R.id.macET6:
        EditText editText = (EditText) view;
        if(editText.getText().length() == 0 && i == keyEvent.KEYCODE_DEL)
          _instance.findViewById(view.getId() - 1).requestFocus();
    }
    return false;
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void afterTextChanged(Editable editable) {
    Log.v("editable text length", editable.length() + "");
    if(editable.length() >= 2) {
      if(macET[0].hasFocus()) {
        macET[1].requestFocus();
      } else if(macET[1].hasFocus()) {
        macET[2].requestFocus();
      } else if(macET[2].hasFocus()) {
        macET[3].requestFocus();
      } else if(macET[3].hasFocus()) {
        macET[4].requestFocus();
      } else if(macET[4].hasFocus()) {
        macET[5].requestFocus();
      } else if(macET[5].hasFocus()) {
        View view = _instance;
        InputMethodManager inputMethodManager = (InputMethodManager) _instance.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }

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
//    distanceTV = findViewById(R.id.distanceTV);
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
//          distanceTV.setText("");
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
