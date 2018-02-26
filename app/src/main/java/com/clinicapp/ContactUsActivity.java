package com.clinicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import AsyTasks.CommonAsyTask;
import Config.ApiParams;
import util.CommonActivity;
import util.NameValuePair;

public class ContactUsActivity extends CommonActivity implements OnMapReadyCallback {
    HashMap<String, String> map;
    TextView txtClinicName, txtClinicAddress, txtClinicPhone, txtClinicLocation;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        setHeaderLogo();
        allowBack();

        txtClinicName = (TextView) findViewById(R.id.clinic_name);
        txtClinicAddress = (TextView) findViewById(R.id.clinic_address);
        txtClinicPhone = (TextView) findViewById(R.id.clinic_phone);
        txtClinicLocation = (TextView) findViewById(R.id.clinic_location);

        ArrayList<HashMap<String, String>> outputArray = common.getParseObject("clinic_contact");
        if(outputArray!=null && outputArray.size() > 0) {
            map = outputArray.get(0);
            updateUI();
        }

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        CommonAsyTask commonTask = new CommonAsyTask(nameValuePairs, ApiParams.CLINC_INFO_URL, handler, true, this);
        commonTask.execute();


    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().containsKey(ApiParams.PARM_RESPONCE)) {
                if (message.getData().getBoolean(ApiParams.PARM_RESPONCE)) {
                    ArrayList<HashMap<String, String>> outputArray = (ArrayList<HashMap<String, String>>) message.getData().getSerializable(ApiParams.PARM_DATA);
                    common.parseObject("clinic_contact",outputArray);
                    map = outputArray.get(0);
                    updateUI();

                } else {
                    common.setToastMessage(message.getData().getString(ApiParams.PARM_ERROR));
                }
            }


        }
    };
    public void updateUI(){
        if(map!=null) {
            txtClinicPhone.setText(map.get("bus_phone"));
            txtClinicName.setText(map.get("bus_title"));
            txtClinicAddress.setText(map.get("bus_address"));
            txtClinicLocation.setText(map.get("country_name") + "," + map.get("city_name") + "," + map.get("postal_code"));

            Button btnCall = (Button) findViewById(R.id.btnPhone);
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callContact(map.get("bus_phone"));
                }
            });

            Button btnEmail = (Button) findViewById(R.id.btnEmail);
            btnEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail(map.get("bus_email"));
                }
            });

            Button btnLocation = (Button) findViewById(R.id.btnLocation);
            btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!map.get("bus_latitude").equalsIgnoreCase("") && !map.get("bus_longitude").equalsIgnoreCase("")) {
                        Intent intent = new Intent(ContactUsActivity.this, MapActivity.class);
                        intent.putExtra("latitude", map.get("bus_latitude"));
                        intent.putExtra("longitude", map.get("bus_longitude"));
                        intent.putExtra("clinic_name", map.get("bus_title"));
                        startActivity(intent);
                    }
                }
            });

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }
    public void sendEmail(String email) {
        /* Create the Intent */
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

/* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I wont to contact...");

/* Send it off to the Activity-Chooser */
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void callContact(String contact) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+contact));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(callIntent);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (map!=null) {
            Double latitude = Double.parseDouble( map.get("bus_latitude"));
            Double longitude = Double.parseDouble(map.get("bus_longitude"));
            String clinic_name = map.get("bus_title");
            // Add a marker in Sydney, Australia, and move the camera.
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title(clinic_name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }
}
