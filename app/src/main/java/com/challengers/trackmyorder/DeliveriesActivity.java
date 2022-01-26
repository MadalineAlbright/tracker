package com.challengers.trackmyorder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class DeliveriesActivity extends AppCompatActivity {

    protected ListView deliveriesParcelsLV;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ArrayList<Order> orders;
    private String username;
    private CollectionReference deliveriesParcels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveries);
        deliveriesParcelsLV = findViewById(R.id.deliveriesParcelsLV);
        setTitle(getIntent().getStringExtra("title"));

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        username = getIntent().getStringExtra(Constants.DELIVERY_BOY);
        deliveriesParcels = firestore.collection("orders");
        orders = new ArrayList<>();

        ProgressDialog progBar;
        progBar = new ProgressDialog(this);
        progBar.setTitle("Fetching my parcels.");
        progBar.setMessage("Please wait");
        progBar.setCancelable(true);
        progBar.show();

        CollectionReference colRef = firestore.collection("orders");
        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot obj:queryDocumentSnapshots){
                    Order order = obj.toObject(Order.class);
                    if(order.getDeliveryBoyId()!=null){
                        if(order.getDeliveryBoyId().equals(username)){
                            orders.add(order);
                        }
                    }

                }
                progBar.dismiss();
                deliveriesParcelsLV.setAdapter(new DeliveriesParcelsArrayAdapter(DeliveriesActivity.this,orders,username));
            }
        });
    }


    private class DeliveriesParcelsArrayAdapter extends ArrayAdapter<Order> {
        Context context;
        ArrayList<Order> orders;
        String userId;
        public DeliveriesParcelsArrayAdapter(Context context, ArrayList<Order> ordersList,String userId) {
            super(context,0,ordersList);
            this.context = context;
            this.orders = ordersList;
            this.userId = userId;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Order order = getItem(position);
            Parcel parcel = order.getParcel();

            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.deliveries_list_item,parent,false);
            }
            TextView deliveriesNameTV = convertView.findViewById(R.id.deliveriesNameTV);
            TextView deliveriesDestinationTV = convertView.findViewById(R.id.deliveriesDestinationTV);
            TextView deliveriesFromTV = convertView.findViewById(R.id.deliveriesFromTV);
            TextView deliveriesToTV = convertView.findViewById(R.id.deliveriesToTV);
            TextView deliveriesStatusTV = convertView.findViewById(R.id.deliveriesStatus);

            Button makeDeliveryBtn = convertView.findViewById(R.id.deliveriesTrackBtn);

            deliveriesNameTV.setText(parcel.getName());
            deliveriesDestinationTV.setText(parcel.getDestinationName());
            deliveriesFromTV.setText(parcel.getFrom());
            deliveriesToTV.setText(parcel.getTo());
            deliveriesStatusTV.setText(order.getStatus());
            switch (parcel.getStatus()){
                case "Pending":
                    deliveriesStatusTV.setBackgroundColor(Color.YELLOW);
                    break;
                case "Success":
                    deliveriesStatusTV.setBackgroundColor(Color.GREEN);
                    break;
                case "Failed":
                case "Cancelled":
                    deliveriesStatusTV.setTextColor(Color.BLACK);
                    deliveriesStatusTV.setBackgroundColor(Color.RED);
                    break;
                default:
                    deliveriesStatusTV.setTextColor(Color.BLACK);
                    deliveriesStatusTV.setBackgroundColor(Color.WHITE);
                    break;
            }


            makeDeliveryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent deliveryBoyMapsIntent = new Intent(DeliveriesActivity.this,DeliveryBoyMapsActivity.class);
                    deliveryBoyMapsIntent.putExtra("Latitude",order.getParcel().getDestinationLatLng().get("Latitude"));
                    deliveryBoyMapsIntent.putExtra("Longitude",order.getParcel().getDestinationLatLng().get("Longitude"));
                    deliveryBoyMapsIntent.putExtra(Constants.DELIVERY_BOY,userId);
                    startActivity(deliveryBoyMapsIntent);
                }
            });

            return convertView;
        }
    }
}