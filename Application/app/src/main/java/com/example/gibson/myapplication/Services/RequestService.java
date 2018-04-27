package com.example.gibson.myapplication.Services;

import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.gibson.myapplication.MainViewPager;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestService {
  static String host = "http://192.168.0.103:5000/";

  public static boolean loginRequest(String username, String password) {

    String url = host + "login/123112";
    if (username.isEmpty() || password.isEmpty()) {
      return false;
    }
    Log.v("login","login");

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

                  MainViewPager.sendBroadcastMessage(intent);
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Log.v("User Login", "Request Fail\n" + error.getMessage());
                MainViewPager.sendToast("error");
                }
              });
      MainViewPager.requestQueue.add(request);

    } catch (JSONException e) {
      MainViewPager.sendToast("Fail to Send, Please Contact for assistance");
      e.printStackTrace();
    }

    MainViewPager.sendToast("Sending Login Request");

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

                  MainViewPager.sendBroadcastMessage(intent);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("Register", error.toString());
                MainViewPager.sendToast("Register Failure!\nPlease Try Again Later!");
              }
            });


    MainViewPager.requestQueue.add(request);
    MainViewPager.sendToast("Sending Register Request");


    return true;
  }
}
