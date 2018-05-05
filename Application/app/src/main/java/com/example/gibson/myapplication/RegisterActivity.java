package com.example.gibson.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gibson.myapplication.Services.RequestService;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

  EditText nameET;
  EditText usernameET;
  EditText passwordET;
  EditText emailET;
  Button submitBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    init();
  }

  public void init() {
    nameET = findViewById(R.id.nameET);
    usernameET = findViewById(R.id.usernameET);
    passwordET = findViewById(R.id.passwordET);
    emailET = findViewById(R.id.emailET);
    submitBtn = findViewById(R.id.submitBtn);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        JSONObject object = new JSONObject();
        try {
          object.put("name", nameET.getText().toString());
          object.put("username", usernameET.getText().toString());
          object.put("password", passwordET.getText().toString());
          object.put("email", emailET.getText().toString());
          RequestService.registerRequest(object);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        finish();
      }
    });


  }
}
