package com.challengers.trackmyorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.challengers.trackmyorder.model.Product;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowUserOrdersActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ListView productOrdersLV;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_orders);
        setTitle("My Parcels");
        productOrdersLV = findViewById(R.id.productOrdersLV);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();

        if(getIntent().hasExtra(Constants.CURRENT_USER)) {
            String username = getIntent().getStringExtra(Constants.CURRENT_USER);
            CollectionReference colRef = firestore.collection("orders");
            colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot obj:queryDocumentSnapshots){
                        Product product = obj.toObject(Product.class);
                        products.add(product);
                    }
                    productOrdersLV.setAdapter(new CustomUserOrderArrayAdapter(ShowUserOrdersActivity.this,products,username));
                }
            });
        } else {
            Toast.makeText(ShowUserOrdersActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class CustomUserOrderArrayAdapter extends  ArrayAdapter<Product> {
        Context context;
        ArrayList<Product> orders;
        String userId;
        public CustomUserOrderArrayAdapter(Context context, ArrayList<Product> ordersList,String userId) {
            super(context,0,ordersList);
            this.context = context;
            this.orders = ordersList;
            this.userId = userId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Product product = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.user_order_list_item,parent,false);
            }
            TextView ckName = convertView.findViewById(R.id.ckName);
            TextView ckDes = convertView.findViewById(R.id.ckDes);
            TextView ckPrice = convertView.findViewById(R.id.ckPrice);
            TextView ckTime = convertView.findViewById(R.id.ckTime);
            Button trackMapBtn = convertView.findViewById(R.id.trackBtn);

            if(product.getUserId().equals(userId)){
                ckName.setText(product.getProdName());
                ckDes.setText(product.getProdDes());
                ckPrice.setText(String.valueOf(product.getProdPrice()));
                ckTime.setText(product.getDate());
                trackMapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mapsActivity = new Intent(ShowUserOrdersActivity.this,MapsActivity.class);
                        mapsActivity.putExtra(Constants.MAPS_TYPE,"customer");
                        mapsActivity.putExtra(Constants.CURRENT_USER,userId);
                        startActivity(mapsActivity);
                    }
                });
            }

            return convertView;
        }
    }
}