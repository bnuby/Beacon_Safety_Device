package com.example.gibson.myapplication.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.CallingActivity;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Contact;
import com.example.gibson.myapplication.R;
import com.example.gibson.myapplication.Services.RequestManager;
import com.example.gibson.myapplication.Services.SinchLoginService;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;

/**
 * Created by gibson on 20/03/2018.
 */

public class ContactFragment extends Fragment implements View.OnClickListener {

  private static final String TAG = "ContactFragment";
  final int REQUESTCALL = 2;
  static ListView listView;
  private String callerId = "callerId";
  ArrayList<String> strings = new ArrayList<>();
  static ArrayList<Contact> contacts;
  private Button addBtn;
  private static SinchLoginService.SinchBinder sinchBinder;

  private static ContactFragment contactFragment;


  AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Toast.makeText(getContext(), "asd", Toast.LENGTH_LONG).show();
      callUser(getContext(), contacts.get(position).recipientID );
    }
  };

  public static ContactFragment getFragment() {
    if (contactFragment == null)
      contactFragment = new ContactFragment();
    return contactFragment;
  }

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      sinchBinder = (SinchLoginService.SinchBinder)service;
      Log.i(TAG, "onServiceConnected: ");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };

  @Override
  public void onDestroy() {
    getActivity().unbindService(connection);
    super.onDestroy();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    callerId = MainActivity.getDatabaseService().getUser().callerID;
    requestPermission();
    contacts = new ArrayList<>();
    getActivity().bindService(new Intent(getContext(), SinchLoginService.class),connection, Context.BIND_AUTO_CREATE);
  }

  @Nullable
  @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_contact, null);

    listView = view.findViewById(R.id.contact_list);

    addBtn = view.findViewById(R.id.addBtn);

    addBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText nameET;
        final EditText usernameET;

        View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_contact_add, null);
        builder.setView(dialog_layout);
        final AlertDialog dialog = builder.create();;

        nameET = dialog_layout.findViewById(R.id.nameET);
        usernameET = dialog_layout.findViewById(R.id.usernameET);
        Button addBtn = dialog_layout.findViewById(R.id.addBtn);
        Button cancelBtn = dialog_layout.findViewById(R.id.cancelBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String name = nameET.getText().toString();
            String recipientID = usernameET.getText().toString();
            RequestManager.registerContact(MainActivity.user,
                    new Contact(name, recipientID));
            dialog.dismiss();
          }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.cancel();
          }
        });
        dialog.show();
      }
    });



    ContactAdapter adapter = new ContactAdapter();
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(itemClickListener);
//    update_contact_list();

    RequestManager.getContact(MainActivity.user);


    return view;
  }

  public static void update_contact_list() {
    ArrayList<Contact> contacts = MainActivity.getDatabaseService().getContact();
    ContactFragment.contacts = contacts;
    listView.invalidateViews();
  }

  public static void callUser(Context mContext, String user) {
    Call call = sinchBinder.callUserVideo(user);
    Intent callingact = new Intent(mContext,CallingActivity.class);
    callingact.putExtra("recipientId",call.getCallId());
    mContext.startActivity(callingact);
  }

  public void requestPermission() {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
              getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUESTCALL);
      } else if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUESTCALL);
      } else if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUESTCALL);
      }
      if (getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUESTCALL);
      }
    }

  }


  @Override
  public void onClick(View v) {

//    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
//              getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Permission Denied");
//        builder.setMessage("Please allow the permissions");
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//          @Override
//          public void onClick(DialogInterface dialog, int which) {
//            Log.v("dialog", ""+which);
//            dialog.cancel();
//          }
//        });
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//          @Override
//          public void onClick(DialogInterface dialog, int which) {
//            requestPermission();
//          }
//        });
//
//        builder.create().show();
//        return;
//      }
//    }
//    Intent intent = new Intent(getContext(), CallingActivity.class);
//    intent.putExtra("callerId", callerId);
//    intent.putExtra("recipientId", recipientId);
//    startActivity(intent);
    }

    public class ContactAdapter extends BaseAdapter {

      public ContactAdapter() {
        super();
      }

      @Override
      public int getCount() {
        return contacts.size();
      }

      @Override
      public Contact getItem(int position) {
        return contacts.get(position);
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
          convertView = getLayoutInflater().inflate(R.layout.contact_list, parent, false);
        }

        TextView name = convertView.findViewById(R.id.contactTV);
        name.setText(contacts.get(position).name);

        Button deleteBtn = convertView.findViewById(R.id.deleteBtn);
        deleteBtn.setTag(contacts.get(position));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Contact contact = (Contact) v.getTag();
            RequestManager.deleteContact(MainActivity.user, contact);
          }
        });
        return convertView;
      }
    }
}
