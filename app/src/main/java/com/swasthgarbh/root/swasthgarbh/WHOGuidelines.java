package com.swasthgarbh.root.swasthgarbh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static android.app.PendingIntent.getActivity;
import static com.swasthgarbh.root.swasthgarbh.patient_registration.session;

public class WHOGuidelines extends AppCompatActivity {

    CheckBox anc1_diabtese, anc1_anemia, anc1_hiv, anc1_ultrasound, anc1_tetnus, anc1_urine, anc2_diabtese, anc2_anemia, anc3_diabtese, anc3_anemia, anc3_urine, anc4_diabtese, anc5_diabtese, anc5_urine, anc6_diabtese, anc6_anemia, anc7_diabtese, anc8_diabtese;
    int clickedPatientId;
    ProgressBar pb;
    Button updateWhoData;
    Calendar newDate1 = Calendar.getInstance();
    int key=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoguidelines);


        anc1_diabtese = (CheckBox) findViewById(R.id.anc1dia);
//        anc1_diabtese.setOnClickListener(this);
        anc1_anemia = (CheckBox) findViewById(R.id.anc1ana);
//        anc1_anemia.setOnClickListener(this);
        anc1_hiv = (CheckBox) findViewById(R.id.anc1hiv);
//        anc1_hiv.setOnClickListener(this);
        anc1_ultrasound = (CheckBox) findViewById(R.id.anc1ult);
//        anc1_ultrasound.setOnClickListener(this);
        anc1_tetnus = (CheckBox) findViewById(R.id.anc1tet);
//        anc1_tetnus.setOnClickListener(this);
        anc1_urine = (CheckBox) findViewById(R.id.anc1uri);
//        anc1_urine.setOnClickListener(this);

        anc2_diabtese = (CheckBox) findViewById(R.id.anc2dia);
//        anc2_diabtese.setOnClickListener(this);
        anc2_anemia = (CheckBox) findViewById(R.id.anc2ult);
//        anc2_anemia.setOnClickListener(this);

        anc3_diabtese = (CheckBox) findViewById(R.id.anc3dia);
//        anc3_diabtese.setOnClickListener(this);
        anc3_anemia = (CheckBox) findViewById(R.id.anc3ana);
//        anc3_anemia.setOnClickListener(this);
        anc3_urine = (CheckBox) findViewById(R.id.anc3uri);
//        anc3_urine.setOnClickListener(this);

        anc4_diabtese = (CheckBox) findViewById(R.id.anc4dia);
//        anc4_diabtese.setOnClickListener(this);

        anc5_diabtese = (CheckBox) findViewById(R.id.anc5dia);
//        anc5_diabtese.setOnClickListener(this);
        anc5_urine = (CheckBox) findViewById(R.id.anc5uri);
//        anc5_urine.setOnClickListener(this);

        anc6_diabtese = (CheckBox) findViewById(R.id.anc6dia);
//        anc6_diabtese.setOnClickListener(this);
        anc6_anemia = (CheckBox) findViewById(R.id.anc6ana);
//        anc6_anemia.setOnClickListener(this);

        anc7_diabtese = (CheckBox) findViewById(R.id.anc7dia);
//        anc7_diabtese.setOnClickListener(this);

        anc8_diabtese = (CheckBox) findViewById(R.id.anc8dia);
//        anc8_diabtese.setOnClickListener(this);

        pb = (ProgressBar) findViewById(R.id.sendPB);
        pb.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Clinical Tests (WHO Guidlines)");

        session = new SessionManager(this);
//      Getting the WHO Data
        Log.i("infoooo", "onCreate: " + "in whooo");
        clickedPatientId= getIntent().getIntExtra("EXTRA_PATIENT_ID", 0);
        String url = ApplicationController.get_base_url() + "swasthgarbh/patient/" + clickedPatientId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("apihit", response.toString());
                        try {
                            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                            TextView lmpDate = (TextView) findViewById(R.id.lmpDate);
                            TextView eddDate = (TextView) findViewById(R.id.eddDate);
                            TextView anc1Date = (TextView) findViewById(R.id.anc1Date);
                            TextView anc2Date = (TextView) findViewById(R.id.anc2Date);
                            TextView anc3Date = (TextView) findViewById(R.id.anc3Date);
                            TextView anc4Date = (TextView) findViewById(R.id.anc4Date);
                            TextView anc5Date = (TextView) findViewById(R.id.anc5Date);
                            TextView anc6Date = (TextView) findViewById(R.id.anc6Date);
                            TextView anc7Date = (TextView) findViewById(R.id.anc7Date);
                            TextView anc8Date = (TextView) findViewById(R.id.anc8Date);

                            String date_date = response.getString("startDate").split("T")[0].split("-")[2];
                            String date_month = response.getString("startDate").split("T")[0].split("-")[1];
                            String date_year = response.getString("startDate").split("T")[0].split("-")[0];

                            String lmpDateString = date_date + "/" + date_month + "/" + date_year;
                            String eddDateString = date_year + "/" + date_month + "/" + date_date;
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

//                            newDate1.set(Integer.parseInt(date_year), Integer.parseInt(date_month), Integer.parseInt(date_date));
                            Date d = sdf.parse(lmpDateString);
                            newDate1.setTime(d);
                            newDate1.set(Calendar.HOUR,9);
                            newDate1.set(Calendar.MINUTE,0);
                            newDate1.set(Calendar.SECOND,0);
                            newDate1.set(Calendar.AM_PM,Calendar.AM);

                            lmpDate.setText(sdf.format(d));
                            newDate1.add(Calendar.DATE, 84);
                            anc1Date.setText("12 Weeks - " + sdf.format(newDate1.getTime()));
                            anc1_diabtese.setChecked(response.getBoolean("anc1_diabtese"));
                            anc1_anemia.setChecked(response.getBoolean("anc1_anemia"));
                            anc1_hiv.setChecked(response.getBoolean("anc1_hiv"));
                            anc1_ultrasound.setChecked(response.getBoolean("anc1_ultrasound"));
                            anc1_tetnus.setChecked(response.getBoolean("anc1_tetnus"));
                            anc1_urine.setChecked(response.getBoolean("anc1_urine"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);

                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){
                                //when current date is before c(i.e. 2 days before newDate1)
                               if(c.compareTo(Calendar.getInstance())>0){
                                   setNotification(manager,c,key);
                               }
                               //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                               //but less than newDate1
                               else {
                                   setNotification(manager,Calendar.getInstance(),++key);
                               }


                            }

                            newDate1.add(Calendar.DATE, 56);
                            anc2Date.setText("20 Weeks - " + sdf.format(newDate1.getTime()));
                            anc2_diabtese.setChecked(response.getBoolean("anc2_diabtese"));
                            anc2_anemia.setChecked(response.getBoolean("anc2_anemia"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }


                            newDate1.add(Calendar.DATE, 42);
                            anc3Date.setText("26 Weeks - " + sdf.format(newDate1.getTime()));
                            anc3_diabtese.setChecked(response.getBoolean("anc3_diabtese"));
                            anc3_anemia.setChecked(response.getBoolean("anc3_anemia"));
                            anc3_urine.setChecked(response.getBoolean("anc3_urine"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }


                            newDate1.add(Calendar.DATE, 28);
                            anc4Date.setText("30 Weeks - " + sdf.format(newDate1.getTime()));
                            anc4_diabtese.setChecked(response.getBoolean("anc4_diabtese"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0) {


                                //when current date is before c(i.e. 2 days before newDate1)
                                if (c.compareTo(Calendar.getInstance()) > 0) {
                                    setNotification(manager, c, ++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager, Calendar.getInstance(), ++key);
                                }

                            }


                            newDate1.add(Calendar.DATE, 28);
                            anc5Date.setText("34 Weeks - " + sdf.format(newDate1.getTime()));
                            anc5_diabtese.setChecked(response.getBoolean("anc5_diabtese"));
                            anc5_urine.setChecked(response.getBoolean("anc5_urine"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }



                            newDate1.add(Calendar.DATE, 14);
                            anc6Date.setText("36 Weeks - " + sdf.format(newDate1.getTime()));
                            anc6_diabtese.setChecked(response.getBoolean("anc6_diabtese"));
                            anc6_anemia.setChecked(response.getBoolean("anc6_anemia"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }


                            newDate1.add(Calendar.DATE, 14);
                            anc7Date.setText("38 Weeks - " + sdf.format(newDate1.getTime()));
                            anc7_diabtese.setChecked(response.getBoolean("anc7_diabtese"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }



                            newDate1.add(Calendar.DATE, 16);
                            anc8Date.setText("40 Weeks - " + sdf.format(newDate1.getTime()));
                            anc8_diabtese.setChecked(response.getBoolean("anc8_diabtese"));
                            c = (Calendar) newDate1.clone();
                            c.add(Calendar.DATE,-2);
                            //when current date is less than newDate1
                            if(newDate1.compareTo(Calendar.getInstance())>0){

                                //when current date is before c(i.e. 2 days before newDate1)
                                if(c.compareTo(Calendar.getInstance())>0){
                                    setNotification(manager,c,++key);
                                }
                                //when current date is greater than or equal to c(i.e. 2 days before newDate1)
                                //but less than newDate1
                                else {
                                    setNotification(manager,Calendar.getInstance(),++key);
                                }


                            }



                            eddDate.setText(sdf.format(newDate1.getTime()));
                            //set notifications

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                            edit.commit();
                    }


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "Error Message: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Token " + session.getUserDetails().get("Token"));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjReq);

        updateWhoData = (Button) findViewById(R.id.updateWhoData);
        LinearLayout a = (LinearLayout)findViewById(R.id.linearLayoutForAll);
        RelativeLayout b = (RelativeLayout)findViewById(R.id.updateButtonLayout);
        final HashMap<String, String> user = session.getUserDetails();
        if ("doctor".equals(user.get("type"))){
            a.removeView(b);
//            updateWhoData.setVisibility(View.GONE);
//            pb.setVisibility(View.GONE);
//            anc8_diabtese.setEnabled(false);
        }
        updateWhoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWhoData();
            }
        });
    }
    public void setNotification(AlarmManager manager,Calendar c,int key){
        Intent alarmIntent = new Intent(this, NotificationReceiver.class);
        alarmIntent.putExtra("Key",key);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, key, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    public void updateWhoData(){
        pb.setVisibility(View.VISIBLE);
        updateWhoData.setVisibility(View.GONE);
        String url = ApplicationController.get_base_url() + "swasthgarbh/patient/" + session.getUserDetails().get("id");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PATCH,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent i = new Intent(WHOGuidelines.this, patient_registration.class);
                        pb.setVisibility(View.GONE);
                        startActivity(i);
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "Error Message: " + error.getMessage());
            }
        }) {

            @Override
            public byte[] getBody() {
                JSONObject params = new JSONObject();
                try {
                    params.put("anc1_diabtese", anc1_diabtese.isChecked());
                    params.put("anc1_anemia", anc1_anemia.isChecked());
                    params.put("anc1_hiv", anc1_hiv.isChecked());
                    params.put("anc1_ultrasound", anc1_ultrasound.isChecked());
                    params.put("anc1_tetnus", anc1_tetnus.isChecked());
                    params.put("anc1_urine", anc1_urine.isChecked());
                    params.put("anc2_diabtese", anc2_diabtese.isChecked());
                    params.put("anc2_anemia", anc2_anemia.isChecked());
                    params.put("anc3_diabtese", anc3_diabtese.isChecked());
                    params.put("anc3_anemia", anc3_anemia.isChecked());
                    params.put("anc3_urine", anc3_urine.isChecked());
                    params.put("anc4_diabtese", anc4_diabtese.isChecked());
                    params.put("anc5_diabtese", anc5_diabtese.isChecked());
                    params.put("anc5_urine", anc5_urine.isChecked());
                    params.put("anc6_diabtese", anc6_diabtese.isChecked());
                    params.put("anc6_anemia", anc6_anemia.isChecked());
                    params.put("anc7_diabtese", anc7_diabtese.isChecked());
                    params.put("anc8_diabtese", anc8_diabtese.isChecked());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + session.getUserDetails().get("Token"));
                Log.d("TAG", "Token " + session.getUserDetails().get("Token"));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjReq);
    }

}

