package com.challengers.trackmyorder;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.util.Constants;

public class MainActivity extends AppCompatActivity {

    Button customerBtn, driverBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customerBtn = (Button) findViewById(R.id.customerBtn);
        driverBtn = findViewById(R.id.driverBtn);
        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(Constants.LOGINTYPE, "Customer");
                startActivity(intent);
            }
        });
        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(Constants.LOGINTYPE, "Driver");
                startActivity(intent);
            }
        });
    }
}
