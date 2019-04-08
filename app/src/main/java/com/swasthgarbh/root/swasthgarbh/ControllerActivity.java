package com.swasthgarbh.root.swasthgarbh;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

import java.util.Calendar;
import java.util.HashMap;

public class ControllerActivity extends Activity {
    SessionManager session;
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    MyFirebaseInstanceIDService fcm;
    Button sign_in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivities();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void startActivities(){
        session = new SessionManager(ControllerActivity.this);
        session.checkLogin();
        fcm = new MyFirebaseInstanceIDService(ControllerActivity.this);
        final HashMap<String, String> user = session.getUserDetails();
        Log.d("DATA2", user.toString());

        fcm.onTokenRefresh();
        if ("doctor".equals(user.get("type"))) {
            Intent i = new Intent(ControllerActivity.this, DoctorScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else if ("patient".equals(user.get("type"))) {
            Intent i = new Intent(ControllerActivity.this, patient_registration.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

}