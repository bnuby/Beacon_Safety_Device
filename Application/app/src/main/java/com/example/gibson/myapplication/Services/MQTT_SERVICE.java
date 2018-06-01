package com.example.gibson.myapplication.Services;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gibson on 20/03/2018.
 */

public class MQTT_SERVICE {

  // MQTT SERVER DEFAULT PARAMETER
  private MqttClient client;
  private MqttConnectOptions options;
  private String myTopic;
  private String username;
  private String password;
  private Handler handler;
  private ScheduledExecutorService scheduler;
  private Context mainContext;
  private TextView statusTV;
  private List<String> feedback;
  private MqttStatus status;

  public MQTT_SERVICE(final Context context, String host, String topic, String username, String password) {
    mainContext = context;
    myTopic = topic;
    this.username = username;
    this.password = password;

    feedback = new ArrayList<>();

    handler = new Handler(){
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(msg.what == 1) {
          Toast.makeText(mainContext, (String) msg.obj,
                  Toast.LENGTH_SHORT).show();
          System.out.println("-----------------------------");
        } else if(msg.what == 2) {
          status = MqttStatus.Connected;
          setTextViewStatus(MqttStatus.Connected);
          Toast.makeText(mainContext, "连接成功", Toast.LENGTH_SHORT).show();
          try {
            client.subscribe(myTopic, 1);
            client.subscribe("before", 1);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if(msg.what == 3) {
          status = MqttStatus.Connecting;
          setTextViewStatus(MqttStatus.Failed);
          Toast.makeText(mainContext, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
        }
      }
    };

    try {
      if(options != null) {
        cancelConnect();
      }
      client = new MqttClient(host, "", new MemoryPersistence());

      options = new MqttConnectOptions();
      options.setCleanSession(true);

      if(!username.equals("") && !password.equals("")) {
        options.setUserName(username);
        options.setPassword(password.toCharArray());
      }

      options.setConnectionTimeout(10);
      options.setKeepAliveInterval(20);


      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          setTextViewStatus(MqttStatus.Failed);
          System.out.println("connectionLost----------");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          System.out.println("Message Arrived----------");
          Message msg = new Message();
          msg.what = 1;
          msg.obj= topic + "---" + message.toString();
          handler.sendMessage(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

          System.out.println("Message Sent----------");
          Log.v("Message", "sent");

        }
      });
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
//
//  public void mqttUpdateUser(String username, String password) {
//    options = new MqttConnectOptions();
//    options.setCleanSession(true);
//    options.setUserName(username);
//    options.setPassword(password.toCharArray());
//    options.setConnectionTimeout(10);
//    options.setKeepAliveInterval(20);
//  }

  private void setTextViewStatus(MqttStatus bool) {
    if(statusTV != null) {
      switch(bool) {
        case Failed:
          statusTV.setText(mainContext.getResources().getString(R.string.mqtt_failed));
          statusTV.setTextColor(mainContext.getResources().getColor(android.R.color.holo_red_dark, null));
          break;
        case Connecting:
          statusTV.setText(mainContext.getResources().getString(R.string.mqtt_connecting));
          statusTV.setTextColor(mainContext.getResources().getColor(android.R.color.holo_blue_dark, null));
          break;
        case Not_Connected:
          statusTV.setText(mainContext.getResources().getString(R.string.mqtt_not_connected));
          statusTV.setTextColor(mainContext.getResources().getColor(R.color.gray, null));
          break;
        case Connected:
          statusTV.setText(mainContext.getResources().getString(R.string.mqtt_connected));
          statusTV.setTextColor(mainContext.getResources().getColor(android.R.color.holo_green_dark, null));
          break;
      }
    }
  }

  public void startConnect() {
    setTextViewStatus(MqttStatus.Connecting);
    scheduler = Executors.newSingleThreadScheduledExecutor();
    Log.v("Scheduler", "Before");
    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        Log.v("Scheduler", "RUNNING");
        if(!client.isConnected()) {
          Log.v("Scheduler", "Connect");

          connect();
        }
      }
    }, 0 * 1000, 3 * 1000, TimeUnit.MILLISECONDS);
  }

  public void cancelConnect() {
    setTextViewStatus(MqttStatus.Not_Connected);
    scheduler.shutdown();
    scheduler = null;
  }

  private void connect() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          client.connect(options);
          Message msg = new Message();
          msg.what = 2;
          handler.sendMessage(msg);
        } catch (Exception e) {
          e.printStackTrace();
          Message msg = new Message();
          msg.what = 3;
          handler.sendMessage(msg);
        }
      }
    }).start();
  }

  public void setStatusTextView(TextView statusTV) {

    this.statusTV = statusTV;
    if(status != null) {
      setTextViewStatus(status);
    }
  }

  public void publishMessage(String topic, String message) {
    byte[] encodeByte = new byte[0];
    try {
      encodeByte = message.getBytes("UTF-8");
      client.publish(topic, new MqttMessage(encodeByte));
    } catch (MqttException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void subscribeMessage(String topic) {
    final int QOS = 1;
    try {
      Log.v("message", "subscribe");
      client.subscribe(topic, QOS);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

  public List<String> getMessage() {
    return feedback;
  }
}
enum MqttStatus {
  Failed, Connecting, Connected, Not_Connected
}