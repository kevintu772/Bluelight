package com.example.orgot.bluelight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.sql.Time;
import java.util.Arrays;

public class AlarmEditActivity extends AppCompatActivity {

    //Day buttons
    private ToggleButton[] days;
    private boolean[] daysState;
    private TimePicker timePicker;
    private int[] time; // hours, mins
    private String name;
    private Context context;
    private boolean sound;
    private boolean vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        timePicker = (TimePicker) findViewById(R.id.alarmTimePicker);

        days = new ToggleButton[7];
        daysState = new boolean[7];
        time = new int[2];
        name = "New Alarm";
        context = this;

        for (int i = 0; i < daysState.length; i++) {
            daysState[i] = false;
        }

        days[0] = (ToggleButton) findViewById(R.id.tS);
        days[1] = (ToggleButton) findViewById(R.id.tM);
        days[2] = (ToggleButton) findViewById(R.id.tT);
        days[3] = (ToggleButton) findViewById(R.id.tW);
        days[4] = (ToggleButton) findViewById(R.id.tTh);
        days[5] = (ToggleButton) findViewById(R.id.tF);
        days[6] = (ToggleButton) findViewById(R.id.tSa);


        setUpButtons();

        setUpNameField();

    }

    private void setUpButtons() {
        //set up buttons
        final Switch soundButton = (Switch) findViewById(R.id.alarmSoundSwitch);
        soundButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                sound = (soundButton.isChecked()) ? true : false;
            }
        });

        //set up buttons
        final Switch vibeButton = (Switch) findViewById(R.id.alarmVibSwitch);
        vibeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                vibration = (vibeButton.isChecked()) ? true : false;
            }
        });

        //set up buttons
        Button close = (Button) findViewById(R.id.cancelCreate);
        close.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

        //set up buttons
        Button finish = (Button) findViewById(R.id.doneCreate);
        finish.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                updateDays();
                updateTime();
                Log.d("test", "activate");
                returnActivity();
            }
        });
    }

    private void setUpNameField(){
        final LinearLayout nameField = (LinearLayout) findViewById(R.id.nameField);
        nameField.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Title");

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

        });
    }

    private void updateDays(){
        for (int i = 0 ; i < days.length; i++) {
            if (days[i].isChecked()) {
                daysState[i] = true;
            }
        }
    }

    private void updateTime(){
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        time[0] = hour;
        time[1] = minute;
    }



    private void returnActivity() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("time", time);
        returnIntent.putExtra("days", daysState);
        returnIntent.putExtra("name", name);
        returnIntent.putExtra("sound", sound);
        returnIntent.putExtra("vibration", vibration);

        Log.d("alarms", "alarm data: " + Arrays.toString(time) + " / " + Arrays.toString(daysState) + " / " + name + " / " + sound + " / " + vibration);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
