package com.example.orgot.bluelight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends AppCompatActivity {

    private AudioManager audioManager;
    private int ringer;
    private Vibrator v;
    private Ringtone ringtone;
    private int origVol;
    private boolean vibrate;
    private boolean alarmSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("alarms", "1");

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm);
        Log.d("alarms", "2");

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        vibrate = getIntent().getBooleanExtra("vibration", pref.getBoolean("vibration", true));
//        alarmSound = getIntent().getBooleanExtra("alarmSound", pref.getBoolean("alarmSound", true));
//        Log.d("alarms", "3");
//
//        // set up alarm
//        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        ringer = audioManager.getRingerMode();
//        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Log.d("alarms", "4");
//
//        ringtone = RingtoneManager.getRingtone(this, alarmUri);
//        Log.d("alarms", "4.5");
////        origVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//        Log.d("alarms", "5");
//
//
//        // set up background
//        Intent intent = getIntent();
//        int green = pref.getInt("green", 100); // getting Integer
//        int color = Color.rgb(0, green, 255);
//        float brightness = pref.getFloat("brightness", 0.4f);
//        Log.d("alarms", "6");
//
//        LinearLayout layout = (LinearLayout) findViewById(R.id.BaseLayout);
//        layout.setBackgroundColor(color);
//        Log.d("alarms", "7");
//
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.screenBrightness = brightness;
//        getWindow().setAttributes(layoutParams);
//        Log.d("alarms", "8");
//
//
//
//
//        //set up buttons
//        Button stop = (Button) findViewById(R.id.wakeButton);
//        stop.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//
//                v.cancel();
//                ringtone.stop();
//                audioManager.setStreamVolume(AudioManager.STREAM_RING, origVol, 0);
//
//
//            }
//        });
//        Log.d("alarms", "9");
//
//        //set up buttons
//        Button close = (Button) findViewById(R.id.closeButton);
//        close.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                Log.d("alarms", "closing");
//
//                finish();
//
//            }
//        });
//        Log.d("alarms", "10");
//
//
//
////        //set up buttons
////        Button snooze = (Button) findViewById(R.id.snoozeButton);
////        stop.setOnClickListener(new View.OnClickListener() {
////
////            public void onClick(View view) {
////
////                v.cancel();
////                ringtone.stop();
////                audioManager.setStreamVolume(AudioManager.STREAM_RING, origVol, 0);
////
////
////            }
////        });
//
//        notify(this);
//        Log.d("alarms", "16");


    }

    public void notify(Context context) {
        Log.d("alarms", "11");

        if (ringer != AudioManager.RINGER_MODE_SILENT & vibrate) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

            // vibrate
            long[] pattern = {500, 500};
            v.vibrate(pattern, 0);
        }
        Log.d("alarms", "12");

        if (alarmSound) {
            ringtone.play();
        }
        Log.d("alarms", "13");

        Timer t =  new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.d("alarms", vibrate + " / " + alarmSound);

                if (vibrate) {
                    v.cancel();
                }
                if (alarmSound) {
                    ringtone.stop();
                }
                audioManager.setStreamVolume(AudioManager.STREAM_RING, origVol, 0);


                Log.d("alarms", "14");

                // REMOVE THIS
                finish();

            }
        }, 1000);//9000);
    }

    @Override
    public void onStop() {
        Log.d("alarms", "15");
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = -1F;
        getWindow().setAttributes(layout);

        super.onStop();

    }
}
