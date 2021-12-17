package com.challengers.trackmyorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.challengers.trackmyorder.model.Product;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OrderProductActivity extends AppCompatActivity {

    protected TextView prodNameTxt,prodPriceTxt,prodDesTxt;
    protected Button orderBtn, checkOrdersBtn;
    protected FirebaseAuth fAuth;
    protected FirebaseFirestore firestore;
    protected CollectionReference userOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product);

        prodNameTxt = (TextView) findViewById(R.id.prodName);
        prodPriceTxt = (TextView) findViewById(R.id.prodPrice);
        prodDesTxt = (TextView) findViewById(R.id.prodDes);
        orderBtn = (Button) findViewById(R.id.orderBtn);
        checkOrdersBtn = (Button) findViewById(R.id.checkOrdersBtn);

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

        Product product = new Product(currentUser,prodNameTxt.getText().toString(),prodDesTxt.getText().toString(),prodPriceTxt.getText().toString(),currentTime);
        /*HashMap<String,String> data= new HashMap<>();
        data.put("userId",currentUser);
        data.put("prodName",prodNameTxt.getText().toString());
        data.put("prodPrice",prodPriceTxt.getText().toString());
        data.put("prodDes",prodDesTxt.getText().toString());
        data.put("date",currentTime);*/

        /*ArrayList<HashMap<String,String>> order = new ArrayList<>();
        order.add(data);*/

        userOrders.add(product).
                addOnSuccessListener(documentReference ->{
                    Toast.makeText(OrderProductActivity.this, "Order is successful", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(OrderProductActivity.this, "Order has failed", Toast.LENGTH_SHORT).show();
        });
        /*firestore.collection("orders").document(getIntent().getStringExtra(Constants.CURRENT_USER)).set(order,SetOptions.merge()).
                addOnSuccessListener(documentReference ->{
                    Toast.makeText(OrderProductActivity.this, "Order is successful", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
            Toast.makeText(OrderProductActivity.this, "Order has failed", Toast.LENGTH_SHORT).show();
        });*/
    }
}