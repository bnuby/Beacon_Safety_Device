package com.example.gibson.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.gibson.myapplication.Fragment.DogFragment;
import com.example.gibson.myapplication.Services.SinchLoginService;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallDirection;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

public class CallingActivity extends AppCompatActivity {

    private static final String TAG = "CallingActivity";
    private Call call;
    private String recipientId;
    private Button button;
    private SinchLoginService.SinchBinder sinchBinder;
    private RelativeLayout localView;
    private LinearLayout view;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private Ringtone ringtone;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (SinchLoginService.class.getName().equals(name.getClassName())) {
                sinchBinder = (SinchLoginService.SinchBinder) service;
                Log.i(TAG, "onServiceConnected: ");
                call = sinchBinder.getCall(recipientId);
                if (call != null) {
                    Log.i(TAG, "onServiceConnected: addlistener");
                    call.addCallListener(new SinchCallListener());
                }
                else {
                    Log.i(TAG, "onServiceConnected: call isnull");
                }
            } else {
                Log.i("Wrong name", "Wrong");
            }
            updateUI();
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: ");

        }
    };
    private void updateUI() {
        if (sinchBinder == null) {
            Log.i(TAG, "updateUI: sinchbinderisnull");
            return; // early
        }
        if (call != null) {
            if(call.getState().toString().equals("INITIATING")){
                Log.i(TAG, "updateUI: getcall");
                if(MainActivity.receiveMode){
                    call.answer();
                    removeAllView();
                }

            }
            if (call.getState() == CallState.ESTABLISHED) {
                Log.i(TAG, "updateUI: add");
                removeAllView();
                //when the call is established, addVideoViews configures the video to  be shown
                addVideoViews();
            }
        }
    }

    public void removeAllView() {
        if(localView!=null)
            localView.removeAllViews();
        if(view!=null)
            view.removeAllViews();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        recipientId = getIntent().getStringExtra("recipientId");
        Log.i(TAG, "onCreate: " + recipientId);
        button = (Button) findViewById(R.id.hangup);

        bindService(new Intent(this, SinchLoginService.class), connection, BIND_AUTO_CREATE);
        audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
//        RingtoneManager ringtoneManager = new RingtoneManager(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(getIntent().getBooleanExtra("fromImcomming",false)){
//            audioManager.setSpeakerphoneOn(true);
            vibrator.vibrate(new long[]{500, 1000, 500, 1000, 500, 1000}, 0);

            setVolumeControlStream(AudioManager.MODE_RINGTONE);

            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING,maxVolume,
                    AudioManager.FLAG_ALLOW_RINGER_MODES);
//            ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);

            ringtone = RingtoneManager.getRingtone(getBaseContext(), notification);

            ringtone.play();

            Log.i(TAG, "onCreate: Income");
            button.setText("hang on");
            button.setBackgroundColor(Color.rgb(0, 255, 0));
        }




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call =sinchBinder.getCall(recipientId);
                if(call!=null) {
                    if (call.getState() == CallState.ESTABLISHED) {
                        call.hangup();
                        finish();
                    }else if (call.getState() == CallState.INITIATING) {
                        if(call.getDirection() == CallDirection.INCOMING)
                            call.answer();
                        else
                            call.hangup();
                    } else if (call.getState() == CallState.PROGRESSING) {
                      if(call.getDirection() == CallDirection.OUTGOING)
                        call.hangup();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        call.hangup();
        unbindService(connection);
        super.onDestroy();
    }

    private class SinchCallListener implements VideoCallListener {
        @Override
        public void onCallEnded(Call endedCall) {
//            call = null;
//            SinchError a = endedCall.getDetails().getError();
//            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            if(MainActivity.receiveMode){
                DogFragment.startBeaconService();
            }

            vibrator.cancel();
//
//            if(MainActivity.receiveMode) {
//                ReceiveBeaconActivity.startBeaconService();
//            }

            if(ringtone != null && ringtone.isPlaying())
                ringtone.stop();
            if (call != null) {
                if(localView!=null){
                    localView.removeAllViews();
                }
                if(view!=null){
                    view.removeAllViews();
                }
                call.hangup();
                Log.i(TAG, "onCallEnded: hangup");
            }
            finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            try{
                vibrator.cancel();
                ringtone.stop();
            }catch (Exception e){
                Log.i(TAG, "onCallEstablished: cannot cancel vibrate");
            }
            Log.i(TAG, "onCallEstablished");
            button.setText("hangup");
            button.setBackgroundColor(Color.rgb(255, 0, 0));
            audioManager.setMicrophoneMute(false);
            audioManager.setSpeakerphoneOn(true);
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        }

        @Override
        public void onCallProgressing(Call progressingCall) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.i(TAG, "onVideoTrackAdded: added");
            addVideoViews();
        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }

    private void addVideoViews() {
        final VideoController vc = sinchBinder.getVideoController();
        if (vc != null) {
            localView = (RelativeLayout) findViewById(R.id.localVideo);
            view = (LinearLayout) findViewById(R.id.remoteVideo);
            removeAllView();
            localView.addView(vc.getLocalView());
            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            // this toggles the front camera to rear camera and vice versa
            vc.toggleCaptureDevicePosition();
                }
            });
            view.addView(vc.getRemoteView());
        }
    }
}