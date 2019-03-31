package com.swasthgarbh.root.swasthgarbh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class image_view_modal extends AppCompatActivity {

    static SessionManager session;
    Bitmap decodedByte;
    String dateString;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_modal);
        Integer imageId = getIntent().getIntExtra("EXTRA_IMAGE_ID", 1);
        session = new SessionManager(this);
        final HashMap<String, String> user = session.getUserDetails();
        Button downloadImage = (Button)findViewById(R.id.downloadImage);
        pb = (ProgressBar) findViewById(R.id.imagePb);
        pb.setVisibility(View.VISIBLE);
        getImage(imageId);
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImage(decodedByte, v.getContext(), dateString);
            }
        });
    }

    public void getImage(Integer imageId){
        String url = ApplicationController.get_base_url() + "api/singleimage/" + imageId;
        final ArrayList<WholeImagesListClass> imageRowAdapter = new ArrayList<WholeImagesListClass>();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("AllPatientList", response.toString());
                        try {
                            Integer id =response.getInt("id");
                            String imageString = response.getString("byte");
                            dateString = response.getString("time_stamp");
                            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ImageView image =(ImageView)findViewById(R.id.compImage);
                            image.setImageBitmap(decodedByte);
                            pb.setVisibility(View.GONE);

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

    private static void SaveImage(Bitmap finalBitmap, Context v, String dateString) {

        Activity activity = (Activity) v;

        if (ContextCompat.checkSelfPermission(v, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/SwasthGarbh");
            myDir.mkdirs();

            String fname = "Image" + dateString +".jpg";
            File file = new File (myDir, fname);
            if (file.exists ()) file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(v, "Image Downloaded", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }
    }
}
