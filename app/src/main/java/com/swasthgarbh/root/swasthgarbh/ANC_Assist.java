package com.swasthgarbh.root.swasthgarbh;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.googleapis.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ANC_Assist extends AppCompatActivity {

    private DatePickerDialog lmpDatePickerDialog;
    private SimpleDateFormat dateFormatterShow, dateFormatterServer;
    String StartTime, EndTime;
    Calendar newDate1 = Calendar.getInstance();

    EditText lmpDate, eddDate;
    String anc1_date,anc2_date,anc3_date,and4_date,anc5_date,anc6_date,anc7_date,anc8_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anc__assist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lmpDate = (EditText) findViewById(R.id.lmpDate);
        lmpDate.setFocusable(false);
        eddDate = (EditText) findViewById(R.id.eddDate);
        eddDate.setFocusable(false);
        final TextView anc1Date = (TextView) findViewById(R.id.anc1Date);
        final TextView anc2Date = (TextView) findViewById(R.id.anc2Date);
        final TextView anc3Date = (TextView) findViewById(R.id.anc3Date);
        final TextView anc4Date = (TextView) findViewById(R.id.anc4Date);
        final TextView anc5Date = (TextView) findViewById(R.id.anc5Date);
        final TextView anc6Date = (TextView) findViewById(R.id.anc6Date);
        final TextView anc7Date = (TextView) findViewById(R.id.anc7Date);
        final TextView anc8Date = (TextView) findViewById(R.id.anc8Date);

        final TextView firstvisit = (TextView) findViewById(R.id.firstvisit);
        final TextView secondvisit = (TextView) findViewById(R.id.secondvisit);
        final TextView thirdvisit = (TextView) findViewById(R.id.thirdvisit);
        final TextView fourthvisit = (TextView) findViewById(R.id.fourthvisit);

        dateFormatterShow = new SimpleDateFormat("dd-MMMM-yyyy", Locale.US);
        final Calendar newCalendar = Calendar.getInstance();

        lmpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lmpDatePickerDialog.show();
            }
        });

        lmpDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate1.set(year, monthOfYear, dayOfMonth);
                lmpDate.setText(dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 84);
                anc1_date = dateFormatterShow.format(newDate1.getTime());
                anc1Date.setText("12 Weeks: " + dateFormatterShow.format(newDate1.getTime()));
                firstvisit.setText(dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 56);
                anc2_date = dateFormatterShow.format(newDate1.getTime());
                anc2Date.setText("20 Weeks: " + dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 42);
                anc3_date = dateFormatterShow.format(newDate1.getTime());
                anc3Date.setText("26 Weeks: " + dateFormatterShow.format(newDate1.getTime()));
                secondvisit.setText(dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 28);
                and4_date = dateFormatterShow.format(newDate1.getTime());
                anc4Date.setText("30 Weeks: " + dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 28);
                anc5_date = dateFormatterShow.format(newDate1.getTime());
                anc5Date.setText("34 Weeks: " + dateFormatterShow.format(newDate1.getTime()));
                thirdvisit.setText(dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 14);
                anc6_date = dateFormatterShow.format(newDate1.getTime());
                anc6Date.setText("36 Weeks: " + dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 14);
                anc7_date = dateFormatterShow.format(newDate1.getTime());
                anc7Date.setText("38 Weeks: " + dateFormatterShow.format(newDate1.getTime()));
                fourthvisit.setText(dateFormatterShow.format(newDate1.getTime()));

                newDate1.add(Calendar.DATE, 16);
                anc8_date = dateFormatterShow.format(newDate1.getTime());
                anc8Date.setText("40 Weeks: " + dateFormatterShow.format(newDate1.getTime()));

                eddDate.setText(dateFormatterShow.format(newDate1.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "All ANC Dates have been compiled as an email", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
//                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"email@example.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ANC Dates");
                String bodyANC;
                bodyANC = "The ANC Dates are as follows - \n" + "\nANC1: " + anc1_date + "\nANC2: " + anc2_date + "\nANC3: " + anc3_date + "\nANC4 :" + and4_date + "\nANC5 :" + anc5_date + "\nANC6: " + anc6_date + "\nANC7 :" +anc7_date + "\nANC8 :" + anc8_date;
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, bodyANC);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            }
        });

    }


}
