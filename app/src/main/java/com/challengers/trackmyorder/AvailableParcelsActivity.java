package com.challengers.trackmyorder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.challengers.trackmyorder.model.Order;
import com.challengers.trackmyorder.model.Parcel;
import com.challengers.trackmyorder.util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AvailableParcelsActivity extends AppCompatActivity {
    protected ListView availableParcelsLV;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ArrayList<Order> orders;
    private String username;
    private CollectionReference availableParcels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_parcels);

        availableParcelsLV = findViewById(R.id.availableParcelsLV);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        username = getIntent().getStringExtra(Constants.DELIVERY_BOY);
        availableParcels = firestore.collection("orders");
        orders = new ArrayList<>();

        ProgressDialog progBar;
        progBar = new ProgressDialog(AvailableParcelsActivity.this);
        progBar.setTitle("Fetching Parcels Available for delivery.");
        progBar.setMessage("Please wait");
        progBar.setCancelable(false);
        progBar.show();

        CollectionReference colRef = firestore.collection("orders");
        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    availableParcels.document(order.getOrderId()).set(order);
                    finish();
                    startActivity(getIntent());
                }
            });

            return convertView;
        }
    }
}