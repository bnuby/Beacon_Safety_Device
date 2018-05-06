package com.example.gibson.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by gibson on 20/03/2018.
 */

public class ContactFragment extends Fragment implements View.OnClickListener {

  private static final String TAG = "LoginActivity";
  final int REQUESTCALL = 2;
  private Context mContext;
  private Button mLoginButton;
  private String callerId = "callerId";
  private String recipientId = "recipientId";

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestPermission();


  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_contact, null);
    mLoginButton = (Button) view.findViewById(R.id.loginButton);
    mLoginButton.setOnClickListener(this);
    return view;
  }

  public void requestPermission() {
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


  @Override
  public void onClick(View v) {

    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
            getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {

      AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
      builder.setTitle("Permission Denied");
      builder.setMessage("Please allow the permissions");
      builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Log.v("dialog", ""+which);
          dialog.cancel();
        }
      });

      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          requestPermission();
        }
      });

      builder.create().show();
      return;
    }
    Intent intent = new Intent(getContext(), CallingActivity.class);
    intent.putExtra("callerId", callerId);
    intent.putExtra("recipientId", recipientId);
    startActivity(intent);
  }
}
