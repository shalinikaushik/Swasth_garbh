package com.swasthgarbh.root.swasthgarbh;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class WholeImagesListAdapter extends ArrayAdapter<WholeImagesListClass> {

    static SessionManager session;

    public WholeImagesListAdapter(Activity context, ArrayList<WholeImagesListClass> patientData) {
        super(context, 0, patientData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final WholeImagesListClass current_medicine_data = getItem(position);
        final Integer imageId = current_medicine_data.getId();

        session = new SessionManager(getContext());
        final HashMap<String, String> user = session.getUserDetails();

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.patient_image_row, parent, false);
        }

        final TextView extraComments = (TextView)listItemView.findViewById(R.id.extraCommentsInImage);
        extraComments.setText(current_medicine_data.getExtraComments());

        final TextView dateText = (TextView)listItemView.findViewById(R.id.image_date);
        dateText.setText(current_medicine_data.getDateString());


        ImageView dataDelete = (ImageView)listItemView.findViewById(R.id.deleteImageData);

        if ("patient".equals(user.get("type"))) {
            //patient action
            dataDelete.setVisibility(View.VISIBLE);
        } else if ("doctor".equals(user.get("type"))) {
            dataDelete.setVisibility(View.GONE);
        }

        dataDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(v.getContext());
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this Image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                String url = ApplicationController.get_base_url() + "api/singleimage/" + imageId;
                                StringRequest jsonObjReq = new StringRequest(Request.Method.DELETE,
                                        url,
                                        new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String   response) {
                                                Log.i("Delete", "onResponse: Data deleted");
                                                Toast.makeText(v.getContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                                ((all_images_view)v.getContext()).getPatientImageData();
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

        final LinearLayout clickDownloadImage = (LinearLayout)listItemView.findViewById(R.id.patientImage);
        clickDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(v.getContext(), image_view_modal.class);
                intent.putExtra("EXTRA_IMAGE_ID", imageId);
                v.getContext().startActivity(intent);
            }
        });
        return listItemView;
    }
}
