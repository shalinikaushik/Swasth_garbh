package com.swasthgarbh.root.swasthgarbh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllPatientListInDoctorAdapter extends ArrayAdapter<PatientListRowInDoctorClass> {

    public AllPatientListInDoctorAdapter(Activity context, ArrayList<PatientListRowInDoctorClass> patientData) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, patientData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PatientListRowInDoctorClass current_patient_data = getItem(position);

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_patient_list_row, parent, false);
        }

        TextView patientName = (TextView)listItemView.findViewById(R.id.PatientNameInList);
        patientName.setText(current_patient_data.getName());

        TextView eddDateOfPatient = (TextView)listItemView.findViewById(R.id.eddDateOfPatientInList);
        try {
            eddDateOfPatient.setText("EDD : " + current_patient_data.getEDD());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final int patientId = current_patient_data.getPatientId();

        getPatientGraph(patientId, current_patient_data, listItemView);

        ImageView verified;
        verified = (ImageView)listItemView.findViewById(R.id.verified);
        if(!current_patient_data.getVerified()){
            verified.setVisibility(View.GONE);
        }

        LinearLayout getInsideParticularPatient = (LinearLayout) listItemView.findViewById(R.id.getInsideParticularPatient);
        getInsideParticularPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DoctorScreenForParticularPatient.class);
                intent.putExtra("EXTRA_PATIENT_ID", patientId);
                v.getContext().startActivity(intent);
            }
        });

        return listItemView;
    }

    public void getPatientGraph(int id, final PatientListRowInDoctorClass current_patient_data, final View listItemView){
        String url = ApplicationController.get_base_url() + "api/patient/" + id;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("getGraphDATA", response.toString());
                        try {

                            JSONArray patientBpData = response.getJSONArray("data");

                            LineChart chart = (LineChart) listItemView.findViewById(R.id.patientBpChartInListItem);

                            ArrayList<Entry> yValues = new ArrayList<Entry>();
                            ArrayList<Integer> colorssys = new ArrayList<Integer>();
                            ArrayList<Entry> y2Values = new ArrayList<Entry>();
                            ArrayList<Integer> colorsdys = new ArrayList<Integer>();

                            Log.d("The length check", "onResponse: " + patientBpData.length());
                            if(patientBpData.length() != 0){
//                                for (int i = 0; i < patientBpData.length(); i++) {
//                                    JSONObject po = (JSONObject) patientBpData.get(i);
//                                    yValues.add(new Entry(i, po.getInt("systolic")));
//                                    y2Values.add(new Entry(i, po.getInt("diastolic")));
//                                }

                                for (int i = patientBpData.length()-1; i>=0; i--) {
                                    JSONObject po = (JSONObject) patientBpData.get(i);
                                    int len = patientBpData.length();
//                                    if(po.getInt("systolic") != 0 || len == 1){
                                        yValues.add(new Entry(patientBpData.length()-i, po.getInt("systolic")));
//                                    }

                                    if(po.getInt("systolic") > 160){
                                        colorssys.add(ContextCompat.getColor(getContext(), R.color.chart6)) ;
                                    } else if (po.getInt("systolic") > 140 && po.getInt("systolic") <= 160){
                                        colorssys.add(ContextCompat.getColor(getContext(), R.color.chart4)) ;
                                    } else if (po.getInt("systolic") <= 145 ){
                                        colorssys.add(ContextCompat.getColor(getContext(), R.color.chartsys)) ;
                                    }

                                    if(po.getInt("diastolic") > 110){
                                        colorsdys.add(ContextCompat.getColor(getContext(), R.color.chart6)) ;
                                    } else if (po.getInt("diastolic") > 90 && po.getInt("diastolic") <= 110){
                                        colorsdys.add(ContextCompat.getColor(getContext(), R.color.chart4)) ;
                                    } else if (po.getInt("diastolic") <= 90 || len == 1) {
                                        colorsdys.add(ContextCompat.getColor(getContext(), R.color.chartdys)) ;
                                    }

//                                    if(po.getInt("diastolic") != 0 || len == 1){
                                        y2Values.add(new Entry(patientBpData.length()-i, po.getInt("diastolic")));
//                                    }
                                }

                                chart.setDragEnabled(true);
                                chart.setScaleEnabled(true);
                                chart.getDescription().setEnabled(false);

                                LineDataSet set1 = new LineDataSet(yValues, "Systolic BP");
                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                                LineDataSet set2 = new LineDataSet(y2Values, "Diastolic BP");
                                set2.setAxisDependency(YAxis.AxisDependency.LEFT);

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

                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1);
                                dataSets.add(set2);

                                LineData data = new LineData(dataSets);
                                chart.setData(data);
                                chart.invalidate();
//                                chart.animateXY(1000, 1000);
                            }
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
                params.put("Authorization", "Token " + DoctorScreen.session.getUserDetails().get("Token"));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
