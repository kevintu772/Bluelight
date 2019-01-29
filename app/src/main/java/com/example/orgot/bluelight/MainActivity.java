package com.example.orgot.bluelight;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.AudioManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


//TODO make the alarms fragments

public class MainActivity extends AppCompatActivity {


    private final float DEFAULT_BRIGHTNESS = 0.4f;
    private final int DEFAULT_GREEN = 200;
    private final int DEFAULT_BLUE = 255;
    public static final int ALARM_REQUEST = 1;


    private Button light;
    private int activatedColor = Color.rgb(0, DEFAULT_GREEN, DEFAULT_BLUE);
    private float activatedBrightness = DEFAULT_BRIGHTNESS;
    private boolean vibration;
    private boolean alarmSound;
    private boolean snooze;
    private boolean alarmState;

    private int brightnessProgress;
    AlarmManager alarmManager;
//    private PendingIntent pendingIntent;
//    private TimePicker alarmTimePicker;
    private int green;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private SwipeMenuListView alarmContainer;

    private SharedPreferences pref;
    private AlarmAdapter adapter;
    private SharedPreferences.Editor editor;

    private ArrayList<AlarmObject> alarms;


    public class AlarmObject {

        private int hour;
        private int minute;
        private boolean[] days; //[S, M ,T ,W ,Th, F, Sa]
        private String name;
        private boolean sound;
        private boolean vibration;
        private boolean alarmOn;
        private PendingIntent pendingIntent;
        private Calendar calendar;


        private Object alarmManager;
        private Context mainActivity;


        protected AlarmObject(int hour, int minute, boolean[] days, String name,
                              boolean sound, boolean vibration, AlarmManager am, Activity mainActivity) {
            this.hour = hour;
            this.minute = minute;
            this.days = days;
            this.name = name;
            this.sound = sound;
            this.vibration = vibration;
            this.mainActivity = mainActivity;
            calendar = Calendar.getInstance();

            Log.d("alarm", hour + ":" + minute + "      test");

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            alarmManager = am;
        }

        public String getDaysString() {
            StringBuilder dayString = new StringBuilder();

            String[] dayResource = new String[]{"SUN", "MON", "TUES", "WED", "THURS", "FRI", " SAT"};

            String prefix = "";

//            Boolean used = false;

            for (int i = 0; i < days.length; i++) {

                if (days[i]) {

                    dayString.append(prefix);
                    prefix = " ";
                    dayString.append(dayResource[i]);

//                    used = true;
                }
            }

            if (dayString.toString().equals("SUN SAT")) {
                return "Weekends";
            }
            if (dayString.toString().equals("MON TUES WED THURS FRI")) {
                return "Weekdays";
            }
            if (dayString.toString().equals("SUN MON TUES WED THURS FRI SAT")) {
                return "Everyday";
            }

            return dayString.toString();
        }

        public String getTimeString() {

            StringBuilder sb = new StringBuilder();

            int formatHour = (hour + 11) % 12 + 1;
            String formatMinute = String.format("%02d", minute);
            String amPm = hour > 11 ? " PM" : " AM";

            sb.append(formatHour);
            sb.append(":");
            sb.append(formatMinute);
            sb.append(amPm);

            return sb.toString();

        }

        public void activate() {

            Log.d("alarms" , "calendar: " + calendar.toString());


            if (Calendar.getInstance().after(calendar)) {
                Toast toast = Toast.makeText(getApplicationContext(), "yeet", Toast.LENGTH_SHORT);
                toast.show();
                calendar.add(Calendar.DATE, 1);
            }


            Intent myIntent = new Intent(mainActivity, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, myIntent, 0);

            CharSequence text = "Alarm set for " + timeToText(hour  , minute) ;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();

            ((AlarmManager)this.alarmManager).setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);


        }

        public void deactivate() {

            if (pendingIntent != null) {
                ((AlarmManager)this.alarmManager).cancel(pendingIntent);
            }

        }


        public String toString() {

            String s = "[";

            s += Arrays.toString(days) + ", ";
            s += "[" + getTimeString() + "], ";
            s += sound + ", ";
            s += vibration + ", ";
            s += alarmOn + "]";
            return s;

        }



    }

//    private void addAlarmView(final AlarmObject alarm) {
//        LinearLayout alarmLayout = new LinearLayout(this);
//        alarmLayout.setBackgroundColor(Color.argb(200,220,220,220)); //temp
//        alarmLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150 );
//        mainParams.setMargins(10, 10, 10, 10);
//        alarmLayout.setLayoutParams(mainParams);
//
//        // ***** Left Section ******
//        LinearLayout leftLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f);
//        leftLayout.setOrientation(LinearLayout.VERTICAL);
//        leftLayout.setBackgroundColor(Color.argb(200,180,220,220)); //temp
//        leftLayout.setGravity(Gravity.LEFT);
//        leftLayout.setLayoutParams(params);
//
//        // text view
//        String s = alarm.getTimeString();
//        int start = s.indexOf(":");
//        SpannableString ss1=  new SpannableString(s);
//        ss1.setSpan(new RelativeSizeSpan(2f), start + 2, s.length(), 0); // set size
//        TextView timeText = new TextView(this);
//        timeText.setTextColor(getResources().getColor(R.color.White));
//        timeText.setText(alarm.getTimeString());
//        timeText.setTextSize(12);
//        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
//        timeText.setLayoutParams(timeParams);
//
//        TextView dayText = new TextView(this);
//        dayText.setTextColor(getResources().getColor(R.color.White));
//        dayText.setText(alarm.getDaysString());
//        dayText.setTextSize(12);
//        LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
//        dayText.setLayoutParams(dayParams);
//
//        Log.d("strings", alarm.getTimeString() + " / " + alarm.getDaysString());
//
//        leftLayout.addView(timeText);
//        leftLayout.addView(dayText);
//
//        alarmLayout.addView(leftLayout);
//
//
//        // toggle button
//        Switch toggle = new Switch(this);
//        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//                    alarm.activate();
//                } else {
//                    alarm.deactivate();
//                }
//            }
//        });
//        LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
//        toggle.setLayoutParams(toggleParams);
//
//
//        alarmLayout.addView(toggle);
//
//        alarmContainer.addView(alarmLayout);
//
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        setupDrawer(pref);

        //TODO REMOVE THESE
        // load past temp and brightness settings
        green = pref.getInt("green", 100); // getting Integer
        activatedBrightness = pref.getFloat("brightness", 0.4f);
        brightnessProgress = pref.getInt("brightnessProgress", 0);
        alarms = new ArrayList<AlarmObject>();

        // assign views
//        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
//        final ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        light = (Button) findViewById(R.id.light);

        setupBrightnessBar(pref);
        setupColorBar(pref);
        setupAlarm();

        // set up the button
        light.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (light.getText().equals("Preview")) {
                    activate();
                } else {
                    deactivate();
                }
            }
        });

        //TODO REMOVE TESTING CODE
//        testAlarm();
        //

//        alarmState = pref.getBoolean("alarmToggle", false);
//        alarmToggle.setChecked(alarmState);
//        alarmToggle.setChecked(false);

//        alarmTimePicker.setCurrentHour(hour);
//        alarmTimePicker.setCurrentMinute(minute);
//
//        alarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                hour = alarmTimePicker.getCurrentHour();
//                minute = alarmTimePicker.getCurrentMinute();
//
//                editor.putInt("hour", hour);
//                editor.putInt("minute", minute);
//                editor.putBoolean("alarmToggle", false);
//
//                editor.commit();
//
//                alarmToggle.setChecked(false);
//                if (pendingIntent != null) {
//                    alarmManager.cancel(pendingIntent);
//                }
//
//
//            }
//        });
    }

    //START HERE- FIGURE OUT IF THIS WORKS WHEN IT IS NOT NESTED IN "ONACTIVITYRESULT"/
    private void testAlarm(int hour, int minute){



        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        if (Calendar.getInstance().after(calendar)) {
            Toast toast = Toast.makeText(getApplicationContext(), "yeet", Toast.LENGTH_SHORT);
            toast.show();
            calendar.add(Calendar.DATE, 1);
        }

        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        CharSequence text = "Alarm set for " + timeToText(hour , minute) ;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    private void setupAlarm() {

        alarmContainer = (SwipeMenuListView) findViewById(R.id.alarmContainer);
        Log.d("setup", "adding alarm start");

        adapter = new AlarmAdapter(MainActivity.this, alarms);
        alarmContainer.setAdapter(adapter);


        loadAlarms();

//        for (final AlarmObject alarm : alarms) {
//
//            addAlarmView(alarm);
//
//            //TODO on click listener to open settings
//
//        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem settingItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                settingItem.setBackground(new ColorDrawable(getResources().getColor(R.color.White)));
                // set item width
                settingItem.setWidth(170);
                // set a icon
                settingItem.setIcon(R.drawable.ic_settings);
                // add to menu
                menu.addMenuItem(settingItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);


            }
        };

        alarmContainer.setMenuCreator(creator);

        alarmContainer.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                Log.d("click", "clicked registered at " + position);

                switch (index) {
                    case 0:
                        // settings
                        break;
                    case 1:
                        // delete

                        AlarmObject temp = alarms.get(position);
                        temp.deactivate();
                        alarms.remove(position);
                        alarmContainer.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        Log.d("click", alarms.toString());

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        alarmContainer.setCloseInterpolator(new BounceInterpolator());
        alarmContainer.setOpenInterpolator(new BounceInterpolator());

    }

    private String alarmsToString(){
        String s = "[";

        for (AlarmObject a : alarms) {
            s += a.toString() + ", ";
        }
        s += "]";
        return s;
    }

    private void loadAlarms() {

        Gson gson = new Gson();
        String json = pref.getString("alarms", "");
        AlarmObject[] temp;
        if (json.equals("")) {
            temp = new AlarmObject[0];
        } else {
            temp = gson.fromJson(json, AlarmObject[].class);
        }


        for (AlarmObject alarm : temp) {
            Log.d("alarms", "loading alarm");
            alarms.add(alarm);
        }


        Log.d("alarms", "current alarms list " + json);

    }

    public void addAlarm(View view) {

        Intent intent = new Intent(this, AlarmEditActivity.class);
        startActivityForResult(intent, ALARM_REQUEST);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("results", "exited");

        if (requestCode == ALARM_REQUEST) {
            if (resultCode == RESULT_OK) {

                // TODO edit alarmobject constructor

                int[] time = data.getIntArrayExtra("time");
                boolean[] days = data.getBooleanArrayExtra("days");
                String name = data.getStringExtra("name");
                boolean sound = data.getBooleanExtra("sound", true);
                boolean vibration = data.getBooleanExtra("vibration", true);


                Log.d("data", Arrays.toString(time) + " / " + Arrays.toString(days) + " / " + name + " / " + sound + " / " + vibration);

                AlarmObject alarm = new AlarmObject(time[0], time[1], days, name, sound, vibration, alarmManager, MainActivity.this);
                alarms.add(alarm);

                adapter.notifyDataSetChanged();





                /// TODO: REMOVE THESE- TESTING

//                testAlarm(time[0], time[1]);


                /// TESTING


            }
        }
    }

    private void setupDrawer(final SharedPreferences pref) {

        alarmSound = pref.getBoolean("alarmSound", true);
        vibration = pref.getBoolean("vibration", true);
        snooze = pref.getBoolean("snooze", false);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };



        NavigationView navigationView = findViewById(R.id.navList);


        SwitchCompat soundSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.soundSwitch).getActionView();
        soundSwitch.setChecked(alarmSound);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarmSound = true;
                } else {
                    alarmSound = false;
                }
            }
        });

        SwitchCompat vibrationSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.vibrationToggle).getActionView();
        vibrationSwitch.setChecked(vibration);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vibration = true;
                } else {
                    vibration = false;
                }
            }
        });

        SwitchCompat snoozeSwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.snoozeToggle).getActionView();
        snoozeSwitch.setChecked(snooze);
        snoozeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    snooze = true;
                } else {
                    snooze = false;

                    //TODO: make snooze switch readdable
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupColorBar(SharedPreferences pref) {

        final ImageView tempCircle = (ImageView) findViewById(R.id.temp_circle);
        tempCircle.setColorFilter(Color.rgb(0, green, DEFAULT_BLUE));
        SeekBar colorControl = (SeekBar)findViewById(R.id.colorTempBar);
        colorControl.setProgress(green);

        colorControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

                green = arg1;
                activatedColor = Color.rgb(0, green, DEFAULT_BLUE);
                tempCircle.setColorFilter(activatedColor);

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }});
    }

    private void setupBrightnessBar(final SharedPreferences pref) {

        final ImageView brightCircle = (ImageView) findViewById(R.id.brightness_circle);
        SeekBar brightnessControl = (SeekBar)findViewById(R.id.brightnessBar);


        int min = 150;
        int max = 245;
        int step = (max - min) / 5;
        int shade = min + step * brightnessProgress;
        brightCircle.setColorFilter(Color.rgb(shade,shade,shade));

        brightnessProgress = pref.getInt("brightnessProgress", 0);
        brightnessControl.setProgress(brightnessProgress);

        brightnessControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {


                WindowManager.LayoutParams layout = getWindow().getAttributes();
                activatedBrightness = arg1 * 3F / 25F + 0.4F;
                brightnessProgress = arg1;

                int min = 150;
                int max = 245;
                int step = (max - min) / 5;
                int shade = min + step * brightnessProgress;
                brightCircle.setColorFilter(Color.rgb(shade,shade,shade));




                if (!light.getText().equals("Preview")) {
                    getWindow().setAttributes(layout);
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }});
    }

    public void deactivate() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = -1F;
        getWindow().setAttributes(layout);

        light.setText("Preview");
    }

    public void activate() {
//        constraintLayout.setBackgroundColor(activatedColor);
//
//        WindowManager.LayoutParams layout = getWindow().getAttributes();
//        layout.screenBrightness = activatedBrightness;
//        getWindow().setAttributes(layout);
//
//        light.setText("Stop Preview");

        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("vibration", false);
        intent.putExtra("alarmSound", false);
        startActivity(intent);
    }



    @Override
    public void onStart() {
        super.onStart();
    }

//    public void onToggleClicked(View view) {
//        if (((ToggleButton) view).isChecked()) {
//            editor.putBoolean("alarmToggle", true);
////            setupAlarm2();
//            startAlarm();
//        } else {
//            editor.putBoolean("alarmToggle", false);
//            if (pendingIntent != null) {
//                alarmManager.cancel(pendingIntent);
//            }
//        }
//        editor.commit();
//
//    }

//    protected void setupAlarm2() {
//
//        hour = alarmTimePicker.getHour();
//        minute = alarmTimePicker.getMinute();
//
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute - 1);
//
//        if (Calendar.getInstance().after(calendar)) {
////            Toast toast = Toast.makeText(getApplicationContext(), "yeet", Toast.LENGTH_SHORT);
////            toast.show();
//            calendar.add(Calendar.DATE, 1);
//        }
//
//        editor.putInt("hour", hour);
//        editor.putInt("minute", minute);
//
//        editor.commit();
//
//        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
//
//    }

//    private void startAlarm() {
//
//        Context context = getApplicationContext();
//        CharSequence text = "Alarm set for " + timeToText(hour  , minute) ;
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//
//    }

    private static String timeToText(int hour, int minute) {

        int formatHour = (hour + 11) % 12 + 1;
        String formatMinute = String.format("%02d", minute);
        String amPm = hour > 11 ? " PM" : " AM";

        return formatHour + ":" + formatMinute + amPm;

    }

    private void saveData() {

        // NOTE: we convert our alarms arraylist into a
        // array to avoid java's issues with generics
        // consider upgrading our arraylist to a growing
        // array later if performace is an issue.
        Gson gson = new Gson();

        AlarmObject[] temp = new AlarmObject[alarms.size()];

        for (int i = 0; i < alarms.size(); i++) {

            temp[i] = alarms.get(i);
//            Log.d("alarms", "hi " + temp[i].toString());

        }
//        Log.d("alarms", "hi " + temp.toString());
//
        String json = gson.toJson(temp); // myObject - instance of MyObject
//        Log.d("alarms", "hi1 " + json);
        editor.putString("alarms", json);
//        Log.d("alarms", "hi " + json);


        editor.putBoolean("alarmSound", alarmSound);
        editor.putBoolean("vibration", vibration);
        editor.putBoolean("snooze", snooze);
        editor.putInt("green", green); // Storing integer
        editor.putFloat("brightness", activatedBrightness); // Storing integer
        editor.putInt("brightnessProgress", brightnessProgress);

        editor.commit();

    }


    @Override
    public void onStop(){


//        Log.d("alarms", "saving");

        saveData();

        super.onStop();

    }

}

