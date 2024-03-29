package com.example.gibson.myapplication.Services;

import android.app.KeyguardManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.example.gibson.myapplication.Fragment.ContactFragment;
import com.example.gibson.myapplication.Fragment.DogFragment;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeaconDetectService extends Service {
  static ArrayList<HashMap<String,Object>> beaconList;
  public static RequestQueue queue;
  private static int no = 0;


  static BluetoothAdapter mBluetoothAdapter;
  static BluetoothLeScanner mBluetoothLeScanner;
  static Context mContext;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  protected static ScanCallback mScanCallback = new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      ScanRecord mScanRecord = result.getScanRecord();
      byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
      int mRssi = result.getRssi();
      int txPower = result.getScanRecord().getTxPowerLevel();
      float distance = MainActivity.mSharedPreferences.getFloat("distance", 1);
      String user = "ben";
      Log.v("Beacon Address", result.getDevice().getAddress());

      for(HashMap<String, Object> i : beaconList) {
        if(result.getDevice().getAddress().equalsIgnoreCase((String) i.get("mac"))) {
          Log.v("test", "" + calculateDistance(txPower, mRssi));
          Log.v("test1",""+ MainActivity.mSharedPreferences.getFloat("distance",1));
          if(distance >= calculateDistance(txPower, mRssi)) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            user = ContactFragment.contacts.get(no).recipientID;
            PowerManager pm=(PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "wake");
            wl.acquire();
            wl.release();
            KeyguardManager km= (KeyguardManager) mContext.getSystemService(mContext.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();

            DogFragment.playMedia(R.raw.dog2, 15);
            RequestManager.armAlarm(MainActivity.user,"danger");
            DogFragment.setAngryDog();
            RequestManager.armAlarm(MainActivity.user,"danger");
            ContactFragment.callUser(mContext, user);
            RequestManager.armAlarm(MainActivity.user,"danger");
            no = (no + 1) %ContactFragment.contacts.size();

          } else if (distance * 1.4 >= calculateDistance(txPower, mRssi)) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            user = ContactFragment.contacts.get(no).recipientID;
            PowerManager pm=(PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "wake");
            wl.acquire();
            wl.release();
            KeyguardManager km= (KeyguardManager) mContext.getSystemService(mContext.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();

            DogFragment.playMedia(R.raw.dog2, 10);
            RequestManager.armAlarm(MainActivity.user,"warning");
            DogFragment.setAngryDog();
            RequestManager.armAlarm(MainActivity.user,"warning");
            ContactFragment.callUser(mContext, user);
            RequestManager.armAlarm(MainActivity.user,"warning");
            no = (no + 1) %ContactFragment.contacts.size();

          } else {
            DogFragment.stopMedia();
            RequestManager.armAlarm(MainActivity.user,"safe");
            DogFragment.setSleepDog();
            ContactFragment.endcall();
          }
          i.put("distance", String.format("%.3f",calculateDistance(txPower, mRssi)));
        }
      }
    }


    @Override
    public void onScanFailed(int errorCode) {
      super.onScanFailed(errorCode);
      Log.v("ScanTask", "Some error occurred" + errorCode);
    };

  };
  public static void stopscan(){
    try{
      mBluetoothLeScanner.stopScan(mScanCallback);
    }catch (Exception e){
      Log.d("fail to stop scan","stopscan: "+e.getStackTrace().toString());
    }

  }



  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("BeaconDeyectService", "onCreate: ");
    mContext = getBaseContext();

    ArrayList<Beacon> beacons = MainActivity.getDatabaseService().getBeacons();

    beaconList = new ArrayList<>();

    // Initial Cache
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);

    // Set up the network to use HttpURLConnection as the HTTP client.
    Network network = new BasicNetwork(new HurlStack());
    if(queue == null)
      queue = new RequestQueue(cache, network);
    queue.start();

    for (int i = 0; i < beacons.size(); i++) {
      Beacon beacon = beacons.get(i);
      HashMap<String, Object> dict = new HashMap();
      dict.put("name",beacon.name);
      dict.put("mac",beacon.MAC);
      dict.put("alert_distance", beacon.alert_distance);
      dict.put("distance",0);
      beaconList.add(dict);
    }

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // check Bluetooth
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // check Bluetooth
    requestBluetoothScan();


//    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
//            new Runnable() {
//              @Override
//              public void run() {
//                notificationManager.notify(1, builder.build());
//              }
//            }, 1000, 1000, MILLISECONDS);


    return Service.START_STICKY;
  }

  static void requestBluetoothScan() {
    final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(
            new Runnable() {
              @Override
              public void run() {
                if(mBluetoothAdapter.isEnabled()) {
                  Log.v("BeaconDetectService", "true");

                  mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

//                  // Blue Scan Filters
                  ArrayList<ScanFilter> filters = new ArrayList<>();

                  for(int i = 0; i < beaconList.size(); i++) {
                    Log.v("mac", String.valueOf(beaconList.get(i).get("mac")).toUpperCase());
                    ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(String.valueOf(beaconList.get(i).get("mac")).toUpperCase()).build();
                    filters.add(filter);
                  }

                  // Bluetooth Scann Setting
                  ScanSettings settings = new ScanSettings.Builder()
                          .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                          .setReportDelay(500)
                          .build();

//                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//                  builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

                  mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

//                  mBluetoothLeScanner.startScan(mScanCallback);
                  service.shutdownNow();
                }
              }
            }, 0, 3000, TimeUnit.MILLISECONDS);
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

  public static double calculateDistance(int txPower, double rssi) {
    Log.v("TXPOWER", String.valueOf(txPower));
    Log.v("RSSI", String.valueOf(rssi));
    double iRssi = Math.abs(rssi);
//    double power = (iRssi - 59) / (15 * 2.0);
//    return Math.pow(10, power);
    return (1.12900922 * Math.pow(10, -13) * Math.pow(iRssi, 7.068735405));
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
  public void onDestroy() {
    if(mBluetoothLeScanner != null)
      mBluetoothLeScanner.stopScan(mScanCallback);
    queue.stop();
    super.onDestroy();
  }
}
