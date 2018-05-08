package com.example.gibson.myapplication.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gibson.myapplication.Model.Beacon;
import com.example.gibson.myapplication.Model.Contact;
import com.example.gibson.myapplication.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gibson on 22/03/2018.
 */

public class DatabaseService extends SQLiteOpenHelper {

  public final static String NAME = "Beacon";
  public final static int VERSION = 1;

  private static DatabaseService _instance;

  public static DatabaseService getDatabaseService() {
    if (_instance != null)
      return _instance;
    return null;
  }

  public DatabaseService(Context context) {
    super(context, NAME, null, VERSION);
    _instance = this;

  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String user = "Create Table User (username TEXT, password TEXT, name TEXT, email TEXT, callerID TEXT)";
    String beaconEntries = "Create Table Beacon_Receivers (mac TEXT PRIMARY KEY, name TEXT, alert_distance FLOAT)";
    String contact = "Create Table Contact (name TEXT, recipientID TEXT)";
    String mqtt = "Create Table Mqtt (host TEXT, topic TEXT, username TEXT, password TEXT)";

    sqLiteDatabase.execSQL(beaconEntries);
    sqLiteDatabase.execSQL(user);
    sqLiteDatabase.execSQL(mqtt);
    sqLiteDatabase.execSQL(contact);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
  }

  public User getUser() {
    SQLiteDatabase db = _instance.getReadableDatabase();
    ContentValues values = new ContentValues();
    Cursor cursor = db.query("User",
            null,
            null,
            null,
            null,
            null,
            null);
    JSONArray obj = null;
    String data = cursorToJson(cursor);
    try {
      obj = new JSONArray(data);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonToUser(obj);
  }

  public void insertUser(String username, String password, String name, String email, String callerID) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("username", username);
    values.put("password", password);
    values.put("name", name);
    values.put("email", email);
    values.put("callerID", callerID);

    db.insert("User", null, values);
  }

  public void logout() {
    SQLiteDatabase db = _instance.getWritableDatabase();

    db.delete("User", null, null);
  }

  public ArrayList<Contact> getContact() {
    SQLiteDatabase db = _instance.getReadableDatabase();
    ContentValues values = new ContentValues();
    Cursor cursor = db.query("Contact",
            null,
            null,
            null,
            null,
            null,
            null);
    JSONArray obj = null;
    String data = cursorToJson(cursor);
    try {
      obj = new JSONArray(data);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonToContact(obj);
  }

  public void insertContact(String name, String recipientID) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("name", name);
    values.put("recipientID", recipientID);

    db.insert("Contact", null, values);
  }

  public void deleteContact(String name, String recipientID) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    db.delete("Contact", "name = '" + name + "' AND recipientID = '" + recipientID + "'", null);
  }

  public void deleteContacts() {
    SQLiteDatabase db = _instance.getWritableDatabase();
    db.delete("Contact", null, null);
  }

  public long insertBeacon(String name, String mac, double alert_distance) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues values = new ContentValues(3);
    values.put("mac", mac);
    values.put("name", name);
    values.put("alert_distance", alert_distance);
    Log.v("values", values.toString());
    return db.insert("Beacon_Receivers", null, values);
  }

//  public JSONArray getBeacons() {
//    SQLiteDatabase db = _instance.getReadableDatabase();
//    Cursor cursor = db.query("Beacon_Receivers",
//            null,
//            null,
//            null,
//            null,
//            null,
//            null);
//    JSONArray object = null;
//    try {
//      String json = cursorToJson(cursor);
//      object = new JSONArray(json);
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }
//
//    Log.v("cursor", object.toString());
//    return object;
//  }

  public ArrayList getBeacons() {
    SQLiteDatabase db = _instance.getReadableDatabase();
    Cursor cursor = db.query("Beacon_Receivers",
            null,
            null,
            null,
            null,
            null,
            null);
    JSONArray object = null;
    ArrayList<Beacon> beacons = new ArrayList<>();
    try {
      String json = cursorToJson(cursor);
      object = new JSONArray(json);
      beacons = jsonToBeacons(object);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Log.v("cursor", object.toString());
    return beacons;
  }

  public int updateBeacon(String oldMac, Beacon beacon) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues data = new ContentValues();
    data.put("name", beacon.name);
    data.put("mac", beacon.MAC);
    data.put("alert_distance", beacon.alert_distance);
    Log.v("name", beacon.name);
    Log.v("mac", beacon.MAC);
    Log.v("distance", beacon.alert_distance + "");

    return db.update("Beacon_Receivers", data, "mac = '" + oldMac + "'", null);
  }

  public int deleteBeacon(String mac) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    return db.delete("Beacon_Receivers", "mac = '" + mac + "'", null);
  }

  public int deleteBeacons() {
    SQLiteDatabase db = _instance.getWritableDatabase();
    return db.delete("Beacon_Receivers", null, null);
  }

  public JSONArray getMqtt() {
    SQLiteDatabase db = _instance.getReadableDatabase();
    ContentValues values = new ContentValues();
    Cursor cursor = db.query("Mqtt",
            null,
            null,
            null,
            null,
            null,
            null);
    JSONArray obj = null;
    try {
      String data = cursorToJson(cursor);
      Log.v("MQTT", data);
      obj = new JSONArray(data);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return obj;
  }

  public void insertMqtt(String host, String topic, String username, String password) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("username", username);
    values.put("password", password);
    values.put("topic", topic);
    values.put("host", host);

    db.insert("Mqtt", null, values);
  }

  public void deleteMqtt() {
    SQLiteDatabase db = _instance.getWritableDatabase();

    db.delete("Mqtt", null, null);
  }

  public String cursorToJson(Cursor cursor) {
    StringBuffer array = new StringBuffer("[");
    while (cursor.moveToNext()) {
      array.append("{");
      for (int i = 0; i < cursor.getColumnCount(); i++) {
        switch (cursor.getType(i)) {
          case Cursor.FIELD_TYPE_STRING:
            array.append(String.format("'%s' : '%s'",
                    cursor.getColumnName(i),
                    cursor.getString(i)));
            break;

          case Cursor.FIELD_TYPE_INTEGER:
            array.append(String.format("'%s' : %d",
                    cursor.getColumnName(i),
                    cursor.getInt(i)));
            break;
          case Cursor.FIELD_TYPE_FLOAT:
            array.append(String.format("'%s' : %f",
                    cursor.getColumnName(i),
                    cursor.getFloat(i)));
            break;
        }
        if (i < cursor.getColumnCount() - 1) {
          array.append(",");
        }
      }
      array.append("}");
      if (!cursor.isLast()) {
        array.append(",");
      }
    }
    array.append("]");
    return array.toString();
  }

  ArrayList<Beacon> jsonToBeacons(JSONArray array) {
    ArrayList<Beacon> beaconArrayList = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      try {
        Beacon beacon = new Beacon(
                array.getJSONObject(i).getString("name"),
                array.getJSONObject(i).getString("mac"),
                array.getJSONObject(i).getDouble("alert_distance")
        );
        beaconArrayList.add(beacon);

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return beaconArrayList;
  }

  User jsonToUser(JSONArray array) {
    try {

      User user = new User(
              array.getJSONObject(0).getString("name"),
              array.getJSONObject(0).getString("username"),
              array.getJSONObject(0).getString("password"),
              array.getJSONObject(0).getString("email"),
              array.getJSONObject(0).getString("username")
      );
      return user;

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  ArrayList<Contact> jsonToContact(JSONArray array) {
    ArrayList<Contact> contacts = new ArrayList<>();
    for (int i = 0; i < array.length(); i++) {
      try {
        JSONObject object = array.getJSONObject(i);
        contacts.add(new Contact(
                object.getString("recipientID"),
                object.getString("name")
        ));

      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return contacts;
  }
}
