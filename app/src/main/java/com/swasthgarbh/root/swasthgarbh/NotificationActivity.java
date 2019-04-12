package com.swasthgarbh.root.swasthgarbh;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);

    NotificationManager notificationManager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this , "com.kaushikshalini.notificationDemo.channelId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,patient_registration.class);
        startActivity(intent);
        finish();

    }
}