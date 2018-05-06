package com.example.gibson.myapplication.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by gibson on 22/03/2018.
 */

public class DatabaseService extends SQLiteOpenHelper {

  public final static String NAME = "Beacon";
  public final static int VERSION = 1;

  private DatabaseService _instance;

  public DatabaseService(Context context) {
    super(context, NAME, null, VERSION);
    _instance = this;

  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String beaconEntries = "Create Table Beacon_Receivers (mac TEXT PRIMARY KEY, name TEXT, alert_distance FLOAT)";
    String user = "Create Table User (username TEXT, password TEXT, name TEXT, email TEXT, callerID TEXT)";
    String contact = "Create Table Contact (name TEXT, id TEXT, phone_number INTEGER)";
    String mqtt = "Create Table Mqtt (host TEXT, topic TEXT, username TEXT, password TEXT)";

    sqLiteDatabase.execSQL(beaconEntries);
    sqLiteDatabase.execSQL(user);
    sqLiteDatabase.execSQL(mqtt);
    sqLiteDatabase.execSQL(contact);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
  }

  public JSONArray getUser() {
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
    return obj;
  }

  public void insertUser(String username, String password) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("username", username);
    values.put("password", password);

    db.insert("User", null, values);
  }

  public void deleteUser() {
    SQLiteDatabase db = _instance.getWritableDatabase();

    db.delete("User", null, null);
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

  public JSONArray getBeacons() {
    SQLiteDatabase db = _instance.getReadableDatabase();
    Cursor cursor = db.query("Beacon_Receivers",
            null,
            null,
            null,
            null,
            null,
            null);
    JSONArray object = null;
    try {
      String json = cursorToJson(cursor);
      object = new JSONArray(json);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Log.v("cursor", object.toString());
    return object;
  }

  public int updateBeacon(String oldMac, String mac, String name, double alert_distance) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    ContentValues data = new ContentValues();
    data.put("name", name);
    data.put("mac", mac);
    data.put("alert_distance", alert_distance);
    Log.v("name", name);
    Log.v("mac", mac);
    Log.v("distance", alert_distance + "");

    return db.update("Beacon_Receivers", data, "mac = '" + oldMac + "'",null );
  }

  public int deleteBeacon(String mac) {
    SQLiteDatabase db = _instance.getWritableDatabase();
    return db.delete("Beacon_Receivers", "mac = '" + mac + "'" , null);
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
    while(cursor.moveToNext()) {
      array.append("{");
      for(int i = 0 ; i < cursor.getColumnCount(); i++) {
        switch(cursor.getType(i)) {
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
        if(i < cursor.getColumnCount() -1){
          array.append(",");
        }
      }
      array.append("}");
      if(!cursor.isLast()) {
        array.append(",");
      }
    }
    array.append("]");
    return array.toString();
  }
}
