package com.challengers.trackmyorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.challengers.trackmyorder.model.DeliveryBoy;
import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.util.Constants;
import com.firebase.client.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeliveryBoyMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String userId, delBoyId, mapType;
    private String userLocation, delBoyLocation;
    private Marker riderMarker,destinationMarker;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference currentDeliveryBoyDocRef;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private DeliveryBoy deliveryBoy;
    private double destinationLongitude,destinationLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_maps);

        userId = getIntent().getStringExtra(Constants.DELIVERY_BOY);
        destinationLatitude = Double.parseDouble(getIntent().getStringExtra("Latitude"));
        destinationLongitude = Double.parseDouble(getIntent().getStringExtra("Longitude"));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        //Log.i("User ID", userId);
        currentDeliveryBoyDocRef = firebaseFirestore.collection("users").document(userId);
        //Log.i("Document reference ID", currentDeliveryBoyDocRef.getId());
        currentDeliveryBoyDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                deliveryBoy = new DeliveryBoy(value.getString("userId"), value.getString("username"));
                deliveryBoy.setCurrentStatus(value.getString("currentStatus"));
                deliveryBoy.setCurrentOrderId(value.getString("currentOrderId"));
                deliveryBoy.setType(Constants.DELIVERY_BOY);
            }
        });
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateGps();
            }
        };

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.deliveryBoyMapFrag);
        mapFragment.getMapAsync(this);
    }/*

    protected class BackgroundTask extends AsyncTask<Void,Void,Void> {
        private ProgressDialog progBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progBar = new ProgressDialog(DeliveryBoyMapsActivity.this);
            progBar.setTitle("Tracking.");
            progBar.setMessage("Please wait");
            progBar.setCancelable(false);
            progBar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            updateGps();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progBar.dismiss();
        }
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
            updateGps();
        }
        else {
            Toast.makeText(DeliveryBoyMapsActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGps(){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    updateMap(location);
                }
            });
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        }
    }
    private void updateMap(Location location) {
        currentLocation = location;
        if(location != null) {
            //Updating firebase firestore with the new location
            HashMap<String,Double> newLocation = new HashMap<>();
            newLocation.put("Latitude",currentLocation.getLatitude());
            newLocation.put("Longitude", currentLocation.getLongitude());
            deliveryBoy.setCurrentLocation(newLocation);
            currentDeliveryBoyDocRef.set(deliveryBoy);

            LatLng rider = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            LatLng destination = new LatLng(destinationLatitude, destinationLongitude);

            riderMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_delivery)).position(rider).title("You are here"));
            destinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_iconmonstr_home_6)).position(destination).title("Make delivery here."));

            List<Marker> markersList = new ArrayList<Marker>();
            markersList.add(riderMarker);
            markersList.add(destinationMarker);
            LatLngBounds.Builder builder;
            builder = new LatLngBounds.Builder();
            for (Marker m : markersList) {
                builder.include(m.getPosition());
            }

            //Bounds padding here
            int padding = 50;

            //Create bounds here
            LatLngBounds bounds = builder.build();

            //Create camera with bounds
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            //Check map is loaded
            mMap.animateCamera(cameraUpdate);
        }
        else{
            Toast.makeText(this, "Location Service is currently unavailable. Try again later.", Toast.LENGTH_SHORT).show();
            Intent checkOrders = new Intent(DeliveryBoyMapsActivity.this,DeliveriesActivity.class);
            checkOrders.putExtra(Constants.CURRENT_USER,getIntent().getStringExtra(Constants.CURRENT_USER));
            startActivity(checkOrders);
        }
    }/*
    public void showMap(List<Marker> markersList) {
        //get the latLngbuilder from the marker list
        LatLngBounds.Builder builder;
        builder = new LatLngBounds.Builder();
        for (Marker m : markersList) {
            builder.include(m.getPosition());
        }

        //Bounds padding here
        int padding = 50;

        //Create bounds here
        LatLngBounds bounds = builder.build();

        //Create camera with bounds
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        //Check map is loaded
        mMap.animateCamera(cameraUpdate);
        *//*mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //animate camera here
                mMap.animateCamera(cameraUpdate);
            }
        });
    }*/
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        updateGps();
        /*riderMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_delivery)).position(new LatLng(destinationLatitude,destinationLongitude)).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(destinationLatitude,destinationLongitude)));
        // Zoom out to zoom level 10, animating with a duration of 1 second.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);*/
    }
}