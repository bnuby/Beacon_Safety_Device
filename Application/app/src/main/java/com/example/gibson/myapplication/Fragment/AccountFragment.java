package com.example.gibson.myapplication.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.LoginActivity;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.R;
import com.example.gibson.myapplication.Services.RequestManager;

import java.util.ArrayList;

public class AccountFragment extends Fragment implements View.OnClickListener, View.OnKeyListener, TextWatcher {

  // Account View
  TextView nameTV;
  TextView callerIDTV;
  Button receiveBtn;
  Button logoutBtn;
  private   View accountView;
  EditText[] macET;
  EditText nameET;
  private static ListView beacon_listView;
  private static ArrayList<Beacon> beacons;
  Button addBtn;
  View dialog_layout;
  EditText distanceET;
  AlertDialog dialog;

  private static AccountFragment accountFragment;

  public static AccountFragment getFragment() {
    if (accountFragment == null)
      accountFragment = new AccountFragment();
    return accountFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    // Account View
    accountView = inflater.inflate(R.layout.fragment_account, container, false);
    nameTV = accountView.findViewById(R.id.nameTV);
    callerIDTV = accountView.findViewById(R.id.callerTV);

    nameTV.setText(MainActivity.user.name);
    callerIDTV.setText(MainActivity.user.callerID);
    //receiveBtn = accountView.findViewById(R.id.receiveBtn);
    logoutBtn = accountView.findViewById(R.id.logOutBtn);

    logoutBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MainActivity.getDatabaseService().logout();
        ((Activity) getContext()).finish();
        LoginActivity.stopSinch();

//        RequestManager.logoutRequest();
//        Toast.makeText(getContext(), "Logout!", Toast.LENGTH_SHORT).show();
      }
    });



    /*receiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), ReceiveBeaconActivity.class);
        MainActivity.receiveMode = true;
        startActivity(intent);
      }
    });*/
    // Inflate the layout for this fragment
    init(accountView);
      return accountView;
  }
  private void init(View view) {
    macET = new EditText[6];
    beacon_listView = view.findViewById(R.id.beacon_list);
    beacons = new ArrayList<>();
    beacon_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PopupMenu contact_menu=new PopupMenu(getActivity(), view);
        contact_menu.getMenuInflater().inflate(R.menu.account_menu,contact_menu.getMenu());
        contact_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
              case R.id.delete:

                break;
              case R.id.edit:


                break;

            }
            return true;
          }
        });
        return false;
      }
    });

//    beaconArray = MainActivity.getDatabaseService().getBeacons();

    beacons = MainActivity.getDatabaseService().getBeacons();
    addBtn = view.findViewById(R.id.beacon_addbtn);
    addBtn.setOnClickListener(this);
    AccountFragment.BeaconTableAdapter adapter = new AccountFragment.BeaconTableAdapter(accountView.getContext());
    beacon_listView.setAdapter(adapter);
    beacon_listView.setMotionEventSplittingEnabled(true);
    RequestManager.getBeaconData(MainActivity.user);
  }

  @Override
  public void onClick(View v) {
    String mac;
    Beacon beacon;
    switch (v.getId()) {
      case R.id.beacon_addbtn:
        dialog_layout = getLayoutInflater().inflate(R.layout.dialog_beacon_add, null);
        nameET = dialog_layout.findViewById(R.id.beacon_dialog_nameET);
        for (int i = 1; i <= 6; i++) {
          macET[i - 1] = dialog_layout.findViewById(getResources().getIdentifier("dialog_beacon_macET" + i, "id", accountView.getContext().getPackageName()));
          macET[i - 1].setOnKeyListener(this);
          macET[i - 1].addTextChangedListener(this);
        }
//        macET = dialog_layout.findViewById(R.id.beacon_dialog_macET);
        distanceET = dialog_layout.findViewById(R.id.beacon_dialog_distanceET);
        final Button okBtn = dialog_layout.findViewById(R.id.beacon_dialog_addBtn);
        final Button cancelBtn = dialog_layout.findViewById(R.id.beacon_dialog_cancelBtn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder( accountView.getContext())
                .setView(dialog_layout)
                .setTitle("Add Beacon");
        dialog = builder.create();
        dialog.create();
        dialog.show();
        break;

      case R.id.beacon_dialog_addBtn:
        String name = nameET.getText().toString();
        mac = getMacAddress(macET);
        String distanceText = distanceET.getText().toString();
        double distance = distanceText.equals("") ? 0 : Double.parseDouble(distanceText.toString());
        if (mac.length() < 17) {
          Toast.makeText(accountView.getContext(), "Mac Address is too short!", Toast.LENGTH_SHORT).show();
          break;
        }
        long status = MainActivity.getDatabaseService().insertBeacon(name, mac, distance);
        if (status != -1) {
//            try {
//            beaconArray.put(new JSONObject(String.format("{'mac':'%s', 'name':'%s', 'alert_distance':%f}", mac, name, distance)));
          beacon = new Beacon(name, mac, distance);
          beacons.add(beacon);
          RequestManager.insertBeaconData(MainActivity.user, beacon);
          beacon_listView.invalidateViews();
//            } catch (JSONException e) {
//              e.printStackTrace();
//            }
        } else
          new AlertDialog.Builder(accountView.getContext()).setMessage("Mac Address is Used!")
                  .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                  }).create().show();
        dialog.cancel();
        break;

      case R.id.beacon_dialog_cancelBtn:
        dialog.cancel();
        break;

      case R.id.beacon_dialog_updateBtn:
//        try {
//          JSONObject object = beaconArray.getJSONObject((Integer) view.getTag());
        beacon = beacons.get((Integer) v.getTag());

        name = nameET.getText().toString();
        mac = getMacAddress(macET);
        distance = Double.parseDouble(distanceET.getText().toString());

        Log.v("name", name);
        Log.v("mac", mac);
        Log.v("distance", distance + "");

//          MainActivity.getDatabaseService().updateBeacon(object.getString("mac"),mac, name, distance);
//          object.put("mac", mac);
//          object.put("name", name);
//          object.put("alert_distance", distance);
        Beacon newBeacon = new Beacon( name, mac, distance);
        MainActivity.getDatabaseService().updateBeacon(beacon.MAC, newBeacon);
        RequestManager.updateBeaconData(MainActivity.user, beacon.MAC, newBeacon);
        beacon.MAC = mac;
        beacon.name = name;
        beacon.alert_distance = distance;
        beacon_listView.invalidateViews();
        dialog.cancel();
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
        break;

      case R.id.beacon_editBtn:
        Button btn = (Button) v;
        dialog_layout = getLayoutInflater().inflate(R.layout.dialog_beacon_update, null);
        nameET = dialog_layout.findViewById(R.id.beacon_dialog_nameET);
        distanceET = dialog_layout.findViewById(R.id.beacon_dialog_distanceET);
        macET = new EditText[6];
//        try {
//          JSONObject object = beaconArray.getJSONObject((Integer) btn.getTag());
//          nameET.setText(object.getString("name"));
//          String[] macArray = object.getString("mac").split(":");
//          distanceET.setText(object.getString("alert_distance"));

        beacon = beacons.get((Integer) btn.getTag());
        nameET.setText(beacon.name);
        String[] macArray = beacon.MAC.split(":");
        distanceET.setText(beacon.alert_distance + "");


        for (int i = 1; i <= macET.length; i++) {
          macET[i - 1] = dialog_layout.findViewById(getResources().getIdentifier("dialog_beacon_macET" + i, "id", dialog_layout.getContext().getPackageName()));
          macET[i - 1].setText(macArray[i - 1]);
        }
        AlertDialog.Builder builder2 = new AlertDialog.Builder(accountView.getContext())
                .setView(dialog_layout)
                .setTitle("Add Beacon");
        dialog_layout.findViewById(R.id.beacon_dialog_updateBtn).setTag(btn.getTag());
        dialog_layout.findViewById(R.id.beacon_dialog_updateBtn).setOnClickListener(this);
        dialog_layout.findViewById(R.id.beacon_dialog_cancelBtn).setOnClickListener(this);
        dialog = builder2.create();
        dialog.create();
        dialog.show();
//        } catch (JSONException e) {
//          e.printStackTrace();
//        }

        break;

      case R.id.beacon_deleteBtn:
//        try {
        int i = Integer.parseInt(v.getTag() + "");
//          mac = beaconArray.getJSONObject(i).getString("mac");
        mac = beacons.get(i).MAC;
        status = MainActivity.getDatabaseService().deleteBeacon(mac);
        RequestManager.deleteBeaconData(MainActivity.user, beacons.get(i));
        if (status == 1) {
//            beaconArray.remove(i);
          beacons.remove(i);
          beacon_listView.invalidateViews();
        }

//        } catch (JSONException e) {
//          e.printStackTrace();
//        }
        break;
      default:
    }
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    switch (v.getId()) {
      case R.id.dialog_beacon_macET2:
      case R.id.dialog_beacon_macET3:
      case R.id.dialog_beacon_macET4:
      case R.id.dialog_beacon_macET5:
      case R.id.dialog_beacon_macET6:
        EditText editText = (EditText) v;
        if (editText.getText().length() == 0 && keyCode == event.KEYCODE_DEL)
          dialog_layout.findViewById(v.getId() - 1).requestFocus();
    }
    return false;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    Log.v("editable text length", s.length() + "");
    if (s.length() >= 2) {
      if (macET[0].hasFocus()) {
        macET[1].requestFocus();
      } else if (macET[1].hasFocus()) {
        macET[2].requestFocus();
      } else if (macET[2].hasFocus()) {
        macET[3].requestFocus();
      } else if (macET[3].hasFocus()) {
        macET[4].requestFocus();
      } else if (macET[4].hasFocus()) {
        macET[5].requestFocus();
      } else if (macET[5].hasFocus()) {
        distanceET.requestFocus();
      }
    }
  }

  public class BeaconTableAdapter extends BaseAdapter {

    private Context context;

    public BeaconTableAdapter(Context context) {
      this.context = context;
    }

    @Override
    public int getCount() {
//      return beaconArray.length();
      return beacons.size();
    }

    @Override
    public Object getItem(int i) {
      return i;
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
      View view1 = view;
      if (view1 == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view1 = inflater.inflate(R.layout.beacon_list_row2, null);
      }

      TextView nameTV = view1.findViewById(R.id.beacon_nameTV);
      TextView macTV = view1.findViewById(R.id.beacon_macTV);
      TextView distanceTV = view1.findViewById(R.id.beacon_distanceTV);
      Button deleteBtn = view1.findViewById(R.id.beacon_deleteBtn);
      Button editBtn = view1.findViewById(R.id.beacon_editBtn);
      deleteBtn.setTag(i);
      editBtn.setTag(i);
      editBtn.setOnClickListener((View.OnClickListener) AccountFragment.this);
      deleteBtn.setOnClickListener((View.OnClickListener) AccountFragment.this);

//      try {
//        final JSONObject currentObj = beaconArray.getJSONObject(i);
      final Beacon current = beacons.get(i);

//        final String mac = currentObj.getString("mac");
      final String mac = current.MAC;

      macTV.setText(mac);
//        nameTV.setText(currentObj.getString("name"));
      nameTV.setText(current.name);
      distanceTV.setText(current.alert_distance + "");


//      } catch (JSONException e) {
//        e.printStackTrace();
//      }

      return view1;
    }
  }
  public static String getMacAddress(EditText[] mac) {
    StringBuffer macAddress = new StringBuffer();
    for (int i = 0; i < mac.length - 1; i++) {
      macAddress.append(mac[i].getText() + ":");
    }
    macAddress.append(mac[5].getText());
    return macAddress.toString();
  }
    public static void updateBeaconArray(ArrayList<Beacon> beacons) {
//    beaconArray = array;
        AccountFragment.beacons = beacons;
        beacon_listView.invalidateViews();
    }


}
