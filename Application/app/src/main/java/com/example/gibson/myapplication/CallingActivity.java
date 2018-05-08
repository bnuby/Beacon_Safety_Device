package com.example.gibson.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gibson.myapplication.Services.SinchLoginService;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class CallingActivity extends AppCompatActivity {

    private static final String TAG = "CallingActivity";
    private Call call;
    private String recipientId;
    private Button button;
    private SinchLoginService.SinchBinder sinchBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (SinchLoginService.class.getName().equals(name.getClassName())) {

                sinchBinder = (SinchLoginService.SinchBinder) service;
                Log.i(TAG, "onServiceConnected: ");
                call = sinchBinder.getCall(recipientId);
                if (call != null) {
                    call.addCallListener(new SinchCallListener());
                }
            } else {
                Log.i("Wrong name", "Wrong");
//                unbindService(connection);
                finish();
            }
            updateUI();

//            if (call != null) {
//                mCallerName.setText(call.getRemoteUserId());
//                mCallState.setText(call.getState().toString());
//                if (call.getState() == CallState.ESTABLISHED) {
//                    //when the call is established, addVideoViews configures the video to  be shown
//                    addVideoViews();
//                }
//            }

        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: ");

        }
    };
    private void updateUI() {
        if (sinchBinder == null) {
            return; // early
        }
        if (call != null) {
            if (call.getState() == CallState.ESTABLISHED) {
                //when the call is established, addVideoViews configures the video to  be shown
//                addVideoViews();
            }
            if(call.getState().toString().equals("INITIATING")){
                call.answer();
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        recipientId = getIntent().getStringExtra("recipientId");
        Log.i(TAG, "onCreate: " + recipientId);
        bindService(new Intent(this, SinchLoginService.class), connection, BIND_AUTO_CREATE);


        button = (Button) findViewById(R.id.hangup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.hangup();
                finish();

            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    private class SinchCallListener implements VideoCallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            finish();

        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            Log.i(TAG, "onCallEstablished");
            button.setText("hangup");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        }

        @Override
        public void onCallProgressing(Call progressingCall) {
//            button.setText("Calling~");
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
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());

            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this toggles the front camera to rear camera and vice versa
                    vc.toggleCaptureDevicePosition();
                }
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
        }
    }
}
