package com.clinicapp;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import util.CommonActivity;
public class MapActivity extends CommonActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Double latitude, longitude;
    String clinic_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
        longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        clinic_name = getIntent().getExtras().getString("clinic_name");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(clinic_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
