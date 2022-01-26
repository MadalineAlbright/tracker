package com.challengers.trackmyorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.challengers.trackmyorder.model.DeliveryBoy;
import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.Parcel;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AvailableParcelsActivity extends AppCompatActivity {
    protected ListView availableParcelsLV;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ArrayList<Order> orders;
    private String username;
    private CollectionReference availableParcels;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private DocumentReference currentDeliveryBoyDocRef;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private String deliveryBoyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_parcels);

        availableParcelsLV = findViewById(R.id.availableParcelsLV);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        deliveryBoyEmail = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra(Constants.DELIVERY_BOY);
        availableParcels = firestore.collection("orders");
        currentDeliveryBoyDocRef = firestore.collection("users").document(username);
        orders = new ArrayList<>();

        ProgressDialog progBar;
        progBar = new ProgressDialog(AvailableParcelsActivity.this);
        progBar.setTitle("Fetching Parcels Available for delivery.");
        progBar.setMessage("Please wait");
        progBar.setCancelable(false);
        progBar.show();


        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

//        locationCallback =  new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                Location location = locationResult.getLastLocation();
//            }
//        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });
        }
        CollectionReference orderRef = firestore.collection("orders");
        orderRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot obj:queryDocumentSnapshots){
                    Order order = obj.toObject(Order.class);
                    if(order.getDeliveryBoyId() == null){
                        orders.add(order);
                    }
                }
                progBar.dismiss();
                availableParcelsLV.setAdapter(new AvailableParcelsArrayAdapter(AvailableParcelsActivity.this,orders,username));
            }
        });
    }


    private class AvailableParcelsArrayAdapter extends ArrayAdapter<Order> {
        Context context;
        ArrayList<Order> orders;
        String userId;
        public AvailableParcelsArrayAdapter(Context context, ArrayList<Order> ordersList,String userId) {
            super(context,0,ordersList);
            this.context = context;
            this.orders = ordersList;
            this.userId = userId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Order order = getItem(position);
            Parcel parcel = order.getParcel();

            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.available_parcel_list_item,parent,false);
            }
            TextView availableOrderIdTV = convertView.findViewById(R.id.deliveriesOrderIdTV);
            TextView availableNameTV = convertView.findViewById(R.id.availableNameTV);
            TextView availableDestinationTV = convertView.findViewById(R.id.availableDestinationTV);
            TextView availableFromTV = convertView.findViewById(R.id.availableFromTV);
            TextView availableToTV = convertView.findViewById(R.id.availableToTV);

            Button makeDeliveryBtn = convertView.findViewById(R.id.moreDetailsBtn);

            availableOrderIdTV.setText(order.getOrderId());
            availableNameTV.setText(parcel.getName());
            availableDestinationTV.setText(parcel.getDestinationName());
            availableFromTV.setText(parcel.getFrom());
            availableToTV.setText(parcel.getTo());


            makeDeliveryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    order.setDeliveryBoyId(userId);
                    order.setStatus("Delivery in progress.");
                    order.getParcel().setStatus("Delivery in progress.");
                    availableParcels.document(order.getOrderId()).set(order);

                    DeliveryBoy deliveryBoy = new DeliveryBoy(userId,deliveryBoyEmail);
                    HashMap<String,Double> deliveryBoyLocation = new HashMap<>();
                    deliveryBoyLocation.put("Latitude",currentLocation.getLatitude());
                    deliveryBoyLocation.put("Longitude",currentLocation.getLongitude());
                    deliveryBoy.setCurrentStatus("Making delivery.");
                    deliveryBoy.setCurrentLocation(deliveryBoyLocation);
                    deliveryBoy.setCurrentOrderId(order.getOrderId());
                    currentDeliveryBoyDocRef.set(deliveryBoy);
                    finish();
                    startActivity(getIntent());
                }
            });

            return convertView;
        }
    }
}