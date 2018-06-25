package com.example.orgot.bluelight;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivityWithPermissions extends AppCompatActivity {

    private Button light;
    private PopupWindow popUpWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;
    public static final int PERMISSIONS = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkDrawOverlayPermission()) {
            Log.d("status", "success!");
//        } else {
//            Log.d("status", "failed");
//        }


            final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);


            // assign the light button
            light = (Button) findViewById(R.id.light);
            constraintLayout = (ConstraintLayout) findViewById(R.id.BaseLayout);

            // set up our overlay
            String state = sharedPreferences.getString("state", "off");
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popwindow, null);
            int currentColor = (state.equals("off")) ? Color.argb(0, 0, 0, 0) : Color.argb(50, 0, 0, 255);
            container.setBackgroundColor(currentColor);
//        container.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            container.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);


            // show the overlay
            popUpWindow = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, false);
            popUpWindow.setTouchable(false);
            popUpWindow.setWindowLayoutType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
//            popUpWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 0, 0);

            Log.d("status", "success!");

            // set up the button
            light.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String state = sharedPreferences.getString("state", "off");
                    editor.putString("state", "off");
                    editor.commit();
                    Log.d("state", state);

                    if (state.equals("off")) {


                        int color = Color.argb(50, 0, 0, 255);
                        container.setBackgroundColor(color);


                        editor.putString("state", "on");
                        editor.commit();

                    } else {

                        int color = Color.argb(0, 0, 0, 0);
                        container.setBackgroundColor(color);

                        editor.putString("state", "off");
                        editor.commit();
                    }


                }
            });
        }
    }

    /** code to post/handler request for permission */
    public final static int REQUEST_CODE = 10101;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public boolean checkDrawOverlayPermission() {
        Log.d("permissions", "checking");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d("permissions", "1");

            return true;

        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            Log.d("permissions", "2");

            return false;
        } else {
            Log.d("permissions", "3");

            return true;
        }

    }


}
