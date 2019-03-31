package com.swasthgarbh.root.swasthgarbh;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class patient_registration extends AppCompatActivity {

    private Button whoGuideLines, logOutButton, done, name_of_medicine, extra_comments;
    EditText doc_number;
    TextView doc_name, docHospital, docSpeciality;
    static SessionManager session;
    ListView patient_list_view;
    patientDataAdapter adapter;
    private int doctorId;
    TextView doctorName, patientName, pregStartDate, doctorMobile, whoFollowing, dummyData;
    LinearLayout TextWhenNoData, parentView, chartLayout;
    ImageView callDoctor, verified;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Dialog choose_doc;
    int doc_id;
    ImageView ivImage;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String ImgBytes;
    Uri ImageUri;
    ImageView loader;
    boolean doubleBackToExitPressedOnce = false;
    // Create a Uri from an intent string. Use the result to create an Intent.
    private void logout(Context _c) {
        session = new SessionManager(_c);
        session.logoutUser();
        Intent i = new Intent(patient_registration.this, ControllerActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        if (item.getItemId() == R.id.action_logout){
            logout(this);
            return true;
        } else if (item.getItemId() == R.id.action_change_doctor){
            change_doctor();
        } else if (item.getItemId() == R.id.action_notification){
            i = new Intent(this, PatientNotifications.class);
            startActivity(i);
        }  else if (item.getItemId() == R.id.patientImages){
            Intent intent = new Intent(this, all_images_view.class);
            intent.putExtra("EXTRA_PATIENT_ID", Integer.parseInt(session.getUserDetails().get("id")));
            startActivity(intent);
        } else if (item.getItemId() == R.id.hospitalsNearYou){
//            String url = "https://www.google.co.in/search?q=hospitals+near+you&oq=hospitals+near+you&aqs=chrome..69i57.4099j0j9&client=ms-android-samsung&sourceid=chrome-mobile&ie=UTF-8#istate=lrl:xpd";
//            Intent webIntent = new Intent(Intent.ACTION_VIEW);
//            webIntent.setData(Uri.parse(url));
//            startActivity(webIntent);

//            i = new Intent(this, NearbyHospitals.class);
//            startActivity(i);
            //classes used : Dataparser, Nearby Hospitals, DownloadURL, GetNearbyPlacesData

            // Search for hospitals nearby //https://developers.google.com/maps/documentation/urls/android-intents
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=nearbyhospitals");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else if (item.getItemId() == R.id.aboutPre){
            i = new Intent(this, AboutPreeclampsia.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    private void change_doctor() {
        choose_doc = new Dialog(this);
        choose_doc.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choose_doc.setContentView(R.layout.choose_doc_dialog);
        choose_doc.setCancelable(true);
        done = choose_doc.findViewById(R.id.changeDocButton);

        doc_number = choose_doc.findViewById(R.id.enteredNumber);
        doc_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 10) {
                    String url = ApplicationController.get_base_url() + "api/doctor?mobile=" + editable.toString();
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("TAG", response.toString());

                                    try {
                                        doc_name.setText(response.get("name") + "");
                                        docHospital.setText(response.get("hospital") + "");
                                        docSpeciality.setText(response.get("speciality") + "");
                                        doc_id = (int) response.get("pk");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        doc_name.setText("");
                                        Toast.makeText(patient_registration.this, "No doctor with this mobile number is registered", Toast.LENGTH_SHORT).show();

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
                            params.put("Authorization", "Token " + session.getUserDetails().get("Token"));
                            Log.d("TAG", "Token " + session.getUserDetails().get("Token"));
//                params.put("Authorization", "Token f00a64734d608991730ccba944776c316c38c544");
                            return params;
                        }

                    };
                    ApplicationController.getInstance().addToRequestQueue(jsonObjReq);
                }
            }
        });

        doc_name = choose_doc.findViewById(R.id.docName);
        docHospital = choose_doc.findViewById(R.id.docHospital);
        docSpeciality = choose_doc.findViewById(R.id.docSpeciality);

        choose_doc.show();

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(choose_doc.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.65f);
        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        // Apply the newly created layout parameters to the alert dialog window
        choose_doc.getWindow().setAttributes(layoutParams);


        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("changeee", "onClick: inside change doc");
//                if (v.getId() == R.id.login) {

                    String url = ApplicationController.get_base_url() + "api/patient/" + session.getUserDetails().get("id");
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                            url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("change doc api resp", response.toString());
                                    doctorId = doc_id;
                                    choose_doc.dismiss();
                                    Toast.makeText(patient_registration.this, "Doctor changed", Toast.LENGTH_LONG).show();
                                    getPatientData();
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
                                params.put("d_id", doc_id);
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
        });
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
        setContentView(R.layout.activity_patient_registration);

        doctorName = (TextView) findViewById(R.id.doctorName);
        doctorMobile = (TextView) findViewById(R.id.doctorMobile);
        patientName = (TextView) findViewById(R.id.patientName);
        pregStartDate = (TextView) findViewById(R.id.pregStartDate);
        whoFollowing = (TextView) findViewById(R.id.whoFollowing);
        callDoctor = (ImageView)  findViewById(R.id.callDoctor);
        dummyData = (TextView)  findViewById(R.id.dummyData);
        verified = (ImageView)findViewById(R.id.verified);
        dummyData.setVisibility(View.GONE);

        Button whoGuidelines = (Button) findViewById(R.id.who_button);
        whoGuidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_registration.this, WHOGuidelines.class);
                intent.putExtra("EXTRA_PATIENT_ID", Integer.parseInt(session.getUserDetails().get("id")));
                startActivity(intent);
            }
        });

        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_registration.this, patientDataEntry.class);
                startActivity(intent);
            }
        });

        Button medicineList = (Button) findViewById(R.id.medicineList);
        medicineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_registration.this, MedicineReminder.class);
                intent.putExtra("EXTRA_PATIENT_ID", Integer.parseInt(session.getUserDetails().get("id")));
                startActivity(intent);
            }
        });

        callDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_no= doctorMobile.getText().toString().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        getSupportActionBar().setTitle("Patient");

        session = new SessionManager(this);

        getPatientData();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshPatientData);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPatientData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /*
    * to fill the doctor details
    * API for doctor details
    * */
    public void getDoctorData(){
        if(doctorId != 0){
            String url = ApplicationController.get_base_url() + "api/doctor/" + doctorId;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("doctor details", response.toString());
                            try {
                                doctorName.setText(response.getString("name"));
                                Log.i("mobilee", "onResponse: " + response.getLong("mobile"));
                                doctorMobile.setText(Long.toString(response.getLong("mobile")));
                                if(!response.getBoolean("verified")){
                                    verified.setVisibility(View.GONE);
                                }else{
                                    verified.setVisibility(View.VISIBLE);
                                }
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
        } else {
            doctorName.setText("No Doctor");
            doctorMobile.setText("- - - -");
            verified.setVisibility(View.GONE);
        }
    }

    /*
     * to fill the patient details
     * API for doctor details
     * */
    public void getPatientData(){
        Log.i("reload", "getPatientData: called");
        String url = ApplicationController.get_base_url() + "api/patient/" + session.getUserDetails().get("id");
        final ProgressBar pb = (ProgressBar) findViewById(R.id.indeterminateBar);
        final ProgressBar chartPB = (ProgressBar) findViewById(R.id.chartPB);
        final ArrayList<patient_data_listview_class> patientRowData = new ArrayList<patient_data_listview_class>();
        pb.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("apihit", response.toString());
                        try {
                            if(response.getString("doctor") == "null"){
                                doctorId = 0;
                            }else{
                                doctorId = response.getInt("doctor");
                            }
                            patientName.setText(response.getString("name"));
                            whoFollowing.setText(response.getString("who_following"));
                            String date = response.getString("lmp").split("T")[0].split("-")[2] + "-" + response.getString("lmp").split("T")[0].split("-")[1] + "-" + response.getString("lmp").split("T")[0].split("-")[0];
                            pregStartDate.setText(date);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMMM-yyyy");
                            Date d = sdf.parse(date);
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.DAY_OF_MONTH, 282);
                            pregStartDate.setText(sdf2.format(c.getTime()));
//                            Log.i("whoooooo", "onResponse: " + response.getString("who_following"));
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

                                    jo.put("time_stamp", "2018-" + String.valueOf((int)(Math.random() * 12 + 1)) + "-" + String.valueOf((int)(Math.random() * 30 + 1)) + "T01:25:37.199340+05:30");
                                    jo.put("pk", session.getUserDetails().get("id"));
                                    ja.put(jo);
                                }
                                Log.i("dataaaaaaa", "onResponse: " + ja);
                                patientBpData = ja;
                                Toast.makeText(patient_registration.this, "Dummy Data", Toast.LENGTH_LONG).show();
                                dummyData.setVisibility(View.VISIBLE);
                                dummyDataVariable = 1;
                            }
                            if(patientBpData.length()!=0){
//                                parentView.removeView(TextWhenNoData);
                                /******************************
                                 * To set the charts
                                 */
                                LineChart chart = (LineChart) findViewById(R.id.chart);

                                ArrayList<Entry> yValues = new ArrayList<Entry>();
                                ArrayList<Integer> colorssys = new ArrayList<Integer>();
                                ArrayList<Entry> y2Values = new ArrayList<Entry>();
                                ArrayList<Integer> colorsdys = new ArrayList<Integer>();
                                ArrayList<Entry> y3Values = new ArrayList<Entry>();

                                for (int i = 0; i < patientBpData.length(); i++) {
                                    JSONObject po = (JSONObject) patientBpData.get(i);
                                    patient_data_listview_class pr = new patient_data_listview_class(dummyDataVariable, patientBpData.length(), po.getInt("pk"), po.getString("time_stamp"),po.getInt("systolic"), po.getInt("diastolic"), po.getDouble("urine_albumin"), po.getInt("weight"), po.getDouble("bleeding_per_vaginum"));
                                    patientRowData.add(pr);
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
//                                    Log.i("lennnnnnnn", "" + sys);
//                                    if(po.getInt("systolic") != 0 || len == 1){
                                        yValues.add(new Entry(patientBpData.length()-i, sys));
//                                    }

                                    if(po.getInt("systolic") >160){
                                        colorssys.add(ContextCompat.getColor(patient_registration.this, R.color.chart6)) ;
                                    } else if (po.getInt("systolic") > 140 && po.getInt("systolic") <= 160){
                                        colorssys.add(ContextCompat.getColor(patient_registration.this, R.color.chart4)) ;
                                    } else if (po.getInt("systolic") <= 145){
                                        colorssys.add(ContextCompat.getColor(patient_registration.this, R.color.chartsys)) ;
                                    }

                                    if(po.getInt("diastolic") > 110){
                                        colorsdys.add(ContextCompat.getColor(patient_registration.this, R.color.chart6)) ;
                                    } else if (po.getInt("diastolic") > 90 && po.getInt("diastolic") <= 110){
                                        colorsdys.add(ContextCompat.getColor(patient_registration.this, R.color.chart4)) ;
                                    } else if (po.getInt("diastolic") <= 90 || len == 1) {
                                        colorsdys.add(ContextCompat.getColor(patient_registration.this, R.color.chartdys)) ;
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

                                set1.setFillAlpha(110);
                                set1.setLineWidth(3.5f);
                                set1.setColor(Color.rgb(19, 141, 117));
                                set1.setDrawValues(false);
//                                set1.setDrawCircles(false);
                                set1.setDrawValues(false);
                                set1.setCircleColors(colorssys);

                                set2.setLineWidth(2f);
                                set2.setColor(Color.rgb(171, 235, 198));
                                set2.setDrawValues(false);
//                                set2.setDrawCircles(false);
                                set2.setCircleColors(colorsdys);
                                set2.setDrawValues(false);

                                set3.setColor(Color.rgb(93, 173, 226));
                                set3.setLineWidth(2f);
                                set3.setDrawValues(false);
                                set3.setDrawCircles(false);
                                set3.setDrawValues(false);

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

                                patientDataAdapter itemsAdapter = new patientDataAdapter(patient_registration.this, patientRowData);
                                ListView listView = (ListView) findViewById(R.id.patient_data_list_view);
                                listView.setAdapter(itemsAdapter);
                                pb.setVisibility(View.GONE);
                                chartPB.setVisibility(View.GONE);
                            }
                            getDoctorData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
}
