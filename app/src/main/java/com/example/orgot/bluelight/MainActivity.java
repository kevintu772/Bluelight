package com.example.orgot.bluelight;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    protected Button light;
    protected ConstraintLayout constraintLayout;
    protected int activatedColor = Color.rgb(0,183,241);
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        // assign the light button
        light = (Button) findViewById(R.id.light);
        constraintLayout = (ConstraintLayout) findViewById(R.id.BaseLayout);

        // set up our overlay
        constraintLayout.setBackgroundColor(Color.WHITE);


        // set up the button
        light.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (light.getText().equals("Activate")) {


                    activate();


                } else {

                    deactivate();

                }


            }
        });
    }


    public void deactivate() {
        constraintLayout.setBackgroundColor(Color.WHITE);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = -1F;
        getWindow().setAttributes(layout);

        light.setText("Activate");
    }

    public void activate() {
        constraintLayout.setBackgroundColor(activatedColor);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);

        light.setText("Deactivate");
    }

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d("MyActivity", "Alarm Off");
        }
    }


}

