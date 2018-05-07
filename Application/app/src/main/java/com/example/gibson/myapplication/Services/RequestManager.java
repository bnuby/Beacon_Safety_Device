package com.example.gibson.myapplication.Services;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gibson.myapplication.BeaconFragment;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.Model.Contact;
import com.example.gibson.myapplication.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestManager {
  static String host = "http://10.21.22.168:5000/";

  public static boolean loginRequest(String username, String password) {

    String url = host + "login/123112";
    if (username.isEmpty() || password.isEmpty()) {
      return false;
    }
    Log.v("login", "login");

    JSONObject object = new JSONObject();
    try {
      object.put("username", username);
      object.put("password", password);

      JsonObjectRequest request = new JsonObjectRequest(
              Request.Method.GET,
              url,
              object,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  Log.v("User Login", "Request Success\n" + response);


                  Intent intent = new Intent("Login");
                  intent.putExtra("status", true);
                  MainActivity.isLogin = true;
                  MainActivity.sendBroadcastMessage(intent);
                  MainActivity.sendToast("Success Login");
                  try {
                    JSONObject jsonObject = response.getJSONObject("data");

                    String username = jsonObject.getString("username");
                    String password = jsonObject.getString("password");
                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String callerID = jsonObject.getString("callerID");
                    MainActivity.getDatabaseService().insertUser(
                            username,
                            password,
                            name,
                            email,
                            callerID
                    );
                    MainActivity.user = new User(
                            username,
                            password,
                            name,
                            email,
                            callerID
                    );
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  MainActivity.dissmissLoading();
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Log.v("User Login", "Request Fail\n" + error.getMessage());
                  MainActivity.sendToast("Login Failed");
                  MainActivity.dissmissLoading();
                }
              });
      MainActivity.requestQueue.add(request);

    } catch (JSONException e) {
      MainActivity.sendToast("Fail to Send, Please Contact for assistance");
      e.printStackTrace();
    }

    MainActivity.sendToast("Sending Login Request");

    return true;
  }

  public static boolean registerRequest(JSONObject jsonObject) {

    if (!jsonObject.has("username") && !jsonObject.has("password")
            && !jsonObject.has("email") && !jsonObject.has("name")) {
      return false;
    }

    String url = host + "login";
    JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.PUT,
            url,
            jsonObject,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                try {
                  Intent intent = new Intent("Register");
                  intent.putExtra("status", response.getBoolean("status"));

                  MainActivity.sendBroadcastMessage(intent);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("Register", error.toString());
                MainActivity.sendToast("Register Failure!\nPlease Try Again Later!");
              }
            });


    MainActivity.requestQueue.add(request);
    MainActivity.sendToast("Sending Register Request");

    return true;
  }

  public static boolean getBeaconData(User user) {
    if (MainActivity.isLogin) {
      ArrayList<Beacon> beacons = new ArrayList();

      BeaconFragment.updateBeaconArray(beacons);

      return true;
    }
    return false;
  }

  public static boolean insertBeaconData(User user, Beacon contact) {
    if(MainActivity.isLogin) {

      return true;
    }
    return false;
  }

  public static boolean deleteBeaconData(User user, Beacon contact) {
    if(MainActivity.isLogin) {

      return true;
    }
    return false;
  }


  public static boolean registerContact(User user, Contact contact) {
    if(MainActivity.isLogin) {

      return true;
    }
    return false;
  }

  public static boolean getContact(User user, Contact contact) {
    if(MainActivity.isLogin) {

      return true;
    }
    return false;
  }

  public static boolean deleteContact(User user, Contact contact) {
    if(MainActivity.isLogin) {

      return true;
    }
    return false;
  }


  public static boolean armAlarm(User user) {

    return true;
  }
}
