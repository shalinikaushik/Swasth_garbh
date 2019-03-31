package com.swasthgarbh.root.swasthgarbh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DoctorScreen extends AppCompatActivity {

    private Button whoGuideLines, logOutButton;
    static SessionManager session;
    ArrayList<patient_data_listview_class> patientRowData = new ArrayList<patient_data_listview_class>();
    ListView patient_list_view;
    patientDataAdapter adapter;
    private int doctorId;
    TextView doctorName, doctorEmail, doctorTotalPatients, doctorHospital, doctorSpeciality, dummyData;
    ImageView callDoctor, verified;
    ProgressBar barPB;
    ProgressBar piePB;
    boolean doubleBackToExitPressedOnce = false;
    int keepDummyData = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        if (item.getItemId() == R.id.action_logout) {
            logout(this);
            return true;
        } else if (item.getItemId() == R.id.action_notification){
            i = new Intent(this, PatientNotifications.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.action_refresh){
            getDoctorData();
            getDoctorAllPatientsData();
        } else if (item.getItemId() == R.id.offlineAnc) {
            i = new Intent(this, ANC_Assist.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(Context _c) {
        session = new SessionManager(_c);
        session.logoutUser();
        Intent i = new Intent(DoctorScreen.this, ControllerActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
//            logout(patient_registration.this);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_screen);

        doctorName = (TextView) findViewById(R.id.registeredDoctorName);
        doctorHospital = (TextView) findViewById(R.id.registeredDoctorHospital);
        doctorEmail = (TextView) findViewById(R.id.registeredDoctorEmail);
        doctorTotalPatients = (TextView) findViewById(R.id.registeredDoctorTotalPatients);
        doctorSpeciality = (TextView) findViewById(R.id.registeredDoctorSpeciality);
        verified = (ImageView)findViewById(R.id.verified);
        barPB = (ProgressBar) findViewById(R.id.barPB);
        piePB = (ProgressBar) findViewById(R.id.piePB);
        barPB.setVisibility(View.VISIBLE);
        piePB.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("Doctor");

        dummyData = (TextView)  findViewById(R.id.dummyData);
        dummyData.setVisibility(View.GONE);

        session = new SessionManager(this);

        Button allPatientsDoctor = (Button) findViewById(R.id.allPatientsDoctor);
        allPatientsDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorScreen.this, AllPatientListActivity.class);
                startActivity(intent);
            }
        });

        Button allNotificationsDoctor = (Button) findViewById(R.id.allNotificationsDoctor);
        allNotificationsDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DoctorScreen.this, PatientNotifications.class);
                startActivity(i);
            }
        });
        getDoctorAllPatientsData();
        getDoctorData();
    }


    public void getDoctorAllPatientsData() {
        String url = ApplicationController.get_base_url() + "swasthgarbh/doc/" + session.getUserDetails().get("id");

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        int anc1Count, anc2Count, anc3Count, anc4Count, anc5Count, anc6Count, anc7Count, anc8Count;
                        anc1Count = anc2Count = anc3Count = anc4Count = anc5Count = anc6Count = anc7Count = anc8Count = 0;
                        Log.d("anc response", response.toString());
                        try {
                            Random rand = new Random();
                            JSONArray ja = new JSONArray();
                            if(response.length() == 0){
                                keepDummyData =1;
                                dummyData.setVisibility(View.VISIBLE);
                                Boolean value;
                                for (int i=0;i<30;i++){
                                    JSONObject jo = new JSONObject();
                                    value = rand.nextBoolean();
                                    jo.put("anc1_diabtese", value);
                                    jo.put("anc1_anemia", value);
                                    jo.put("anc1_hiv", value);
                                    jo.put("anc1_ultrasound", value);
                                    jo.put("anc1_anemia", value);
                                    jo.put("anc1_tetnus", value);
                                    jo.put("anc1_urine", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc2_diabtese", value);
                                    jo.put("anc2_anemia", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc3_urine", value);
                                    jo.put("anc3_anemia", value);
                                    jo.put("anc3_diabtese", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc4_diabtese", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc5_diabtese", value);
                                    jo.put("anc5_urine", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc6_diabtese", value);
                                    jo.put("anc6_anemia", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc7_diabtese", value);
                                    value = rand.nextBoolean();
                                    jo.put("anc8_diabtese", value);
                                    ja.put(jo);
                                }
//                                Log.i("aaaaaaa", "onResponse: " + ja);
                                response = ja;
                                Toast.makeText(DoctorScreen.this, "Dummy Data", Toast.LENGTH_LONG).show();
                            } else {
                                keepDummyData = 0;
                                dummyData.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject po = response.getJSONObject(i);
                                Log.i("data in array", "onResponse: " + po);
                                if (po.getBoolean("anc1_diabtese") == Boolean.TRUE && po.getBoolean("anc1_anemia") == Boolean.TRUE  && po.getBoolean("anc1_hiv") == Boolean.TRUE  && po.getBoolean("anc1_ultrasound") == Boolean.TRUE  && po.getBoolean("anc1_anemia") == Boolean.TRUE  && po.getBoolean("anc1_tetnus") == Boolean.TRUE  && po.getBoolean("anc1_urine") == Boolean.TRUE ) {
                                    anc1Count++;
                                }
                                if (po.getBoolean("anc2_diabtese") == Boolean.TRUE && po.getBoolean("anc2_anemia") == Boolean.TRUE) {
                                    anc2Count++;
                                }
                                if (po.getBoolean("anc3_urine") == Boolean.TRUE && po.getBoolean("anc3_anemia") == Boolean.TRUE && po.getBoolean("anc3_diabtese") == Boolean.TRUE) {
                                    anc3Count++;
                                }
                                if (po.getBoolean("anc4_diabtese")) {
                                    anc4Count++;
                                }
                                if (po.getBoolean("anc5_diabtese") == Boolean.TRUE && po.getBoolean("anc5_urine") == Boolean.TRUE) {
                                    anc5Count++;
                                }
                                if (po.getBoolean("anc6_diabtese") == Boolean.TRUE && po.getBoolean("anc6_anemia") == Boolean.TRUE) {
                                    anc6Count++;
                                }
                                if (po.getBoolean("anc7_diabtese") == Boolean.TRUE) {
                                    anc7Count++;
                                }
                                if (po.getBoolean("anc8_diabtese") == Boolean.TRUE) {
                                    anc8Count++;
                                }
                            }

                            int total_ladies = response.length();

                            BarChart barChart = (BarChart) findViewById(R.id.whoChart);

                            List<BarEntry> entriesGroup1 = new ArrayList<>();
                            List<BarEntry> entriesGroup2 = new ArrayList<>();

                            entriesGroup1.add(new BarEntry(1,anc1Count));
                            entriesGroup2.add(new BarEntry(1,total_ladies-anc1Count));
                            entriesGroup1.add(new BarEntry(2, anc2Count));
                            entriesGroup2.add(new BarEntry(2,total_ladies-anc2Count));
                            entriesGroup1.add(new BarEntry(3, anc3Count));
                            entriesGroup2.add(new BarEntry(3,total_ladies-anc3Count));
                            entriesGroup1.add(new BarEntry(4, anc4Count));
                            entriesGroup2.add(new BarEntry(4,total_ladies-anc4Count));
                            entriesGroup1.add(new BarEntry(5, anc5Count));
                            entriesGroup2.add(new BarEntry(5,total_ladies-anc5Count));
                            entriesGroup1.add(new BarEntry(6, anc6Count));
                            entriesGroup2.add(new BarEntry(6,total_ladies-anc6Count));
                            entriesGroup1.add(new BarEntry(7, anc7Count));
                            entriesGroup2.add(new BarEntry(7,total_ladies-anc7Count));
                            entriesGroup1.add(new BarEntry(8, anc8Count));
                            entriesGroup2.add(new BarEntry(8,total_ladies-anc8Count));

//                            List<String> xValues = "ANC1","ANC2","ANC3","ANC4","ANC5","ANC6","ANC7","ANC8";
//
//                            XAxis xAxis = barChart.getXAxis();
//                            xAxis.setValueFormatter(new MyValueFormatterPie(xValues));
                            BarDataSet set1 = new BarDataSet(entriesGroup1, "Following WHO Guidelines");
                            BarDataSet set2 = new BarDataSet(entriesGroup2, "Not Following WHO Guidelines");
                            set2.setColor(Color.RED);

                            float groupSpace = 0.15f;
                            float barSpace = 0.05f; // x2 dataset
                            float barWidth = 0.37f; // x2 dataset
//                          (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"

                            BarData data = new BarData(set1, set2);
                            data.setValueFormatter(new MyValueFormatterPie());

                            // Code to convert yaxis labels to integers
//                            barChart.getAxisLeft().setLabelCount(total_ladies  + 2, true);
//                            barChart.getAxisLeft().setAxisMinValue(0f);
//                            barChart.getAxisLeft().setAxisMaxValue(total_ladies  + 1);
//                            barChart.getAxisRight().setLabelCount(total_ladies  + 2, true);
//                            barChart.getAxisRight().setAxisMinValue(0f);
//                            barChart.getAxisRight().setAxisMaxValue(total_ladies  + 1);
                            barChart.getAxisLeft().setGranularity(1.0f);
                            barChart.getAxisLeft().setGranularityEnabled(true);
                            barChart.getAxisRight().setGranularity(1.0f);
                            barChart.getAxisRight().setGranularityEnabled(true);
//                            barChart.getAxisLeft().setDrawLabels(false);
//                            barChart.getAxisRight().setDrawLabels(false);

                            data.setBarWidth(barWidth); // set the width of each bar
                            barChart.setData(data);
                            barChart.groupBars(0.5f,groupSpace, barSpace); // perform the "explicit" grouping
                            barChart.setDragEnabled(true);
                            barChart.setScaleEnabled(true);
                            barChart.getDescription().setEnabled(false);
                            barChart.getAxisLeft().setDrawGridLines(false);
                            barChart.getXAxis().setDrawGridLines(false);
                            barChart.getAxisRight().setDrawGridLines(false);
                            barChart.invalidate();
                            barChart.animateXY(3000, 3000);
                            barPB.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    /*
     * to fill the doctor details
     * API for doctor details
     * */
    public void getDoctorData() {
        String url = ApplicationController.get_base_url() + "api/doctor/" + session.getUserDetails().get("id");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("apihit", response.toString());
                        try {
                            doctorName.setText(response.getString("name"));
                            doctorHospital.setText(response.getString("hospital"));
                            doctorEmail.setText(response.getString("email"));
//                            doctorEmail.setText("vatsala.dhadwal@gmail.com");
                            doctorTotalPatients.setText(String.valueOf(response.getJSONArray("patients").length()));
                            doctorSpeciality.setText(response.getString("speciality"));
                            if(!response.getBoolean("verified")){
                                verified.setVisibility(View.GONE);
                            }

                            JSONObject analysis_obj = (JSONObject) response.getJSONObject("analysis_object");
                            if(keepDummyData == 1){
//                                if(analysis_obj.getInt("high_sys") ==0 && analysis_obj.getInt("high_dys") ==0 && analysis_obj.getInt("high_weight") ==0 && analysis_obj.getInt("hyper_tension") ==0 && analysis_obj.getInt("urine_albumin") ==0 && analysis_obj.getInt("who_following") ==0){
                                    //dummy data for new doctor
                                    JSONObject jo = new JSONObject();
                                    Random rand = new Random();
                                    for (int i=0;i<30;i++){
                                        jo.put("high_sys", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                        jo.put("high_dys", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                        jo.put("high_weight", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                        jo.put("hyper_tension", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                        jo.put("urine_albumin", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                        jo.put("who_following", (int)(Math.random() * ((30 - 10) + 1)) + 10);
                                    }
                                    doctorTotalPatients.setText("30");
                                    Log.i("dataaaaaaa", "onResponse: " + jo);
                                    analysis_obj = jo;
                                    Toast.makeText(DoctorScreen.this, "Dummy Data", Toast.LENGTH_LONG).show();
//                                }
                            }
                            PieChart pieChart = (PieChart) findViewById(R.id.doc2chart);
                            ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
                            if(analysis_obj.getInt("high_sys")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("high_sys"), "High Systolic BP"));
                            if(analysis_obj.getInt("high_dys")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("high_dys"), "High Diastolic BP"));
                            if(analysis_obj.getInt("high_weight")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("high_weight"), "Under Weight"));
                            if(analysis_obj.getInt("hyper_tension")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("hyper_tension"), "Hypertensed"));
                            if(analysis_obj.getInt("urine_albumin")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("urine_albumin"), "High Urine Albumin"));
                            if(analysis_obj.getInt("who_following")!=0)
                                yvalues.add(new PieEntry(analysis_obj.getInt("who_following"), "Following WHO"));

                            PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.PieChartDesc));

                            PieData data = new PieData(dataSet);
                            data.setValueFormatter(new MyValueFormatterPie());
                            pieChart.setDrawHoleEnabled(true);
                            pieChart.getDescription().setEnabled(false);
                            data.setValueTextSize(13f);
                            data.setValueTextColor(Color.DKGRAY);
                            pieChart.setEntryLabelColor(Color.BLACK);
                            pieChart.getLegend().setWordWrapEnabled(true);

//                            dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                            dataSet.setColors(new int[] { getResources().getColor(R.color.chart1), getResources().getColor(R.color.chart2), getResources().getColor(R.color.chart3), getResources().getColor(R.color.chart4), getResources().getColor(R.color.chart5), getResources().getColor(R.color.chart6) });
                            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//                            data.setValueFormatter(new PercentFormatter());
                            pieChart.setData(data);
                            pieChart.invalidate();
                            pieChart.animateY(1500, Easing.EasingOption.EaseInOutCubic);
//                            pieChart.spin( 1000,0,-360f, Easing.EasingOption.EaseInOutQuad);
                            piePB.setVisibility(View.GONE);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Token " + session.getUserDetails().get("Token"));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
