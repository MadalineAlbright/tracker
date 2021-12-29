package com.challengers.trackmyorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.Parcel;
import com.challengers.trackmyorder.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class OrderProductActivity extends AppCompatActivity {

    protected EditText parcelName,parcelTo,parcelFrom,parcelDescription,parcelDestination;
    protected Button orderBtn, checkOrdersBtn;
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
        parcelDestination = findViewById(R.id.parcelDestinationET);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userOrders = firestore.collection("orders");

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