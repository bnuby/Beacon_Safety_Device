package com.example.gibson.myapplication.Services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gibson.myapplication.Fragment.BeaconFragment;
import com.example.gibson.myapplication.Fragment.ContactFragment;
import com.example.gibson.myapplication.LoginActivity;
import com.example.gibson.myapplication.MainActivity;
import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.Model.Contact;
import com.example.gibson.myapplication.Model.User;
import com.example.gibson.myapplication.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {

//  static String host = "http://182.155.208.64:5000";
//  static String host = "http://10.21.25.216:5000";
//  static String host = "http://140.134.26.73:5000";
  static String host = "http://192.168.123.254:5000";

  public static boolean loginRequest(final String username, final String password) {
    String url = host + "/login";
    if (username.isEmpty() || password.isEmpty()) {
      LoginActivity.dismissLoading();
      return false;
    }
    Log.v("login", "login");
    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

                Log.v("login", response);
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  int status = jsonObject.getInt("status");
                  if (status == 200) {
                    Log.v("Login", "Success\n" + response);
                    LoginActivity.sendToast("Success Login");
                    JSONObject userObject = jsonObject.getJSONObject("data");

                    String username = userObject.getString("username");
                    String password = userObject.getString("password");
                    String name = userObject.getString("nickname");
                    String email = userObject.getString("email");
                    String callerID = userObject.getString("username");

                    DatabaseService.getDatabaseService().insertUser(
                            username,
                            password,
                            name,
                            email,
                            callerID
                    );
                    LoginActivity.startSinch(username);

                    LoginActivity.successLogin();
                  } else if (status == 404) {
                    LoginActivity.sendToast("Invalid Username Or Password.");
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                LoginActivity.dismissLoading();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("login error", error.toString());

                Log.v("User Login", "Request Fail\n" + error.getMessage());
                LoginActivity.sendToast("Login Failed");
                LoginActivity.dismissLoading();
              }
            }) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", username);
        post.put("password", password);
        return post;
      }
    };
    LoginActivity.getQueue().add(request);

//    LoginActivity.sendToast("Sending Login Request");

    return true;
  }

  public static void logoutRequest() {
    JSONObject object = new JSONObject();
    try {
      object.put("username", MainActivity.user.username);
      StringRequest request = new StringRequest(
              Request.Method.POST,
              host + "/logout",
              new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  try {
                    int status = new JSONObject(response).getInt("status");
                    if (status == 200)
                      Log.v("Logout", "Success");
                    MainActivity.getDatabaseService().logout();
                    MainActivity.dismissLoading();
                  } catch (JSONException e) {
                    MainActivity.sendToast("Logout Fail");
                    MainActivity.dismissLoading();
                    e.printStackTrace();
                  }
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
              }
      ) {

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
          return super.getParams();
        }
      };
      MainActivity.getQueue().add(request);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  public static boolean registerRequest(final JSONObject jsonObject) {
    RegisterActivity.showLoading("Registering..");
    Log.v("register", "pp");
    if (!jsonObject.has("username") && !jsonObject.has("password")
            && !jsonObject.has("email") && !jsonObject.has("nickname")) {
      RegisterActivity.dissmissLoading(400);
      return false;
    }
    Log.v("register", jsonObject.toString());
    String url = host + "/signup";
    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  int registerStatus = new JSONObject(response).getInt("status");
                  RegisterActivity.dissmissLoading(registerStatus);
                } catch (JSONException e) {
                  RegisterActivity.dissmissLoading(400);
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("register", error.toString());
                RegisterActivity.sendToast("Register Failure!\nPlease Try Again Later!");
                RegisterActivity.dissmissLoading(400);
              }
            }) {

      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        try {
          post.put("username", jsonObject.getString("username"));
          post.put("password", jsonObject.getString("password"));
          post.put("email", jsonObject.getString("email"));
          post.put("nickname", jsonObject.getString("nickname"));
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return post;
      }
    };


    RegisterActivity.queue.add(request);
//    RegisterActivity.sendToast("Sending Register Request");

    return true;
  }

  public static boolean getBeaconData(User user) {
    String url = host + "/beacon/" + user.username;

    StringRequest request = new StringRequest(
            Request.Method.GET,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject jsonObject = new JSONObject(response);
                  if(jsonObject.getInt("status") == 400 || jsonObject.getString("msg").equals("Not ")) {
                    return;
                  }

                  ArrayList<Beacon> beacons = new ArrayList();

                  JSONArray beaconArray = jsonObject.getJSONArray("data");
                  MainActivity.getDatabaseService().deleteBeacons();

                  for(int i = 0; i < beaconArray.length(); i++) {
                    JSONObject curr = beaconArray.getJSONObject(i);
                    String name = curr.getString("name");
                    String mac = curr.getString("mac");
                    double distance = curr.getDouble("distance");
                    beacons.add(new Beacon(
                            name,
                            mac,
                            distance
                    ));
                    MainActivity.getDatabaseService().insertBeacon(
                            name,
                            mac,
                            distance
                    );
                  }
                  BeaconFragment.updateBeaconArray(beacons);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

              }
            }
    );

    MainActivity.getQueue().add(request);


    return true;

  }

  public static boolean insertBeaconData(final User user, final Beacon beacon) {
    String url = host +"/beacon";
    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if(object.getInt("status") == 400) {
                    MainActivity.sendToast("Insert Error");
                    return;
                  }
                  MainActivity.sendToast("Insert Success");
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("error", error.toString());
              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", user.username);
        post.put("name", beacon.name);
        post.put("mac", beacon.MAC);
        post.put("distance", String.valueOf(beacon.alert_distance));
        return post;
      }
    };

    MainActivity.getQueue().add(request);
    return true;
  }


  public static boolean updateBeaconData(final User user, final String old_mac, final Beacon beacon) {
    String url = host +"/beacon";
    StringRequest request = new StringRequest(
            Request.Method.PUT,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if(object.getInt("status") == 400) {
                    MainActivity.sendToast("Update Error");
                    return;
                  }
                  MainActivity.sendToast("Update Success");
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.v("error", error.toString());
              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", user.username);
        post.put("old_mac", old_mac);
        post.put("name", beacon.name);
        post.put("mac", beacon.MAC);
        post.put("distance", String.valueOf(beacon.alert_distance));
        return post;
      }
    };

    MainActivity.getQueue().add(request);
    return true;
  }

  public static boolean deleteBeaconData(final User user, final Beacon beacon) {
    String url = host + "/beacon/delete";
    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if (object.getInt("status") == 400)
                    MainActivity.sendToast("Delete Failed" + object.getString("msg"));

                  MainActivity.sendToast("Delete Sucess");
                } catch (JSONException e) {
                  MainActivity.sendToast("Delete Failed, Please Contact Developer");
                  e.printStackTrace();
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MainActivity.sendToast("Delete Failed");
              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", user.username);
        post.put("mac", beacon.MAC);
        return post;
      }

    };
    MainActivity.getQueue().add(request);
    return true;
  }


  public static boolean registerContact(final User user, final Contact contact) {
    String url = host + "/contact";
    MainActivity.showLoading("Adding Contact");
    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if(object.getInt("status") == 400) {
                    MainActivity.dismissLoading();
                    MainActivity.sendToast(object.getString("msg"));
                    return;
                  }
                  MainActivity.getDatabaseService().insertContact(contact.name, contact.recipientID);
                  ContactFragment.update_contact_list();
                  MainActivity.sendToast("Add Sucessful");
                } catch (JSONException e) {
                  e.printStackTrace();
                  MainActivity.sendToast("Add Fail");

                }
                MainActivity.dismissLoading();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MainActivity.sendToast("Connection Fail. \nPlease Check Your network");
                MainActivity.dismissLoading();
              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", user.username);
        post.put("name", contact.name);
        post.put("recipientID", contact.recipientID);
        return post;
      }
    };

    MainActivity.getQueue().add(request);
    return true;
  }

  public static boolean getContact(User user) {
    String url = host + "/contact/" + user.username;
    StringRequest request = new StringRequest(
            Request.Method.GET,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if(object.getInt("status") == 400) {
                    MainActivity.sendToast("Get Fail");
                    return;
                  }
                  MainActivity.getDatabaseService().deleteContacts();
                  JSONArray contactArray = object.getJSONArray("data");
                  for(int i = 0; i < contactArray.length(); i++) {
                    String name = contactArray.getJSONObject(i).getString("name");
                    String recipientID = contactArray.getJSONObject(i).getString("recipientID");
                    MainActivity.getDatabaseService().insertContact(
                            name,
                            recipientID
                    );
                  }
                  ContactFragment.update_contact_list();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                MainActivity.dismissLoading();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MainActivity.dismissLoading();
              }
            }
    );

    MainActivity.getQueue().add(request);
    return true;
  }

  public static boolean deleteContact(final User user, final Contact contact) {
    String url = host + "/contact";
    MainActivity.showLoading("Deleting Contact..");
    StringRequest request = new StringRequest(
            Request.Method.PUT,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                try {
                  JSONObject object = new JSONObject(response);
                  if(object.getInt("status") == 400) {
                    MainActivity.sendToast("Delete Fail");
                    return;
                  }
                  MainActivity.getDatabaseService().deleteContact(contact.name, contact.recipientID);
                  ContactFragment.update_contact_list();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                MainActivity.dismissLoading();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                MainActivity.dismissLoading();
              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("username", user.username);
        post.put("recipientID", contact.recipientID);
        return post;
      }
    };

    MainActivity.getQueue().add(request);
//    MainActivity.sendToast("Pending Delete Request");
    return true;
  }


  public static boolean armAlarm(User user, final String status) {
    String url = host + "/arduino";
    StringRequest request = new StringRequest(
            Request.Method.PUT,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();
        post.put("status", status);
        return post;
      }
    };
    BeaconDetectService.queue.add(request);
    return true;
  }

  public static boolean sendUsername(final User user) {
    String url = host + "/arduino";

    StringRequest request = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

              }
            }
    ) {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> post = new HashMap<>();

        post.put("username", user.username);

        return post;
      }
    };

    BeaconDetectService.queue.add(request);

    return true;
  }

  public static boolean getArduinoStatus(User user, final String status) {
    String url = host + "/arduino/" + user.username;
    StringRequest request = new StringRequest(
            Request.Method.PUT,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {

              }
            }
    );

    MainActivity.getQueue().add(request);
    return true;
  }
}
