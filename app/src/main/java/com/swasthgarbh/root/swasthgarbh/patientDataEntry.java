package com.swasthgarbh.root.swasthgarbh;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.swasthgarbh.root.swasthgarbh.ApplicationController;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class patientDataEntry extends AppCompatActivity {

    private NotificationManager mNotificationManager;
    EditText sysData, dysData, bodyWeight, extraComments, heartRate, bleedingVag, urineAlb;
    CheckBox headache, abdPain, visProb, decFea, swell, hyperT;
    SessionManager session;
    Button sendData, add_pic;
    ImageView ivImage;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String ImgBytes = "";
    ImageView loader;
    String currentDateandTime;

    Uri ImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data_entry);
        getSupportActionBar().setTitle("Add data");
        sendData = (Button) findViewById(R.id.sendData);
        session = new SessionManager(this);

        session = new SessionManager(this);
        sysData = (EditText) findViewById(R.id.sysData);
        dysData = (EditText) findViewById(R.id.dysData);
        heartRate = (EditText) findViewById(R.id.heartRate);
        headache = (CheckBox) findViewById(R.id.headache);
        abdPain = (CheckBox) findViewById(R.id.abdPain);
        visProb = (CheckBox) findViewById(R.id.visProb);
        decFea = (CheckBox) findViewById(R.id.decFea);
        swell = (CheckBox) findViewById(R.id.swell);
//        hyperT = (CheckBox) findViewById(R.id.hyperT);
        bodyWeight = (EditText) findViewById(R.id.bodyWeight);
        extraComments = (EditText) findViewById(R.id.extraComments);
        urineAlb = (EditText)findViewById(R.id.urineAlbumin);
        bleedingVag = (EditText)findViewById(R.id.bleedingVag);

        add_pic = (Button) findViewById(R.id.add_pic);
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);

        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "notify_001");
        Intent ii = new Intent(this, patientDataEntry.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Your Blood Pressure is above critical, Please contact your doctor");
        bigText.setBigContentTitle("SwasthGarbh Alert");
        bigText.setSummaryText("Automated Alert");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("SwasthGarbh Alert");
        mBuilder.setContentText("Your Blood Pressure is above critical, Please contact your doctor");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        final ProgressBar pb = (ProgressBar) findViewById(R.id.sendPB);
        pb.setVisibility(View.GONE);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                final String str_weight = "" + bodyWeight.getText().toString();
                final String str_heart_rate = "" + heartRate.getText().toString();
                final String str_systolic = "" + sysData.getText().toString();
                final String str_diastolic = "" + dysData.getText().toString();
                final int proceed = 0;
                final Float str_bleedingVag;
                if (bleedingVag.getText().length() == 0) {
                    str_bleedingVag = 0f;
                } else {
                    str_bleedingVag = Float.parseFloat(bleedingVag.getText().toString());
                }
                final Float str_urine;
                if (bleedingVag.getText().length() == 0) {
                    str_urine = 0f;
                } else {
                    str_urine = Float.parseFloat(urineAlb.getText().toString());
                }

                String url = ApplicationController.get_base_url() + "api/data";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
//                                Log.d("DATA", response.toString());
                                Toast.makeText(patientDataEntry.this, "Data Uploaded!", Toast.LENGTH_SHORT).show();

                                // Add Image
                                if(ImgBytes.length() == 0){

                                    pb.setVisibility(View.GONE);
                                    if(Integer.parseInt(sysData.getText().toString()) > 160 || Integer.parseInt(dysData.getText().toString()) > 110) {
                                        mNotificationManager.notify(0, mBuilder.build());
                                    }
                                    Intent i = new Intent(patientDataEntry.this, patient_registration.class);
                                    startActivity(i);
                                    return;

                                } else {

                                    String url2 = ApplicationController.get_base_url() + "/api/image";
                                    JsonObjectRequest jsonObjReq2 = new JsonObjectRequest(Request.Method.POST,
                                            url2, null,
                                            new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("TAG", response.toString());
                                                    Toast.makeText(patientDataEntry.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                                                    pb.setVisibility(View.GONE);
                                                    finish();
                                                }
                                            }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("TAG", "Error Message: " + error.getMessage());
                                            Toast.makeText(patientDataEntry.this, "Image not Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {

                                        @Override
                                        public byte[] getBody() {
                                            JSONObject params = new JSONObject();

                                            try {
                                                String extraComm = "";
                                                if(extraComments.getText().length()==0){
                                                    extraComm = "No Comments";
                                                } else {
                                                    extraComm = extraComments.getText().toString();
                                                }
                                                HashMap<String, String> user = session.getUserDetails();
                                                params.put("byte", ImgBytes);
                                                params.put("extra_comments_image",extraComm);
                                                params.put("patient", Integer.parseInt(user.get("id")));
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
                                    ApplicationController.getInstance().addToRequestQueue(jsonObjReq2);
                                    if(sysData.getText().length() != 0 && dysData.getText().length() != 0){
                                        if(Integer.parseInt(sysData.getText().toString()) > 160 || Integer.parseInt(dysData.getText().toString()) > 110) {
                                            mNotificationManager.notify(0, mBuilder.build());
                                        }
                                    }
                                    Intent i = new Intent(patientDataEntry.this, patient_registration.class);
                                    startActivity(i);
                                }


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
                            int sys,dys,hrtrate,weight;

                            if(sysData.getText().length()==0){
                                sys = 0;
                            } else {
                                sys = Integer.parseInt(sysData.getText().toString());
                            }
                            params.put("systolic", sys);

                            if(dysData.getText().length()==0){
                                dys = 0;
                            } else {
                                dys = Integer.parseInt(dysData.getText().toString());
                            }
                            params.put("diastolic", dys);

                            if(heartRate.getText().length()==0){
                                hrtrate = 0;
                            } else {
                                hrtrate = Integer.parseInt(heartRate.getText().toString());
                            }
                            params.put("heart_rate", hrtrate);

                            if(bodyWeight.getText().length()==0){
                                weight = 0;
                            } else {
                                weight = Integer.parseInt(bodyWeight.getText().toString());
                            }
                            params.put("weight", weight);

                            params.put("urine_albumin", str_urine);
                            params.put("bleeding_per_vaginum", str_bleedingVag);

                            params.put("headache", headache.isChecked());
                            params.put("abdominal_pain", abdPain.isChecked());
                            params.put("visual_problems", visProb.isChecked());
                            params.put("decreased_fetal_movements", decFea.isChecked());
                            params.put("swelling_in_hands_or_face", swell.isChecked());
                            params.put("hyper_tension", (sys >= 180 && dys >=120) ? Boolean.TRUE : Boolean.FALSE);

                            String extraComm = "";
                            if(extraComments.getText().length()==0){
                                extraComm = "No Comments";
                            } else {
                                extraComm = extraComments.getText().toString();
                            }
                            params.put("extra_comments", extraComm);

                            params.put("patient", session.getUserDetails().get("id"));
//                            params.put("time_stamp",currentDateandTime);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), ImageUri);
            ivImage.setImageBitmap(thumbnail);
            ivImage.setVisibility(View.VISIBLE);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            byte[] ba  = bytes.toByteArray();
            ImgBytes = Base64.encodeToString(ba, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;

        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        byte[] ba  = bytes.toByteArray();
        ImgBytes = Base64.encodeToString(ba, 0);
        ivImage.setImageBitmap(bm);
        ivImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(patientDataEntry.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(patientDataEntry.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
            ImageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(patientDataEntry.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ImageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }
}