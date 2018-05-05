package com.example.gibson.myapplication;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gibson on 21/03/2018.
 */

public class BeaconActivity extends Fragment implements View.OnClickListener, TextWatcher, View.OnKeyListener{

  private ListView beacon_listView;
  private JSONArray beaconArray;
  private View _instance;
  AlertDialog dialog;
  EditText nameET;
  EditText[] macET;
  EditText distanceET;
  View dialog_layout;
  Button addBtn;

//  @Override
//  protected void onCreate(@Nullable Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_beacon);
//    Toolbar toolbar = findViewById(R.id.toolbar_beacon);
//
//    setSupportActionBar(toolbar);
//    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    init();
//  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    _instance = inflater.inflate(R.layout.activity_beacon, null);
    init(_instance);
    return _instance;
  }

  private void init(View view) {
    macET = new EditText[6];
    beacon_listView = view.findViewById(R.id.beacon_table_list);
    beaconArray = MainActivity.getDatabaseService().getBeacons();
    addBtn = view.findViewById(R.id.beacon_addBtn);
    addBtn.setOnClickListener(this);
    BeaconTableAdapter adapter = new BeaconTableAdapter(_instance.getContext());
    beacon_listView.setAdapter(adapter);
    beacon_listView.setMotionEventSplittingEnabled(true);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      // Add Dialog
      case R.id.action_beacon_addBtn:

        dialog_layout = getLayoutInflater().inflate(R.layout.dialog_beacon_add, null);
        nameET = dialog_layout.findViewById(R.id.beacon_dialog_nameET);
        for(int i = 1; i <= 6; i++) {
          macET[i-1] = dialog_layout.findViewById(getResources().getIdentifier("dialog_beacon_macET"+i, "id", _instance.getContext().getPackageName()));
          macET[i-1].setOnKeyListener(this);
          macET[i-1].addTextChangedListener(this);
        }

        distanceET = dialog_layout.findViewById(R.id.beacon_dialog_distanceET);
        final Button okBtn = dialog_layout.findViewById(R.id.beacon_dialog_addBtn);
        final Button cancelBtn = dialog_layout.findViewById(R.id.beacon_dialog_cancelBtn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(_instance.getContext())
                .setView(dialog_layout)
                .setTitle("Add Beacon");
        dialog = builder.create();
        dialog.create();
        dialog.show();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View view) {
    String mac;
    switch(view.getId()) {
      case R.id.beacon_addBtn:
        dialog_layout = getLayoutInflater().inflate(R.layout.dialog_beacon_add, null);
        nameET = dialog_layout.findViewById(R.id.beacon_dialog_nameET);
        for(int i = 1; i <= 6; i++) {
          macET[i-1] = dialog_layout.findViewById(getResources().getIdentifier("dialog_beacon_macET"+i, "id", _instance.getContext().getPackageName()));
          macET[i-1].setOnKeyListener(this);
          macET[i-1].addTextChangedListener(this);
        }
//        macET = dialog_layout.findViewById(R.id.beacon_dialog_macET);
        distanceET = dialog_layout.findViewById(R.id.beacon_dialog_distanceET);
        final Button okBtn = dialog_layout.findViewById(R.id.beacon_dialog_addBtn);
        final Button cancelBtn = dialog_layout.findViewById(R.id.beacon_dialog_cancelBtn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(_instance.getContext())
                .setView(dialog_layout)
                .setTitle("Add Beacon");
        dialog = builder.create();
        dialog.create();
        dialog.show();
        break;

      case R.id.beacon_dialog_addBtn:
          String name = nameET.getText().toString();
          mac = MainPageActivity.getMacAddress(macET);
          String distanceText = distanceET.getText().toString();
          double distance = distanceText.equals("") ? 0: Double.parseDouble(distanceText.toString());
          if(mac.length() < 17) {
            Toast.makeText(_instance.getContext(), "Mac Address is too short!", Toast.LENGTH_SHORT).show();
            break;
          }
          long status = MainActivity.getDatabaseService().insertBeacon(name, mac, distance);
          if(status != -1)
            try {
              beaconArray.put(new JSONObject(String.format("{'mac':'%s', 'name':'%s', 'alert_distance':%f}", mac, name, distance)));
              beacon_listView.invalidateViews();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          else
            new AlertDialog.Builder(_instance.getContext()).setMessage("Mac Address is Used!")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) { }}).create().show();
          dialog.cancel();
        break;

      case R.id.beacon_dialog_cancelBtn:
        dialog.cancel();
        break;

      case R.id.beacon_dialog_updateBtn:
        try {
          JSONObject object = beaconArray.getJSONObject((Integer) view.getTag());
          name = nameET.getText().toString();
          mac = MainPageActivity.getMacAddress(macET);
          distance = Double.parseDouble(distanceET.getText().toString());
          Log.v("name", name);
          Log.v("mac", mac);
          Log.v("distance", distance + "");

          MainActivity.getDatabaseService().updateBeacon(object.getString("mac"),mac, name, distance);
          object.put("mac", mac);
          object.put("name", name);
          object.put("alert_distance", distance);
          beacon_listView.invalidateViews();
          dialog.cancel();
        } catch (JSONException e) {
          e.printStackTrace();
        }
        break;

      case R.id.beacon_editBtn:
        Button btn =  (Button)view;
        dialog_layout = getLayoutInflater().inflate(R.layout.dialog_beacon_update, null);
        nameET = dialog_layout.findViewById(R.id.beacon_dialog_nameET);
        distanceET = dialog_layout.findViewById(R.id.beacon_dialog_distanceET);
        macET = new EditText[6];
        try {
          JSONObject object = beaconArray.getJSONObject((Integer) btn.getTag());
          nameET.setText(object.getString("name"));
          String[] macArray = object.getString("mac").split(":");
          distanceET.setText(object.getString("alert_distance"));
          for (int i = 1; i <= macET.length; i ++) {
            macET[i-1] = dialog_layout.findViewById(getResources().getIdentifier("dialog_beacon_macET" + i, "id", dialog_layout.getContext().getPackageName()));
            macET[i-1].setText(macArray[i - 1]);
          }
          AlertDialog.Builder builder2 = new AlertDialog.Builder(_instance.getContext())
                  .setView(dialog_layout)
                  .setTitle("Add Beacon");
          dialog_layout.findViewById(R.id.beacon_dialog_updateBtn).setTag(btn.getTag());
          dialog_layout.findViewById(R.id.beacon_dialog_updateBtn).setOnClickListener(this);
          dialog_layout.findViewById(R.id.beacon_dialog_cancelBtn).setOnClickListener(this);
          dialog = builder2.create();
          dialog.create();
          dialog.show();
        } catch (JSONException e) {
          e.printStackTrace();
        }

        break;

      case R.id.beacon_deleteBtn:
        try {
          int i = Integer.parseInt(view.getTag()+"");
          mac = beaconArray.getJSONObject(i).getString("mac");
          status = MainActivity.getDatabaseService().deleteBeacon(mac);
          if(status == 1) {
            beaconArray.remove(i);
            beacon_listView.invalidateViews();
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }
        break;
      default:
    }
  }

  @SuppressLint("ResourceType")
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    switch(view.getId()) {
      case R.id.dialog_beacon_macET2:
      case R.id.dialog_beacon_macET3:
      case R.id.dialog_beacon_macET4:
      case R.id.dialog_beacon_macET5:
      case R.id.dialog_beacon_macET6:
        EditText editText = (EditText) view;
        if(editText.getText().length() == 0 && i == keyEvent.KEYCODE_DEL)
          dialog_layout.findViewById(view.getId() - 1).requestFocus();
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
      return beaconArray.length();
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
      if(view1 == null){
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
      editBtn.setOnClickListener(BeaconActivity.this);
      deleteBtn.setOnClickListener(BeaconActivity.this);

      try {
        final JSONObject currentObj = beaconArray.getJSONObject(i);
        final String mac = currentObj.getString("mac");
        macTV.setText(mac);
        nameTV.setText(currentObj.getString("name"));
        distanceTV.setText(currentObj.getDouble("alert_distance")+"");


      } catch (JSONException e) {
        e.printStackTrace();
      }

      return view1;
    }
  }
}
