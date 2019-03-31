package com.swasthgarbh.root.swasthgarbh;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class patientDetailInDoctor extends AppCompatActivity {

    static SessionManager session;
    private int clickedPatientId;
    CheckBox highbp, histPree, motherPre, histObes, moreThanOneBaby, diseases;
    Switch verifyUser, preeclampsia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail_in_doctor);

        session = new SessionManager(this);
        clickedPatientId= getIntent().getIntExtra("EXTRA_PATIENT_ID", 1);
        getPatientData(clickedPatientId);

        highbp = (CheckBox)findViewById(R.id.highbp);
        histPree = (CheckBox)findViewById(R.id.histPree);
        motherPre = (CheckBox)findViewById(R.id.motherPre);
        histObes = (CheckBox)findViewById(R.id.histObes);
        moreThanOneBaby = (CheckBox)findViewById(R.id.moreThanOneBaby);
        diseases = (CheckBox)findViewById(R.id.diseases);

        verifyUser = (Switch)findViewById(R.id.verifyUser);
        preeclampsia = (Switch)findViewById(R.id.preeclampsia);

        verifyUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
//                if(isChecked){
//
//                }else{
//
//                }
                updatePatientData();
            }
        });

        preeclampsia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
//                if(isChecked){
//
//                }else{
//
//                }
                updateOtherPatientData();
            }
        });
    }

    public void getPatientData(int pId){
        String url = ApplicationController.get_base_url() + "api/patient/" + String.valueOf(clickedPatientId);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("rrrrrrrrrrr", "onResponse: " + response.getBoolean("verified") + clickedPatientId);

                            highbp.setChecked(response.getBoolean("history_high_blood_pressure"));
                            histPree.setChecked(response.getBoolean("history_of_preeclampsia"));
                            motherPre.setChecked(response.getBoolean("mother_or_sister_had_preeclampsia"));
                            histObes.setChecked(response.getBoolean("history_of_obesity"));
                            moreThanOneBaby.setChecked(response.getBoolean("more_than_one_baby"));
                            diseases.setChecked(response.getBoolean("history_of_diseases"));

                            verifyUser.setChecked(response.getBoolean("verified"));
                            getOtherPregData();
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

    public void updatePatientData(){

        String url = ApplicationController.get_base_url() + "api/patient/" + String.valueOf(clickedPatientId);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(patientDetailInDoctor.this, "Details Updated", Toast.LENGTH_LONG).show();
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
                    params.put("verified", verifyUser.isChecked());
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

    public void getOtherPregData(){

        String url = ApplicationController.get_base_url() + "api/resultsByDoc/" + String.valueOf(clickedPatientId);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            preeclampsia.setChecked(response.getBoolean("preeclampsia"));
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

    public void updateOtherPatientData(){

        String url = ApplicationController.get_base_url() + "api/resultsByDoc/" + String.valueOf(clickedPatientId);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(patientDetailInDoctor.this, "Details Updated", Toast.LENGTH_LONG).show();
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
                    params.put("preeclampsia", preeclampsia.isChecked());
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
