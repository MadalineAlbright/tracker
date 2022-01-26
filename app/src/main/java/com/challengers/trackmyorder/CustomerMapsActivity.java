package com.challengers.trackmyorder;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.challengers.trackmyorder.util.Constants;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String userId, deliveryBoyUsername, mapType;
    private String userLocation, delBoyLocation;
    private Marker destinationMarker,riderMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private FirebaseFirestore firestore;
    private DocumentReference deliveryBoyDocRef;
    private LatLng latLng;
    private ProgressDialog progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    setTitle("Track your Parcel");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        currentLocation = new LatLng(Double.parseDouble(getIntent().getStringExtra("Latitude")),Double.parseDouble(getIntent().getStringExtra("Longitude")));
        deliveryBoyUsername = getIntent().getStringExtra(Constants.DELIVERY_BOY);
        firestore = FirebaseFirestore.getInstance();
        deliveryBoyDocRef = firestore.collection("users").document(deliveryBoyUsername);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                latLng = new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                Log.i("DeliveryBoy",String.valueOf(locationResult.getLastLocation().getLatitude()));
                updateMap(latLng);
                deliveryBoyDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            HashMap <String,Double> currentLocationArray = (HashMap<String, Double>) value.get("currentLocation");
                            latLng = new LatLng(currentLocationArray.get("Latitude"),currentLocationArray.get("Longitude"));
                            Log.i("DeliveryBoy",currentLocationArray.get("Latitude").toString());
                            updateMap(latLng);
                        }catch (Exception e){
                            Log.i("Error",e.getMessage());
                            Toast.makeText(CustomerMapsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 10){
            updateGps();
        }
        else {
            Toast.makeText(CustomerMapsActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGps(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateMap(latLng);
                }
            });
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        }
    }

    private void updateMap(LatLng riderCurrentLocation) {
        mMap.clear();
        if(riderCurrentLocation != null) {
            LatLng locationUpdate = new LatLng(currentLocation.latitude, currentLocation.longitude);
            destinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_iconmonstr_home_6)).position(locationUpdate).title("Customer is here"));



            LatLng rider = riderCurrentLocation;
            //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp);
            riderMarker = mMap.addMarker(new MarkerOptions().icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_delivery)).position(rider).title("Your Order is here"));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(rider));
            // Zoom out to zoom level 10, animating with a duration of 1 second.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 1000, null);

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
            Intent checkOrders = new Intent(CustomerMapsActivity.this,ShowUserOrdersActivity.class);
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
        destinationMarker = mMap.addMarker(new MarkerOptions().position(location).title("User is here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        // Zoom out to zoom level 10, animating with a duration of 1 second.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 1000, null);
    }
}
