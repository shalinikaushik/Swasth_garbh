package com.swasthgarbh.root.swasthgarbh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.json.JSONException;
import org.json.JSONObject;


public class MedicineAdapter extends ArrayAdapter<MedicineListClass> {

    static SessionManager session;
    Dialog add_medicine_dialog;
    Button addMedButton;
    EditText medName, medStart, medEnd, medComments;
    int medId;
    RadioGroup radioGroupFReq;
    RadioButton radioGroupFReqDaily, radioGroupFReqWeekly, radioGroupFReqMonthly;
    FloatingActionButton fab;
    int clickedPatientId;
    ProgressBar listPB,addMedPB;
    Switch sosSwitch;
    Boolean isSOS = Boolean.FALSE;
    LinearLayout periodLayout;
    ImageView deleteMedicine;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatterShow, dateFormatterServer;
    String medStartTime, medEndTime;

    Calendar newDate1 = Calendar.getInstance();
    Calendar newDate2 = Calendar.getInstance();

    public MedicineAdapter(Activity context, ArrayList<MedicineListClass> patientData) {
        super(context, 0, patientData);
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

    public void openMedicineModal(final View v){
        final View MedReminderView= v;
        add_medicine_dialog = new Dialog(v.getContext());
        add_medicine_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add_medicine_dialog.setContentView(R.layout.medicine_add_screen);
        add_medicine_dialog.setCancelable(true);
        add_medicine_dialog.show();
        deleteMedicine = (ImageView)add_medicine_dialog.findViewById(R.id.deleteMedicine);
        deleteMedicine.setVisibility(View.VISIBLE);
        addMedPB = (ProgressBar) add_medicine_dialog.findViewById(R.id.addMedPB);
        addMedPB.setVisibility(View.VISIBLE);
        addMedButton = add_medicine_dialog.findViewById(R.id.addMedButton);
        addMedButton.setText("Update");
        addMedButton.setVisibility(View.GONE);
        medName = add_medicine_dialog.findViewById(R.id.medName);
        sosSwitch = add_medicine_dialog.findViewById(R.id.sosSwitch);
        periodLayout = add_medicine_dialog.findViewById(R.id.periodLayout);

        dateFormatterShow = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatterServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:dd'Z'");

        sosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    periodLayout.setVisibility(View.GONE);
                    isSOS = Boolean.TRUE;
                }else{
                    periodLayout.setVisibility(View.VISIBLE);
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
        fromDatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

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
        toDatePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

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
            public void onClick(final View view) {
                addMedPB.setVisibility(View.VISIBLE);
                addMedButton.setVisibility(View.GONE);

                String url = ApplicationController.get_base_url() + "swasthgarbh/med/" + medId;
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PATCH,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("reeeesponse", "onResponse: " + response);
                                Toast.makeText(v.getContext(), "Medicine updated successfully", Toast.LENGTH_SHORT).show();
                                add_medicine_dialog.dismiss();
                                ((MedicineReminder)MedReminderView.getContext()).getPatientData(clickedPatientId);
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

        deleteMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(v.getContext());
                }
                builder.setTitle("Delete Medicine")
                        .setMessage("Are you sure you want to delete this medicine?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                String url = ApplicationController.get_base_url() + "swasthgarbh/med/" + medId;
                                StringRequest jsonObjReq = new StringRequest(Request.Method.DELETE,
                                        url,
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String   response) {
                                                Log.i("Delete", "onResponse: Data deleted");
                                                Toast.makeText(v.getContext(), "Medicine deleted successfully", Toast.LENGTH_SHORT).show();
                                                add_medicine_dialog.dismiss();
                                                ((MedicineReminder)MedReminderView.getContext()).getPatientData(clickedPatientId);
//                                                    Intent intent = new Intent(v.getContext(), patient_registration.class);
//                                                    v.getContext().startActivity(intent);
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
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        String url = ApplicationController.get_base_url() + "swasthgarbh/med/" + medId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("reeeesponse", "onResponse: " + response);
                            addMedPB.setVisibility(View.GONE);
                            addMedButton.setVisibility(View.VISIBLE);
                            medName.setText(response.getString("medicine_name"));
                            medComments.setText(response.getString("medicine_extra_comments"));
                            sosSwitch.setChecked(response.getBoolean("isSOS"));
                            medStart.setText(get_date_date(response.getString("medicine_start")) + "-" + get_date_month(response.getString("medicine_start")) + "-" + get_date_year(response.getString("medicine_start")));
                            medEnd.setText(get_date_date(response.getString("medicine_end")) + "-" + get_date_month(response.getString("medicine_end")) + "-" + get_date_year(response.getString("medicine_end")));
                            clickedPatientId = response.getInt("patient_id");
                            if(response.getString("medicine_freq").toLowerCase().equals("daily")){
                                radioGroupFReqDaily.setChecked(Boolean.TRUE);
                            } else if(response.getString("medicine_freq").toLowerCase().equals("weekly")){
                                radioGroupFReqWeekly.setChecked(Boolean.TRUE);
                            }else if(response.getString("medicine_freq").toLowerCase().equals("monthly")){
                                radioGroupFReqMonthly.setChecked(Boolean.TRUE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token " + session.getUserDetails().get("Token"));
                Log.d("TAG", "Token " + session.getUserDetails().get("Token"));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MedicineListClass current_medicine_data = getItem(position);

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.medicine_list_row, parent, false);
        }

        final LinearLayout medicineBlock = (LinearLayout)listItemView.findViewById(R.id.medicineBlock);
        medicineBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Press long to edit/delete", Toast.LENGTH_SHORT).show();
            }
        });

        medicineBlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("hhhhhhhhh", "onClick: " + current_medicine_data.getMedId());
                medId = current_medicine_data.getMedId();
                openMedicineModal(v);
                return true;
            }
        });



        final TextView medName = (TextView)listItemView.findViewById(R.id.medName);
        medName.setText(current_medicine_data.getMedName());

        final TextView medFreq = (TextView)listItemView.findViewById(R.id.medFreq);

        final TextView startDateMed = (TextView)listItemView.findViewById(R.id.startDateMed);
        startDateMed.setText(current_medicine_data.getStartDate_date() + "-" + current_medicine_data.getStartDate_month() + "-" + current_medicine_data.getStartDate_year());

        final TextView endDateMed = (TextView)listItemView.findViewById(R.id.endDateMed);
        endDateMed.setText(current_medicine_data.getEndDate_date() + "-" + current_medicine_data.getEndDate_month() + "-" + current_medicine_data.getEndDate_year());

        final TextView extraComments = (TextView)listItemView.findViewById(R.id.medicineDetail);
        extraComments.setText(current_medicine_data.getComments());

        final TextView medFreqText = (TextView)listItemView.findViewById(R.id.medFreqText);

        ImageView reminder = (ImageView)listItemView.findViewById(R.id.reminderButton);

        if(current_medicine_data.getSOS() == Boolean.TRUE){
            reminder.setVisibility(View.INVISIBLE);
//            medFreqText.setText("SOS");
//            medFreqText.setTextColor(Color.parseColor("#C70039"));
//            medFreq.setText("");
            startDateMed.setTextColor(Color.parseColor("#C70039"));
            startDateMed.setText("SOS");
            endDateMed.setText("-");
        } else {
            reminder.setVisibility(View.VISIBLE);
            medFreqText.setText("Period");
            medFreqText.setTextColor(Color.parseColor("#808080"));
            medFreq.setText(current_medicine_data.getfreq());
        }

        session = new SessionManager(getContext());
        final HashMap<String, String> user = session.getUserDetails();
        if ("patient".equals(user.get("type"))){
            reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add calender events
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                    calIntent.setType("vnd.android.cursor.item/event");

                    dateFormatterServer = new SimpleDateFormat("yyyy-MM-dd");
                    Date start_date = current_medicine_data.getStartDate();

                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                    long millis = start_date.getTime();
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            millis);
                    Date end_date = current_medicine_data.getEndDate();

                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                    millis = end_date.getTime();
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            millis);

                    calIntent.putExtra("allDay", false);
                    calIntent.putExtra("rrule", "FREQ=" + current_medicine_data.getfreq());

                    calIntent.putExtra("title", "Reminder for " + current_medicine_data.getMedName());
                    calIntent.putExtra("description", current_medicine_data.getComments());
                    getContext().startActivity(calIntent);
                }
            });
        } else if ("doctor".equals(user.get("type"))){
            reminder.setVisibility(View.INVISIBLE);
        }
        return listItemView;
    }
}
