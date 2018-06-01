package com.example.gibson.myapplication.AbstractClass;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.R;

public class BeaconBaseActivity extends AppCompatActivity{

  protected static AlertDialog dialog;
  protected static Context mContext;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public static void sendToast(String message) {
    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mContext = this;
  }

  public static void showLoading(String message) {
    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
    View view = View.inflate(mContext, R.layout.loading_view, null);
    TextView textView = view.findViewById(R.id.message);
    textView.setText(message );
    builder.setCancelable(false);
    builder.setView(view);
    dialog = builder.create();
    dialog.show();
  }

  public static void dismissLoading() {
    dialog.dismiss();
  }



}
