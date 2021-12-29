package com.challengers.trackmyorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.challengers.trackmyorder.util.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String userId, delBoyId, mapType;
    private String userLocation, delBoyLocation;
    private Marker lastMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback =  new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateMap(location);
            }
        };
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Toast.makeText(this, ""+getIntent().getStringExtra(Constants.MAPS_TYPE), Toast.LENGTH_SHORT).show();
    }
    protected class BackgroundTask extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progBar = new ProgressDialog(MapsActivity.this);
            progBar.setTitle("Tracking.");
            progBar.setMessage("Please wait");
            progBar.setCancelable(false);
            progBar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progBar.dismiss();
            updateGps();
            return null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
            updateGps();
        }
        else {
            Toast.makeText(MapsActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGps(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        if(location != null) {
            if (getIntent().getStringExtra(Constants.MAPS_TYPE).equals("customer")) {
                LatLng locationUpdate = new LatLng(location.getLatitude(), location.getLongitude());
                lastMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_iconmonstr_home_6)).position(locationUpdate).title("Customer is here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(locationUpdate));
                // Zoom out to zoom level 10, animating with a duration of 1 second.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);


                LatLng rider = new LatLng(currentLocation.getLatitude() + 0.01, currentLocation.getLongitude() + 0.02);
                //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp);
                mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_delivery)).position(rider).title("Your Order is here"));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(rider));
                // Zoom out to zoom level 10, animating with a duration of 1 second.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);

            } else if (getIntent().getStringExtra(Constants.MAPS_TYPE).equals("driver")) {
                LatLng locationUpdate = new LatLng(location.getLatitude(), location.getLongitude());
                lastMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_delivery)).position(locationUpdate).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(locationUpdate));
                // Zoom out to zoom level 10, animating with a duration of 1 second.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);
                LatLng rider = new LatLng(currentLocation.getLatitude() + 0.01, currentLocation.getLongitude() + 0.02);
                //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp);
                mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_iconmonstr_home_6)).position(locationUpdate).title("Make delivery here."));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(rider));
                // Zoom out to zoom level 10, animating with a duration of 1 second.
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);
            }
        }
        else{
            Toast.makeText(this, "Location Service is currently unavailable. Try again later.", Toast.LENGTH_SHORT).show();
            Intent checkOrders = new Intent(MapsActivity.this,ShowUserOrdersActivity.class);
            checkOrders.putExtra(Constants.CURRENT_USER,getIntent().getStringExtra(Constants.CURRENT_USER));
            startActivity(checkOrders);
        }
    }
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
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(delBoyId != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.maps_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.show_directions_menu:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + userLocation);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    */
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
        LatLng location = new LatLng(4.9789,120.09);
        lastMarker = mMap.addMarker(new MarkerOptions().position(location).title("User is here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        // Zoom out to zoom level 10, animating with a duration of 1 second.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);
    }
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mapType != null && mapType.equals("D")) {
            if (delBoyId != null && userId != null) {
                Firebase currentUserRef = Constants.userRef.child("/" + userId);
                currentUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (lastMarker != null) {
                            lastMarker.remove();
                        }
                        String latLang = (String) dataSnapshot.child("currentLocation").getValue();
                        userLocation = latLang;
                        LatLng location = new LatLng(Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[0]), Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[1]));
                        lastMarker = mMap.addMarker(new MarkerOptions().position(location).title("User is here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        // Zoom out to zoom level 10, animating with a duration of 1 second.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MapsActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("Maps Activity", "One of users is null");
            }
        } else if (mapType != null && mapType.equals("customer")) {
            if (userId != null) {
                //Toast.makeText(this, "Hello "+getIntent().getStringExtra(Constants.MAPS_TYPE), Toast.LENGTH_SHORT).show();
                Firebase currentDelBoyRef = Constants.delboyRef.child("/" + delBoyId);
                currentDelBoyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (lastMarker != null) {
                            lastMarker.remove();
                        }
                        String latLang = (String) dataSnapshot.child("currentLocation").getValue();
                        delBoyLocation = latLang;
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp);
                        LatLng location = new LatLng(Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[0]), Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[1]));
                        lastMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(location).title("Your Order is here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        // Zoom out to zoom level 10, animating with a duration of 1 second.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MapsActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("Maps Activity", "One of users is null");
            }
            if (delBoyId != null && userId != null) {
                Firebase currentDelBoyRef = Constants.delboyRef.child("/" + delBoyId);
                currentDelBoyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (lastMarker != null) {
                            lastMarker.remove();
                        }
                        String latLang = (String) dataSnapshot.child("currentLocation").getValue();
                        delBoyLocation = latLang;
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp);
                        LatLng location = new LatLng(Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[0]), Double.parseDouble(latLang.split(Constants.LOCATION_DELIMITER)[1]));
                        lastMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(location).title("Your Order is here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        // Zoom out to zoom level 10, animating with a duration of 1 second.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MapsActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("Maps Activity", "One of users is null");
            }
        }
    }*/
}
