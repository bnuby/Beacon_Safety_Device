package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AccountFragment extends Fragment {

  TextView nameTV;
  TextView callerIDTV;
  Button logoutBtn;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_account, container, false);

    nameTV = view.findViewById(R.id.nameTV);
    callerIDTV = view.findViewById(R.id.callerTV);
    logoutBtn = view.findViewById(R.id.logOutBtn);

    // Inflate the layout for this fragment
    return view;
  }
}
