package com.example.orgot.bluelight;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message

        final MainActivity inst = MainActivity.instance();

        notify(context);

        // make blue
        inst.activate();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                inst.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        inst.deactivate();
                    }
                });
            }
        }, 10000);



    }

    public void notify(Context context) {
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        // maximise volume and vibrate
        final int ringer = audioManager.getRingerMode();
        final int origVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        final Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (ringer != AudioManager.RINGER_MODE_SILENT) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

            // vibrate
            long[] pattern = {500, 500};
            v.vibrate(pattern,0);
        }

        // sound the alarm
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();





        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ringtone.stop();
                if (ringer != AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, origVol, 0);
                    v.cancel();
                }
            }
        }, 10000);



    }

}
