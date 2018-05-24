package com.example.gibson.myapplication.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.R;
import com.example.gibson.myapplication.ReceiveBeaconActivity;
import com.example.gibson.myapplication.Services.RequestManager;

public class AccountFragment extends Fragment {

  // Account View
  TextView nameTV;
  TextView callerIDTV;
  Button receiveBtn;
  Button logoutBtn;
  static View accountView;

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
    receiveBtn = accountView.findViewById(R.id.receiveBtn);
    logoutBtn = accountView.findViewById(R.id.logOutBtn);

    logoutBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MainActivity.getDatabaseService().logout();
        ((Activity) getContext()).finish();
//        RequestManager.logoutRequest();
//        Toast.makeText(getContext(), "Logout!", Toast.LENGTH_SHORT).show();
      }
    });

    receiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), ReceiveBeaconActivity.class);
        MainActivity.receivedMode = true;
        startActivity(intent);
      }
    });
    // Inflate the layout for this fragment
      return accountView;
  }

}
