package com.indusapp.gpstracking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.websmithing.gpstracker.GpsTrackerAlarmReceiver;
import com.websmithing.gpstracker.R;

import java.util.UUID;

public class GpsTrackerActivity extends AppCompatActivity {
    private static final String TAG = "GpsTrackerActivity";
    private String defaultUploadWebsite;

    private EditText txtUserName;
    private EditText txtWebsite;
    private Button trackingButton;

    private boolean currentlyTracking;
    private RadioGroup intervalRadioGroup;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_tracker);
        Log.d(TAG,"onCreate");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        defaultUploadWebsite = getString(R.string.default_upload_website);

        txtWebsite = findViewById(R.id.txtWebsite);
        txtUserName = findViewById(R.id.txtUserName);
        intervalRadioGroup = findViewById(R.id.intervalRadioGroup);
        trackingButton = findViewById(R.id.trackingButton);
//        txtUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        boolean firstTimeLoadingApp = sharedPreferences.getBoolean("firstTimeLoadingApp", true);
        if (firstTimeLoadingApp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeLoadingApp", false);
            editor.putString("appID",  UUID.randomUUID().toString());
            editor.apply();
        }


        intervalRadioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        saveInterval();
                    }
                });

        trackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                trackLocation();
            }
        });

//        trackLocation();

    }

    private void saveInterval() {
        if (currentlyTracking) {
            Toast.makeText(getApplicationContext(),
                    R.string.user_needs_to_restart_tracking, Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (intervalRadioGroup.getCheckedRadioButtonId()) {
            case R.id.i1:
                editor.putInt("intervalInMinutes", 1);
                break;
            case R.id.i5:
                editor.putInt("intervalInMinutes", 5);
                break;
            case R.id.i15:
                editor.putInt("intervalInMinutes", 15);
                break;
        }

        editor.apply();
    }


    // called when trackingButton is tapped
    protected void trackLocation() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!saveUserSettings()) {
            return;
        }

        Log.i(TAG,"checkIfGooglePlayEnabled "+checkIfGooglePlayEnabled());

        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (currentlyTracking) {
            Log.i(TAG,"cancelAlarmManager -currentlyTracking "+currentlyTracking);

            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            Log.i(TAG,"startAlarmManager -currentlyTracking "+currentlyTracking);
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID",  UUID.randomUUID().toString());
        }

        editor.apply();
        setTrackingButtonState();
    }


    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        // Using PendingIntent with an IntentService
        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        Log.d(TAG, "PendingIntent.getBroadcast -GpsTrackerAlarmReceiver ");

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", intervalInMinutes);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "PendingIntent.getBroadcast -GpsTrackerAlarmReceiver ");
        alarmManager.cancel(pendingIntent);
    }

    private boolean saveUserSettings() {
        if (textFieldsAreEmptyOrHaveSpaces()) {
            return false;
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (intervalRadioGroup.getCheckedRadioButtonId()) {
            case R.id.i1:
                editor.putInt("intervalInMinutes", 1);
                break;
            case R.id.i5:
                editor.putInt("intervalInMinutes", 5);
                break;
            case R.id.i15:
                editor.putInt("intervalInMinutes", 15);
                break;
        }


        editor.putString("userName", txtUserName.getText().toString().trim());
        editor.putString("defaultUploadWebsite", txtWebsite.getText().toString().trim());

        editor.apply();

        return true;
    }

    private boolean textFieldsAreEmptyOrHaveSpaces() {
        txtUserName.setText("MyTest");
        String tempUserName = txtUserName.getText().toString().trim();
        String tempWebsite = txtWebsite.getText().toString().trim();

        if (tempWebsite.length() == 0 || hasSpaces(tempWebsite) || tempUserName.length() == 0 || hasSpaces(tempUserName)) {
            Toast.makeText(this, R.string.textfields_empty_or_spaces, Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    private boolean hasSpaces(String str) {
        return ((str.split(" ").length > 1) ? true : false);
    }

    private void displayUserSettings() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.indusapp.hrms", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);

        switch (intervalInMinutes) {
            case 1:
                intervalRadioGroup.check(R.id.i1);
                break;
            case 5:
                intervalRadioGroup.check(R.id.i5);
                break;
            case 15:
                intervalRadioGroup.check(R.id.i15);
                break;
        }

        txtWebsite.setText(sharedPreferences.getString("defaultUploadWebsite", defaultUploadWebsite));
        txtUserName.setText(sharedPreferences.getString("userName", "MyTest"));
    }

    private boolean checkIfGooglePlayEnabled() {
        Log.d(TAG,"Called checkIfGooglePlayEnabled");


        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            return true;
        } else {

            Log.e(TAG, String.valueOf(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) );
            Log.e(TAG, "unable to connect to google play services.");
            Toast.makeText(getApplicationContext(), R.string.google_play_services_unavailable, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setTrackingButtonState() {
        if (currentlyTracking) {
            trackingButton.setBackgroundResource(R.drawable.green_tracking_button);
            trackingButton.setTextColor(Color.BLACK);
            trackingButton.setText(R.string.tracking_is_on);
        } else {
            trackingButton.setBackgroundResource(R.drawable.red_tracking_button);
            trackingButton.setTextColor(Color.WHITE);
            trackingButton.setText(R.string.tracking_is_off);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        displayUserSettings();



    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
