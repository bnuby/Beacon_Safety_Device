package com.example.gibson.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


/**
 * Created by gibson on 19/03/2018.
 */

public class AltBeaconActivity extends AppCompatActivity implements BeaconConsumer {


  protected static final String TAG = "MonitoringActivity";
  private BeaconManager beaconManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    beaconManager = BeaconManager.getInstanceForApplication(this);
    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
    // type.  Do a web search for "setBeaconLayout" to get the proper expression.

    // AltBeacon
    beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

    // IBeacon
    beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

    // EddyStone TLM
    beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

    // EddyStone UID
    beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

    // EDDYSTONE URL
    beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
    beaconManager.bind(this);
  }
  @Override
  protected void onDestroy() {
    super.onDestroy();
    beaconManager.unbind(this);
  }
  @Override
  public void onBeaconServiceConnect() {
    beaconManager.addMonitorNotifier(new MonitorNotifier() {
      @Override
      public void didEnterRegion(Region region) {
        Log.i(TAG, "I just saw an beacon for the first time!");
      }

      @Override
      public void didExitRegion(Region region) {
        Log.i(TAG, "I no longer see an beacon");
      }

      @Override
      public void didDetermineStateForRegion(int state, Region region) {
        Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state + region.getUniqueId());
      }
    });

    try {
      beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
    } catch (RemoteException e) {    }
  }


//  final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
//  final String TAG = "AltBeacon";
//  private BeaconManager mBeaconManager;
//
//  @Override
//  protected void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_main);
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Request Location Permissions");
//        builder.setMessage("Please Granted Permissions for Beacon.");
//        builder.setPositiveButton(android.R.string.ok, null);
//        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//          @Override
//          public void onDismiss(DialogInterface dialogInterface) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//          }
//        });
//        builder.show();
//      }
//    }
//
//    mBeaconManager = BeaconManager.getInstanceForApplication(this);
//    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
//    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
//    mBeaconManager.getBeaconParsers().add(new BeaconParser().
//            setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//    mBeaconManager.getBeaconParsers().add(new BeaconParser().
//            setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//    mBeaconManager.bind(this);
//  }
//
//  @Override
//  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    switch (requestCode) {
//      case PERMISSION_REQUEST_COARSE_LOCATION:
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//          Log.d(TAG, "coarse location permission granted");
//        } else {
//          final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//          builder.setTitle("Functionality limited");
//          builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
//          builder.setPositiveButton(android.R.string.ok, null);
//          builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//              requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//            }
//
//          });
//          builder.show();
//        }
//        return;
//    }
//  }
//
//  public void onResume() {
//    super.onResume();
//    mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
//    // Detect the main Eddystone-UID frame:
////    mBeaconManager.getBeaconParsers().add(new BeaconParser().
////            setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
//    mBeaconManager.getBeaconParsers().add(new BeaconParser().
//            setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//    mBeaconManager.getBeaconParsers().add(new BeaconParser().
//            setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//    Log.i("test","test");
//    mBeaconManager.bind(this);
//  }
//
//  @Override
//  public void onBeaconServiceConnect() {
//    // Set the two identifiers below to null to detect any beacon regardless of identifiers
////    Identifier myBeaconNamespaceId = Identifier.parse("0x2f234454f4911ba9ffa6");
////    Identifier myBeaconInstanceId = Identifier.parse("0x000000000001");
//    Region region = new Region("my-beacon-region", null, null, null);
//    mBeaconManager.addMonitorNotifier(this);
//    try {
//      mBeaconManager.startMonitoringBeaconsInRegion(region);
//    } catch (RemoteException e) {
//      e.printStackTrace();
//    }
//  }
//
//  public void didEnterRegion(Region region) {
//    Log.d(TAG, "I detected a beacon in the region with namespace id " + region.getId1() +
//            " and instance id: " + region.getId2());
//  }
//
//  public void didExitRegion(Region region) {
//  }
//
//  public void didDetermineStateForRegion(int state, Region region) {
//    Log.d(TAG, "I detected a beacon in the region with namespace id " + region.getUniqueId() +
//            " and instance id: " + region.getId2() + region.toString());
//  }
//
//  @Override
//  public void onPause() {
//    super.onPause();
//    mBeaconManager.unbind(this);
//  }
//
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    mBeaconManager.unbind(this);
//  }

}
