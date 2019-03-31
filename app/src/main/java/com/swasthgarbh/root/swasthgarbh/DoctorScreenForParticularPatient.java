package com.swasthgarbh.root.swasthgarbh;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DoctorScreenForParticularPatient extends AppCompatActivity implements View.OnClickListener{

    private Button whoGuideLines, logOutButton;
    static SessionManager session;
//    ArrayList<patient_data_listview_class> patientRowData = new ArrayList<patient_data_listview_class>();
    ListView patient_list_view;
    patientDataAdapter adapter;
    private int doctorId, clickedPatientId;
    TextView doctorName, patientName, pregStartDate, patientMobile, whoFollowing, patientEmail, dummyData;
    ImageView callDoctor, verified;
    Button notify, send;
    Dialog notify_dialog;
    EditText message_box;
    static String p_id, to_fcm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_detail_in_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        if (item.getItemId() == R.id.action_logout){
            logout(this);
            return true;
        } else if (item.getItemId() == R.id.patientImages){
            Intent intent = new Intent(this, all_images_view.class);
            intent.putExtra("EXTRA_PATIENT_ID", clickedPatientId);
            startActivity(intent);
        } else if (item.getItemId() == R.id.who_details){
            Intent intent = new Intent(DoctorScreenForParticularPatient.this, WHOGuidelines.class);
            intent.putExtra("EXTRA_PATIENT_ID", clickedPatientId);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_refresh){
            getPatientData(clickedPatientId);
        } else if (item.getItemId() == R.id.patientDetails){
            Intent intent = new Intent(DoctorScreenForParticularPatient.this, patientDetailInDoctor.class);
            intent.putExtra("EXTRA_PATIENT_ID", clickedPatientId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(Context _c) {
        session = new SessionManager(_c);
        session.logoutUser();
        Intent i = new Intent(DoctorScreenForParticularPatient.this, ControllerActivity.class);
        startActivity(i);
    }

    private String get_time_period(String time) {
        if (Integer.parseInt(time.split(":")[0]) > 12) {
            return "PM";
        } else {
            return "AM";
        }
    }

    private String get_time_min(String time) {
        return "" + time.split(":")[1];
    }

    private String get_time_hour(String time) {
        Log.d("TAG", time.toString());
        if (Integer.parseInt(time.split(":")[0]) > 12) {
            int hr_int = Integer.parseInt(time.split(":")[0]) - 12;
            return "" + hr_int;
        } else {
            return "" + time.split(":")[0];
        }
    }

    private String get_date_year(String date) {
        return date.split("-")[0];
    }

    private String get_date_month(String date) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int mon_int = Integer.parseInt(date.split("-")[1]);
        return months[mon_int];
    }

    private String get_date_date(String date) {
        return date.split("-")[2].split("T")[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_screen_for_particular_patient);

        patientMobile = (TextView) findViewById(R.id.patientMobile);
        patientEmail = (TextView) findViewById(R.id.patientEmail);
        patientName = (TextView) findViewById(R.id.patientName);
        pregStartDate = (TextView) findViewById(R.id.pregStartDate);
        whoFollowing = (TextView) findViewById(R.id.whoFollowing);
        callDoctor = (ImageView)  findViewById(R.id.callDoctor);
        dummyData = (TextView)  findViewById(R.id.dummyData);
        verified = (ImageView)findViewById(R.id.verified);
        dummyData.setVisibility(View.GONE);
//        Button logOutButton = (Button) findViewById(R.id.analyseResult);
//        logOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logout(patient_registration.this);
//            }
//        });


        Button NotificationByDoctor = (Button) findViewById(R.id.NotificationByDoctor);
        NotificationByDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorScreenForParticularPatient.this, patientDataEntry.class);
                startActivity(intent);
            }
        });

        callDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_no= patientMobile.getText().toString().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        notify = (Button) findViewById(R.id.NotificationByDoctor);
        notify.setOnClickListener(this);

        getSupportActionBar().setTitle("Patient Details");

        session = new SessionManager(this);

        clickedPatientId= getIntent().getIntExtra("EXTRA_PATIENT_ID", 1);
        getPatientData(clickedPatientId);
        p_id = String.valueOf(clickedPatientId);

        Button MedicineReminderDoctor = (Button) findViewById(R.id.MedicineReminderDoctor);
        MedicineReminderDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorScreenForParticularPatient.this, MedicineReminder.class);
                intent.putExtra("EXTRA_PATIENT_ID", clickedPatientId);
                startActivity(intent);
            }
        });
    }

    /*
     * to fill the patient details
     * API for doctor details
     * */
    public void getPatientData(int pId){
        final ArrayList<patient_data_listview_class> patientRowData = new ArrayList<patient_data_listview_class>();
        final ProgressBar listPB = (ProgressBar) findViewById(R.id.listPB);
        final ProgressBar chartPB = (ProgressBar) findViewById(R.id.chartPB);
        listPB.setVisibility(View.VISIBLE);
        chartPB.setVisibility(View.VISIBLE);
        String url = ApplicationController.get_base_url() + "api/patient/" + pId ;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("apihitttttttt", response.toString());
                        try {
                            doctorId = response.getInt("doctor");
                            patientName.setText(response.getString("name"));
                            patientEmail.setText(response.getString("email"));
                            patientMobile.setText(String.valueOf(response.getLong("mobile")));
                            whoFollowing.setText(response.getString("who_following"));
                            if(!response.getBoolean("verified")){
                                verified.setVisibility(View.GONE);
                            }
                            String date = response.getString("lmp").split("T")[0].split("-")[2] + "-" + response.getString("lmp").split("T")[0].split("-")[1] + "-" + response.getString("lmp").split("T")[0].split("-")[0];
                            pregStartDate.setText(date);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMMM-yyyy");
                            Date d = sdf.parse(date);
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.DAY_OF_MONTH, 282);
                            pregStartDate.setText(sdf2.format(c.getTime()));
                            JSONArray patientBpData = response.getJSONArray("data");
                            int dummyDataVariable = 0;

                            if(patientBpData.length()==0){
                                //dummy data for new patient
                                Random rand = new Random();
                                JSONArray ja = new JSONArray();
                                for (int i=0;i<30;i++){
                                    JSONObject jo = new JSONObject();
                                    jo.put("systolic", (int)(Math.random() * ((170 - 110) + 1)) + 110);
                                    jo.put("diastolic", (int)(Math.random() * ((115 - 60) + 1)) + 60);
                                    jo.put("urine_albumin", (int) (Math.random() * 4 + 1));
                                    jo.put("weight", (int)(Math.random() * ((80 - 50) + 1)) + 50);
                                    jo.put("bleeding_per_vaginum", (int) (Math.random() * 4 + 1));

                                    jo.put("headache", rand.nextBoolean());
                                    jo.put("abdominal_pain", rand.nextBoolean());
                                    jo.put("visual_problems", rand.nextBoolean());
                                    jo.put("decreased_fetal_movements", rand.nextBoolean());
                                    jo.put("swelling_in_hands_or_face", rand.nextBoolean());
                                    jo.put("extra_comments", "No comments");

                                    jo.put("time_stamp", "2018-" + String.valueOf((int)(Math.random() * 12 + 1)) + "-" + String.valueOf((int)(Math.random() * 30 + 1)) + "T01:25:37.199340+05:30");
                                    jo.put("pk", session.getUserDetails().get("id"));
                                    ja.put(jo);
                                }
                                Log.i("dataaaaaaa", "onResponse: " + ja);
                                patientBpData = ja;
                                Toast.makeText(DoctorScreenForParticularPatient.this, "Dummy Data", Toast.LENGTH_LONG).show();
                                dummyData.setVisibility(View.VISIBLE);
                                dummyDataVariable = 1;
                            }
                            if(patientBpData.length()!=0) {

                                LineChart chart = (LineChart) findViewById(R.id.chart);

                                ArrayList<Entry> yValues = new ArrayList<Entry>();
                                ArrayList<Integer> colorssys = new ArrayList<Integer>();
                                ArrayList<Entry> y2Values = new ArrayList<Entry>();
                                ArrayList<Integer> colorsdys = new ArrayList<Integer>();
                                ArrayList<Entry> y3Values = new ArrayList<Entry>();
                                for (int i = 0; i < patientBpData.length(); i++) {
                                    JSONObject po = (JSONObject) patientBpData.get(i);
                                    patient_data_listview_class pr = new patient_data_listview_class(dummyDataVariable, patientBpData.length(), po.getString("time_stamp"),po.getInt("systolic"), po.getInt("diastolic"), po.getDouble("urine_albumin") ,po.getInt("weight"), po.getBoolean("headache"), po.getBoolean("abdominal_pain"), po.getBoolean("visual_problems"), po.getDouble("bleeding_per_vaginum") , po.getBoolean("decreased_fetal_movements"), po.getBoolean("swelling_in_hands_or_face"), po.getString("extra_comments"));
                                    patientRowData.add(pr);
                                    Log.i("Data in array", "" + String.valueOf(patientBpData.get(i)));
                                }
                                Integer sys,dys,wt;

                                Integer sys_temp = 0, dys_temp=0, wt_temp=0, update_temp_sys=0, update_temp_dys=0, update_temp_wt=0;

                                for (int i = patientBpData.length()-1; i>=0; i--) {
                                    JSONObject po = (JSONObject) patientBpData.get(i);
                                    if(patientBpData.length() > 1){
                                    if(update_temp_sys == 0){
                                        if(po.getInt("systolic") == 0){
                                            sys_temp = ((JSONObject) patientBpData.get(i+1)).getInt("systolic");
                                            update_temp_sys = 1;
                                        }
                                    }
                                    if(po.getInt("systolic") != 0){
                                        update_temp_sys = 0;
                                    }

                                    if(update_temp_dys == 0){
                                        if(po.getInt("diastolic") == 0){
                                            dys_temp = ((JSONObject) patientBpData.get(i+1)).getInt("diastolic");
                                            update_temp_dys = 1;
                                        }
                                    }
                                    if(po.getInt("diastolic") != 0){
                                        update_temp_dys = 0;
                                    }

                                    if(update_temp_wt == 0){
                                        if(po.getInt("weight") == 0){
                                            wt_temp = ((JSONObject) patientBpData.get(i+1)).getInt("weight");
                                            update_temp_wt = 1;
                                        }
                                    }
                                    if(po.getInt("weight") != 0){
                                        update_temp_wt = 0;
                                    }
                                }


                                    sys = (po.getInt("systolic") != 0) ? po.getInt("systolic") : sys_temp;
                                    dys = (po.getInt("diastolic") != 0) ? po.getInt("diastolic") : dys_temp;
                                    wt = (po.getInt("weight") != 0) ? po.getInt("weight") : wt_temp;


                                    int len = patientBpData.length();
//                                    if(po.getInt("systolic") != 0 || len == 1){
                                        yValues.add(new Entry(patientBpData.length()-i, sys));

//                                    }

                                    if(po.getInt("systolic") >160){
                                        colorssys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chart6)) ;
                                    } else if (po.getInt("systolic") > 140 && po.getInt("systolic") <= 160){
                                        colorssys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chart4)) ;
                                    } else if (po.getInt("systolic") <= 145){
                                        colorssys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chartsys)) ;
                                    }

                                    if(po.getInt("diastolic") > 110){
                                        colorsdys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chart6)) ;
                                    } else if (po.getInt("diastolic") > 90 && po.getInt("diastolic") <= 110 ){
                                        colorsdys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chart4)) ;
                                    } else if (po.getInt("diastolic") <= 90 || len == 1){
                                        colorsdys.add(ContextCompat.getColor(DoctorScreenForParticularPatient.this, R.color.chartdys)) ;
                                    }

//                                    if(po.getInt("diastolic") != 0 || len == 1){
                                        y2Values.add(new Entry(patientBpData.length()-i, dys));
//                                    }
//                                    if(po.getInt("weight") != 0 || len == 1){
                                        y3Values.add(new Entry(patientBpData.length()-i, wt));
//                                    }
                                }

                                chart.setDragEnabled(true);
                                chart.setScaleEnabled(true);
                                chart.getDescription().setEnabled(false);

                                LineDataSet set1 = new LineDataSet(yValues, "Systolic BP");
                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                                LineDataSet set2 = new LineDataSet(y2Values, "Diastolic BP");
                                set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                                LineDataSet set3 = new LineDataSet(y3Values, "Weight");
                                set3.setAxisDependency(YAxis.AxisDependency.LEFT);
                                // Color.rgb(171, 235, 198) sys
                                // Color.rgb(19, 141, 117) dys
                                set1.setFillAlpha(110);
                                set1.setLineWidth(3.5f);
                                set1.setColor(Color.rgb(19, 141, 117));
                                set1.setDrawValues(false);
//                                set1.setDrawCircles(false);
                                set1.setCircleColors(colorssys);

                                set2.setLineWidth(2f);
                                set2.setColor(Color.rgb(171, 235, 198));
                                set2.setDrawValues(false);
//                                set2.setDrawCircles(false);
                                set2.setCircleColors(colorsdys);

                                set3.setColor(Color.rgb(93, 173, 226));
                                set3.setLineWidth(2f);
                                set3.setDrawValues(false);
                                set3.setDrawCircles(false);

                                YAxis leftAxis = chart.getAxisLeft();
                                LimitLine ll = new LimitLine(160f, "Critical");
                                ll.setLineColor(Color.rgb(19, 141, 117));
                                ll.setLineWidth(1f);
                                ll.setTextColor(Color.rgb(19, 141, 117));
                                ll.setTextSize(12f);
                                ll.enableDashedLine(4, 2, 0);
                                leftAxis.addLimitLine(ll);

                                LimitLine l2 = new LimitLine(90f, "Critical");
                                l2.setLineColor(Color.rgb(171, 235, 198));
                                l2.setLineWidth(1f);
                                l2.setTextColor(Color.rgb(171, 235, 198));
                                l2.setTextSize(12f);
                                l2.enableDashedLine(4, 2, 0);
                                leftAxis.addLimitLine(l2);

                                set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                                set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                                set3.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1);
                                dataSets.add(set2);
                                dataSets.add(set3);

                                LineData data = new LineData(dataSets);
                                chart.setData(data);
                                chart.invalidate();
                                chart.animateXY(3000, 3000);
                                /*
                                 * To set the charts
                                 ******************************/

                                patientDataAdapter itemsAdapter = new patientDataAdapter(DoctorScreenForParticularPatient.this, patientRowData);
                                ListView listView = (ListView) findViewById(R.id.patient_data_list_view);
                                listView.setAdapter(itemsAdapter);
                                JSONObject device = (JSONObject) response.get("device");
                                to_fcm = device.getString("device_id");
                            }
                            listPB.setVisibility(View.GONE);
                            chartPB.setVisibility(View.GONE);
                            /******************************
                             * To set the charts
                             */
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace ( );
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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.NotificationByDoctor) {
            showNotifyDialog();
        }
    }

    private void showNotifyDialog() {
        notify_dialog = new Dialog(this);
        notify_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notify_dialog.setContentView(R.layout.notification_send);

        message_box = notify_dialog.findViewById(R.id.msg_box);
        send = notify_dialog.findViewById(R.id.sendNotif);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(notify_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        notify_dialog.getWindow().setAttributes(lp);


        notify_dialog.setCancelable(true);
        notify_dialog.show();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.sendNotif) {
                    if (message_box.getText().length() == 0) {
                        Toast.makeText(DoctorScreenForParticularPatient.this, "enter your message", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String url = ApplicationController.get_base_url() + "dhadkan/api/notification";
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("TAG", response.toString());

//
                                    try {
                                        JSONArray patient_details = response.getJSONArray("data");
                                        Log.d("TAG", patient_details.toString());
                                    } catch (JSONException e) {
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
                        public byte[] getBody() {
                            JSONObject params = new JSONObject();

                            try {
                                params.put("p_id", p_id);
                                params.put("message", "" + message_box.getText());
                                params.put("to", "" + to_fcm);
                                params.put("from", "" + "me");
                                notify_dialog.dismiss();
//                        params.put("date_of_birth", );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            return params.toString().getBytes();

                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", "Token " + session.getUserDetails().get("Token"));;
                            return params;
                        }
                    };
                    ApplicationController.getInstance().addToRequestQueue(jsonObjReq);

                }
            }
        });
    }
}
