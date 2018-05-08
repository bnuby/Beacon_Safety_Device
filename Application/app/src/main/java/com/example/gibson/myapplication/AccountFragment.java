package com.example.gibson.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.Services.RequestManager;

public class AccountFragment extends Fragment {

  // Account View
  TextView nameTV;
  TextView callerIDTV;
  Button logoutBtn;
  boolean bool = true;
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

    logoutBtn = accountView.findViewById(R.id.logOutBtn);

    logoutBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        RequestManager.logoutRequest();
        MainActivity.getDatabaseService().logout();
        ((Activity) getContext()).finish();
        Toast.makeText(getContext(), "Logout!", Toast.LENGTH_SHORT).show();
      }
    });
    // Inflate the layout for this fragment
      return accountView;
  }


}
