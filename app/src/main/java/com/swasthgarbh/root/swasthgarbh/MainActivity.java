package com.swasthgarbh.root.swasthgarbh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView patient_registration = (TextView)findViewById(R.id.patient_registration);
        patient_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent patient_registration_activity = new Intent(MainActivity.this, com.swasthgarbh.root.swasthgarbh.patient_registration.class);
                startActivity(patient_registration_activity);
            }
        });
//        URL url = new URL("127.0.0.1:8000/cbtbiitr/api/login");
//        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.connect();
//        int responsecode = conn.getResponseCode();
//        if(responsecode != 200)
//            throw new RuntimeException("HttpResponseCode: " +responsecode);
//        else
//        {
//
//        }
    }
}
