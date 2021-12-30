package com.challengers.trackmyorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private FirebaseUser currentUser;
    private String[] orders;
    private String orderList, userId;
    private Button myOrdersButton;
    private static int LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dboy_main_menu);

        if(getIntent().hasExtra(Constants.CURRENT_DELBOY)) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            userId = getIntent().getStringExtra(Constants.CURRENT_DELBOY);

            setTitle(""+currentUser.getEmail());

            myOrdersButton = (Button) findViewById(R.id.availableParcelBtn);

            myOrdersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PickOrderDialog pickOrderDialog = new PickOrderDialog();
                    pickOrderDialog.setOrders(orders);
                    pickOrderDialog.show(getFragmentManager(),"Pick Order Dialog");
                }
            });
            Firebase currentDelBoyRef = Constants.delboyRef.child("/" + userId);
            currentDelBoyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    orderList = (String) dataSnapshot.child("currentOrders").getValue();
                    orders = orderList.split(Constants.LOCATION_DELIMITER);

                    /*if(myOrdersButton != null) {
                        myOrdersButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PickOrderDialog pickOrderDialog = new PickOrderDialog();
                                pickOrderDialog.setOrders(orders);
                                pickOrderDialog.show(getFragmentManager(),"Pick Order Dialog");
                            }
                        });
                    }*/
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(DeliveryBoyActivity.this, "Check network connection", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    /*|| ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {*/
                //Request for Permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Location Permission is needed to proceed", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        } else {
            Intent intent = new Intent(this, LocationUpdateService.class);
            startService(intent);
        }

    }

    public void launchUpdateOrderStatusActivity(View v){
        startActivity(new Intent(DeliveryBoyActivity.this,UpdateOrderActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, LocationUpdateService.class);
                startService(intent);
            } else {
                Toast.makeText(DeliveryBoyActivity.this, "Need Location permission to show in map", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
