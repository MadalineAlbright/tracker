package com.challengers.trackmyorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.challengers.trackmyorder.service.LocationUpdateService;
import com.challengers.trackmyorder.util.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DeliveryBoyActivity extends AppCompatActivity {
    protected Button availableParcelsBtn, checkDeliveriesBtn;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dboy_main_menu);

        availableParcelsBtn = findViewById(R.id.availableParcelBtn);
        checkDeliveriesBtn = findViewById(R.id.checkDeliveriesBtn);

        if (getIntent().hasExtra(Constants.CURRENT_DELBOY)) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            userId = getIntent().getStringExtra(Constants.CURRENT_DELBOY);

            setTitle("Delivery Boy Home Page");

            availableParcelsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent availableParcelsIntent = new Intent(DeliveryBoyActivity.this,AvailableParcelsActivity.class);
                    availableParcelsIntent.putExtra(Constants.DELIVERY_BOY,userId);
                    startActivity(availableParcelsIntent);
                }
            });
            checkDeliveriesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent deliveriesIntent = new Intent(DeliveryBoyActivity.this,DeliveriesActivity.class);
                    deliveriesIntent.putExtra(Constants.DELIVERY_BOY,userId);
                    startActivity(deliveriesIntent);
                }
            });
        } else {
            finish();
        }
    }
}
