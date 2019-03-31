package com.swasthgarbh.root.swasthgarbh;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MedicineReminder extends AppCompatActivity {

    ListView patient_list_view;
    patientDataAdapter adapter;
    static SessionManager session;
    Dialog add_medicine_dialog;
    Button addMedButton;
    EditText medName, medStart, medEnd, medComments;
    int clickedPatientId;
    RadioGroup radioGroupFReq;
    RadioButton radioGroupFReqDaily, radioGroupFReqWeekly, radioGroupFReqMonthly;
    FloatingActionButton fab;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar listPB,addMedPB;
    Switch sosSwitch;
    Boolean isSOS = Boolean.FALSE;
    LinearLayout periodLayout, dateLayout;
    ImageView deleteMedicine;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatterShow, dateFormatterServer;
    String medStartTime, medEndTime;

    Calendar newDate1 = Calendar.getInstance();
    Calendar newDate2 = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        dateFormatterShow = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatterServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:dd'Z'");
        listPB = (ProgressBar) findViewById(R.id.listPB);
        session = new SessionManager(this);
        clickedPatientId = getIntent().getIntExtra("EXTRA_PATIENT_ID", 0);
//        Log.i("iiiidddd", "onCreate: " + clickedPatientId);
        getPatientData(clickedPatientId);

        fab = findViewById(R.id.fab);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshMedicine);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMedicineDialog();
            }
        });
//        final HashMap<String, String> user = session.getUserDetails();
//        if ("doctor".equals(user.get("type"))) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showAddMedicineDialog();
//                }
//            });
//        } else if ("patient".equals(user.get("type"))) {
//            fab.hide();
//        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPatientData(clickedPatientId);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void showAddMedicineDialog() {
        add_medicine_dialog = new Dialog(this);
        add_medicine_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add_medicine_dialog.setContentView(R.layout.medicine_add_screen);
        add_medicine_dialog.setCancelable(true);
        add_medicine_dialog.show();

        deleteMedicine = (ImageView)add_medicine_dialog.findViewById(R.id.deleteMedicine);
        deleteMedicine.setVisibility(View.GONE);

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
        layoutParams.copyFrom(add_medicine_dialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 0.85f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.70f);
        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        // Apply the newly created layout parameters to the alert dialog window
        add_medicine_dialog.getWindow().setAttributes(layoutParams);


        addMedPB = (ProgressBar) add_medicine_dialog.findViewById(R.id.addMedPB);
        addMedPB.setVisibility(View.GONE);
        addMedButton = add_medicine_dialog.findViewById(R.id.addMedButton);
        medName = add_medicine_dialog.findViewById(R.id.medName);
        sosSwitch = add_medicine_dialog.findViewById(R.id.sosSwitch);
        periodLayout = add_medicine_dialog.findViewById(R.id.periodLayout);
        dateLayout = add_medicine_dialog.findViewById(R.id.dateLayout);

        sosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    periodLayout.setVisibility(View.GONE);
                    dateLayout.setVisibility(View.GONE);
                    isSOS = Boolean.TRUE;
                }else{
                    periodLayout.setVisibility(View.VISIBLE);
                    dateLayout.setVisibility(View.VISIBLE);
                    isSOS = Boolean.FALSE;
                }
            }
        });

        medStart = add_medicine_dialog.findViewById(R.id.medStart);
        medStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate1.set(year, monthOfYear, dayOfMonth);
                medStart.setText(dateFormatterShow.format(newDate1.getTime()));
                medStartTime = dateFormatterServer.format(newDate1.getTime());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        medEnd = add_medicine_dialog.findViewById(R.id.medEnd);
        medEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate2.set(year, monthOfYear, dayOfMonth);
                medEnd.setText(dateFormatterShow.format(newDate2.getTime()));
                medEndTime = dateFormatterServer.format(newDate2.getTime());
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        medComments = add_medicine_dialog.findViewById(R.id.medComments);
        radioGroupFReq = add_medicine_dialog.findViewById(R.id.radioGroupFreq);

        radioGroupFReqDaily = add_medicine_dialog.findViewById(R.id.daily);
        radioGroupFReqWeekly = add_medicine_dialog.findViewById(R.id.weekly);
        radioGroupFReqMonthly = add_medicine_dialog.findViewById(R.id.monthly);

        addMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedPB.setVisibility(View.VISIBLE);
                addMedButton.setVisibility(View.GONE);
                String url = ApplicationController.get_base_url() + "swasthgarbh/patient/" + clickedPatientId + "/med";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("reeeesponse", "onResponse: " + response);
                                add_medicine_dialog.dismiss();
                                getPatientData(clickedPatientId);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Message: " + error.getMessage());
                        addMedPB.setVisibility(View.GONE);
                        addMedButton.setVisibility(View.VISIBLE);
                    }
                }) {

                    @Override
                    public byte[] getBody() {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("patient_id", clickedPatientId);
                            params.put("medicine_name", medName.getText());

                            String freq = "daily";
                            if (radioGroupFReqDaily.isChecked()) {
                                freq = "daily";
                            } else if (radioGroupFReqMonthly.isChecked()) {
                                freq = "monthly";
                            } else if (radioGroupFReqWeekly.isChecked()) {
                                freq = "weekly";
                            }

                            params.put("medicine_extra_comments", medComments.getText());
                            params.put("medicine_Image", "Sample image byte for Medicine");
                            params.put("medicine_start", medStartTime);
                            params.put("medicine_end", medEndTime);
                            if(isSOS){
                                params.put("isSOS", isSOS);
                            }else{
                                params.put("medicine_freq", freq);
                                params.put("isSOS", isSOS);
                            }

                            Log.i("Boddddyyyyy", "getBody: " + params.toString());
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


    public void getPatientData(int id) {
        listPB.setVisibility(View.VISIBLE);
        String url = ApplicationController.get_base_url() + "swasthgarbh/patient/" + id;
        final ArrayList<MedicineListClass> medicineRowAdapter = new ArrayList<MedicineListClass>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("apihit1", response.toString());
                        try {
                            JSONArray medicineData = response.getJSONArray("medicines");
                            if(medicineData.length()==0){
                                Toast.makeText(MedicineReminder.this, "No Medicines Added", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < medicineData.length(); i++) {
                                JSONObject po = (JSONObject) medicineData.get(i);
                                MedicineListClass pr = new MedicineListClass(po.getInt("pk"), po.getString("medicine_name"), po.getString("medicine_start"), po.getString("medicine_end"), po.getString("medicine_freq"), po.getString("medicine_extra_comments"), po.getBoolean("isSOS"));
                                medicineRowAdapter.add(pr);
                                Log.i("Data in array", "" + String.valueOf(medicineData.get(i)));
                            }
                            MedicineAdapter itemsAdapter = new MedicineAdapter(MedicineReminder.this, medicineRowAdapter);
                            ListView listView = (ListView) findViewById(R.id.patient_medicine_list);
                            listView.setAdapter(itemsAdapter);
                            listPB.setVisibility(View.GONE);
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
    }
}
