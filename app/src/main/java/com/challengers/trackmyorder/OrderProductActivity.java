package com.challengers.trackmyorder;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.Parcel;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class OrderProductActivity extends AppCompatActivity {

    protected EditText parcelName,parcelTo,parcelFrom,parcelDescription;
    protected TextView parcelDestination;
    protected Button orderBtn, checkOrdersBtn;
    protected FirebaseAuth fAuth;
    protected FirebaseFirestore firestore;
    protected CollectionReference userOrders;
    protected AutocompleteSupportFragment destinationFrag;
    protected HashMap<String,String> destinationLatLng;
    protected String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product);

        orderBtn = (Button) findViewById(R.id.sendParcelBtn);
        checkOrdersBtn = (Button) findViewById(R.id.checkParcelsBtn);
        parcelName = findViewById(R.id.parcelNameET);
        parcelTo = findViewById(R.id.parcelToET);
        parcelFrom = findViewById(R.id.parcelFromET);
        parcelDescription = findViewById(R.id.parcelDesET);
        parcelDestination = findViewById(R.id.destinationTV);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userOrders = firestore.collection("orders");

        String placesAPIKey = "AIzaSyDzRrvzZYuy1QtT4Hz133xUnZo4n9oirQo";
        Places.initialize(getApplicationContext(),placesAPIKey);
        PlacesClient placesClient = Places.createClient(this);
        destinationLatLng = new HashMap<>();

        destinationFrag = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destinationFrag);

        // Specify the types of place data to return.
        destinationFrag.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        //Restrict to a specific region
        destinationFrag.setCountries("KE");
        // Set up a PlaceSelectionListener to handle the response.
        destinationFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                parcelDestination.setText("Destination: " + place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                destinationLatLng.put("Latitude",latitude);
                destinationLatLng.put("Longitude",longitude);
                destinationName = place.getName();
            }

            @Override
            public void onError(@androidx.annotation.NonNull Status status) {
                Toast.makeText(OrderProductActivity.this, "An error has occurred"+status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeOrder();
            }
        });


        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkOrders = new Intent(OrderProductActivity.this,ShowUserOrdersActivity.class);
                checkOrders.putExtra(Constants.CURRENT_USER,getIntent().getStringExtra(Constants.CURRENT_USER));
                startActivity(checkOrders);
            }
        });
    }

    protected void MakeOrder(){
        String currentTime = Calendar.getInstance().getTime().toString();
        String currentUser = getIntent().getStringExtra(Constants.CURRENT_USER);

        if(!parcelName.getText().toString().isEmpty()||!parcelDescription.getText().toString().isEmpty()||!parcelTo.getText().toString().isEmpty()||!parcelFrom.getText().toString().isEmpty()){
            Parcel parcel = new Parcel(parcelName.getText().toString(),
                    parcelTo.getText().toString(),
                    parcelFrom.getText().toString(),
                    parcelDescription.getText().toString(),
                    destinationLatLng,
                    destinationName);
            if (currentUser == null) {
                Toast.makeText(OrderProductActivity.this, "Error has occurred, please login again to send parcel.", Toast.LENGTH_SHORT).show();

            }else{
                Order order = new Order(currentTime,parcel,currentUser);
                userOrders.add(order).
                        addOnSuccessListener(documentReference ->{
                            order.setOrderId(documentReference.getId());
                            order.getParcel().setParcelId("prl."+documentReference.getId());
                            order.getParcel().setStatus(order.getStatus());
                            userOrders.document(documentReference.getId()).set(order);
                            Toast.makeText(OrderProductActivity.this, "Parcel sent successfully. Await delivery.", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                    Toast.makeText(OrderProductActivity.this, "Parcel sending has failed.", Toast.LENGTH_SHORT).show();
                });
            }
        }
        else{
            Toast.makeText(OrderProductActivity.this, "Please fill all the values", Toast.LENGTH_SHORT).show();
        }

    }
}