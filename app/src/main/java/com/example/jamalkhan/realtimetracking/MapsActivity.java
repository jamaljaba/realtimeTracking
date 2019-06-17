package com.example.jamalkhan.realtimetracking;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
                GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

TextView textView;
        private LatLng mLastLocation,passengerLatLng;
        final String[] longitude = new String[1];
        final String[] latitude = new String[1];
        private  List<LatLng> points;
        Double latPoints,longPoints;
        GoogleMap map;
        String locationLat = "";
        String locationLng = "";
        private Polyline gpsTrack;
        private SupportMapFragment mapFragment;
        private GoogleApiClient googleApiClient;
        private LatLng lastKnownLatLng;
        String passengerID,pp="";
        private Marker passengerMarker;
        private DatabaseReference driverLocationRef;
        private ValueEventListener driverLocationRefListener;
        String[] name;
    //String data;
    String number;
    String[] carType;
    String carNumber;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

    PackageManager p = getPackageManager();
    ComponentName componentName = new ComponentName(this, MapsActivity.class);
    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    Intent intent = getIntent();
//        String action=intent.getAction();
//        Uri data=intent.getData();
    if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        Uri uri = intent.getData();
        // may be some test here with your custom uri
        //String var = uri.getQueryParameter("var"); // "str" is set
        // String varr = uri.getQueryParameter("varr"); // "string" is set
        textView = findViewById(R.id.textView2);
        pp= uri.toString();
        passengerID=pp.substring(39,67);
        textView.setText(passengerID);
        String[] datas = passengerID.split("/", 4);
        //System.out.println("Name = "+datas[0]); //Pankaj
//         System.out.println("Address = "+datas[1]); //New York,USA
        String txt = "";
        String domain = "http://SecuredTransporter.com";
        String substrings = domain.substring(7, 25);
        Toast.makeText(this, ""+substrings, Toast.LENGTH_SHORT).show();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getPassengerLocation();

//        for(int i=0;i<datas.length;i++){
//               txt=txt+"\n"+datas[i];
//                if(datas[i].equals("SecuredTransporter.com")){
//                    domain=datas[i];
//                }if(datas[i].equals("/id")){
//                   pp=datas[i].substring(39,67);
//                   textView.setText(pp);
//
//               // name=datas[i].split("name");
//                Toast.makeText(this, ""+passengerID, Toast.LENGTH_SHORT).show();
//                }if(datas[i].equals("carType")){
//                //substrings+=datas[i].substring(5,17);
//                carType=datas[i].split("carType");
//                Toast.makeText(this, ""+carType[0], Toast.LENGTH_SHORT).show();
//            }else{
//                   //txt+=datas[i];
//                }
//
//            }


    } else {

        Toast.makeText(this, "not found the link", Toast.LENGTH_SHORT).show();
    }

        if (googleApiClient == null) {
        googleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

                try {
                        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

                        googleApiClient.connect();
                }catch (IllegalStateException e)
                {
                        Log.e("IllegalStateException", e.toString());
                }
        }

        }
        private void getPassengerLocation() {
            final String lat = "";

if(passengerID!=null){
            driverLocationRef = FirebaseDatabase.getInstance().getReference().child("Track").child("TrackingID").child(passengerID).child("i");
            //   driverLocationRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Passenger").child(passengerID).child("Tracking");
            //   driverLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(passengerID).child("l");
            driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("latitude") != null) {
                            latitude[0] = map.get("latitude").toString();

                        }
                        if (map.get("longitude") != null) {
                            longitude[0] = map.get("longitude").toString();

                        }
                        parsingFunct();
//if(dataSnapshot.getValue().toString().contains("latitude")||dataSnapshot.getValue().toString().contains("longitude")){
//                                        if(dataSnapshot.getValue().toString().contains("latitude")){
//
//
//                                        }else{
//
//                                        }
//    locationLat[0]=dataSnapshot.getValue().toString();
//       // locationLat[1]=dataSnapshot.getValue().toString();
//       Toast.makeText(MapsActivity.this, ""+locationLat[0]+" ", Toast.LENGTH_SHORT).show();
//
//} //String substrings = domain.substring(7, 14);
//if(locationLat.equals("latitude")){
//        lat[0] =locationLat.substring(7, 14);
//}
//for(int index=0;index<locationLat[0].length();index++){
//                                        if(locationLat[0].equals("latitude")){
//                                            lat =locationLat.length().substring(9, 19);
//                                            Toast.makeText(MapsActivity.this, "lat "+lat[index] , Toast.LENGTH_SHORT).show();
//                                        } if(locationLat[index].equals("longitude")){
//                                            lat[index] =locationLat[index].substring(11, 21);
//        Toast.makeText(MapsActivity.this, "lat "+lat[index] , Toast.LENGTH_SHORT).show();
//
//    }

                    }
                    //  }
                    //    lat=latitude[0].toString();


                    Toast.makeText(MapsActivity.this, " " + latitude[0] + "  " + longitude[0], Toast.LENGTH_SHORT).show();

//                                        if(map.get(0) != null){
//                                                locationLat = Double.parseDouble(map.get(0).toString());
//                                        }
//                                        if(map.get(1) != null){
//                                                locationLng = Double.parseDouble(map.get(1).toString());
//                                        }
                    // passengerLatLng = new LatLng(locationLat,locationLng);
                    if (passengerMarker != null) {
                        passengerMarker.remove();
                    }
                    //  Toast.makeText(MapsActivity.this, "latitude "+locationLat+" longitude "+locationLng, Toast.LENGTH_SHORT).show();
//                    Location loc1 = new Location("");
//                    loc1.setLatitude(pickupLocation.latitude);
//                    loc1.setLongitude(pickupLocation.longitude);

//                    Location loc2 = new Location("");
//                    loc2.setLatitude(passengerLatLng.latitude);
//                    loc2.setLongitude(passengerLatLng.longitude);
//when drivers arives(dialogue builder)
if(latPoints!=null&&longPoints!=null) {
    passengerLatLng = new LatLng(latPoints, longPoints);
    //passengerMarker = map.addMarker(new MarkerOptions().position(passengerLatLng).title("your Member").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
}else{
    Toast.makeText(MapsActivity.this, "Data sre Not Exist Before Riding...", Toast.LENGTH_SHORT).show();

}
                    updateTrack();
                    Toast.makeText(MapsActivity.this, "Data are to be track in list...", Toast.LENGTH_SHORT).show();

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, "Errorr is : " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });

        }else{
    Toast.makeText(this, "You cannot be Traced without getting Ride..", Toast.LENGTH_SHORT).show();

}
}

        public void parsingFunct(){

            double[] lats = new double[latitude.length];
            double[] langs = new double[longitude.length];
            for (int i = 0; i < lats.length; i++) {
                lats[i] = Double.parseDouble(latitude[i]);
                latPoints=lats[i];
            }
            for (int i = 0; i < langs.length; i++) {
                langs[i] = Double.parseDouble(longitude[i]);
                longPoints=langs[i];
            }
            GetTrack();
        }
        public void GetTrack(){

         //  Toast.makeText(this, "l "+latPoints+" o"+longPoints, Toast.LENGTH_SHORT).show();
            LatLng passenger = new LatLng(latPoints,longPoints);
            map.moveCamera(CameraUpdateFactory.newLatLng(passenger));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(passenger, 15));
            Marker mDriverMarker = map.addMarker(new MarkerOptions().position(passenger).title(passengerID).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground)));

        }
@Override
public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getPassengerLocation();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(9);
        gpsTrack = map.addPolyline(polylineOptions);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
        }
      //  map.setMyLocationEnabled(true);
        getPassengerLocation();
        }


@Override
protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        }

@Override
protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        }

@Override
protected void onPause() {
        super.onPause();
//        stopLocationUpdates();
        }

@Override
public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
                googleApiClient.connect();
        startLocationUpdates();
        }
}

@Override
public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
        }

@Override
public void onConnectionSuspended(int i) {

        }

@Override
public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

@Override
public void onLocationChanged(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
      getPassengerLocation();
        }

protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
        googleApiClient, this);
        }




        private void updateTrack() {
//        points = gpsTrack.getPoints();
//        points.add(passengerLatLng);
//        gpsTrack.setPoints(points);

        }

        }