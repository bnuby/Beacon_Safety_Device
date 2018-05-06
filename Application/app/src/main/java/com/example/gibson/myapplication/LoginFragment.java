package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

  EditText usernameET;
  EditText passwordET;
  Button loginBtn;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_login, container, false);

    usernameET = view.findViewById(R.id.usernameET);
    passwordET = view.findViewById(R.id.passwordET);
    loginBtn = view.findViewById(R.id.loginBtn);

    // Inflate the layout for this fragment
    return view;
  }
}
