package com.example.sriharshakarumuri.parkmycar;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.net.Uri;
import android.support.v4.app.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button;
    private double latitude, longitude;
    private double parkLat, parkLong, currLat, currLong, navLat, navLong;
    private int flag = 0, k = 0;
    private String parkStr;


    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button = (Button) findViewById(R.id.park);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 2) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + navLat + "," + navLong));
                    startActivity(intent);
                }

                if (flag == 1) {
                    navigateToCar();
                    // v.setVisibility(View.GONE);
                    flag = 2;
                }

                if (flag == 0) {
                    storeLatLng();
                    flag = 1;

                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findMyLocation();
    }

    private void findMyLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        }

        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                       getLocation(location);

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        else {
            // Add a marker in Tamuc and move the camera
            LatLng sydney = new LatLng(-34, 151);
//          LatLng sydney = new LatLng(20.5, 78.9);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Tamuc"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20.2f));
        }

    }

    private void getLocation(Location location) {
        try {
            mMap.setMyLocationEnabled(true);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            parkLat = currLat = latitude;
            parkLong = currLong = longitude;

            LatLng latLng = new LatLng(latitude, longitude);
            Geocoder geocoder = new Geocoder(getApplicationContext());

            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            String str = addressList.get(0).getAddressLine(0);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.2f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeLatLng() {

        navLat = parkLat;
        navLong = parkLong;
        LatLng park = new LatLng(navLat, navLong);
        mMap.addMarker(new MarkerOptions().position(park).title(parkStr).icon(BitmapDescriptorFactory.fromResource(R.mipmap.carpic)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(park, 18.2f));
        mMap.addCircle(new CircleOptions().center(park).radius(30).strokeColor(Color.RED));

        button.setText("Take me to my Car");
    }


    private void navigateToCar() {

        LatLng curr = new LatLng(currLat, currLong);
        if (k == 0) {
            mMap.addMarker(new MarkerOptions().position(curr).title("You're Here !!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 18.2f));
            mMap.addCircle(new CircleOptions().center(curr).radius(30).strokeColor(Color.GRAY));
            k=1;
        }

        button.setText("Navigate in Google Maps");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
