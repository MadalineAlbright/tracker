package com.challengers.trackmyorder;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.Parcel;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class OrderProductActivity extends AppCompatActivity {

    protected EditText parcelName,parcelTo,parcelFrom,parcelDescription;
    protected TextView parcelDestination;
    protected Button orderBtn, checkOrdersBtn, getDetinationBtn;
    protected FirebaseAuth fAuth;
    protected FirebaseFirestore firestore;
    protected CollectionReference userOrders;

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
        getDetinationBtn = findViewById(R.id.getDestinationBtn);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userOrders = firestore.collection("orders");

        getDetinationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(OrderProductActivity.this), 100);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
                }
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

        if(!parcelName.getText().toString().isEmpty()||!parcelDescription.getText().toString().isEmpty()||!parcelDestination.getText().toString().isEmpty()||!parcelTo.getText().toString().isEmpty()||!parcelFrom.getText().toString().isEmpty()){
            Parcel parcel = new Parcel(parcelName.getText().toString(),
                    parcelTo.getText().toString(),
                    parcelFrom.getText().toString(),
                    parcelDescription.getText().toString(),
                    parcelDestination.getText().toString());
            if (currentUser == null) {
                Toast.makeText(OrderProductActivity.this, "Parcel sending has failed.", Toast.LENGTH_SHORT).show();

            }else{
                Order order = new Order(currentTime,parcel,currentUser);
                userOrders.add(order).
                        addOnSuccessListener(documentReference ->{
                            order.setOrderId(documentReference.getId());
                            order.getParcel().setParcelId("prl."+documentReference.getId());
                            userOrders.document(documentReference.getId()).set(order);
                            Toast.makeText(OrderProductActivity.this, "Parcel sent successfully.Await delivery.", Toast.LENGTH_SHORT).show();
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