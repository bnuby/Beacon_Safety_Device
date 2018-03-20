package com.example.gibson.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MQTT_SERVICE implements MqttCallback {

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
          setTextViewStatus(true);
          Toast.makeText(mainContext, "连接成功", Toast.LENGTH_SHORT).show();
          try {
            client.subscribe(myTopic, 1);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else if(msg.what == 3) {
          setTextViewStatus(false);
          Toast.makeText(mainContext, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
        }
      }
    };

    try {
      client = new MqttClient(host, "", new MemoryPersistence());
      client.setCallback(this);

      options = new MqttConnectOptions();
      options.setCleanSession(true);
      options.setUserName(username);
      options.setPassword(password.toCharArray());
      options.setConnectionTimeout(10);
      options.setKeepAliveInterval(20);
      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          setTextViewStatus(false);
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

  private void setTextViewStatus(boolean bool) {
    if(statusTV != null) {
      if(!bool) {
        statusTV.setText("Lost");
        statusTV.setTextColor(mainContext.getResources().getColor(android.R.color.holo_red_dark, null));
      } else {
        statusTV.setText("Connected");
        statusTV.setTextColor(mainContext.getResources().getColor(android.R.color.holo_green_dark, null));
      }
    }
  }

  public void startConnect() {
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
    }, 0 * 1000, 2 * 1000, TimeUnit.MILLISECONDS);
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

  @Override
  public void connectionLost(Throwable cause) {
    Log.v("connectionLost", cause.getMessage());

  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    Log.v("messageArrived", new String(message.getPayload()));
    feedback.add(new String(message.getPayload()));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    try {
      Log.v("deliveryCompleted", token.getMessage().toString());
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}
