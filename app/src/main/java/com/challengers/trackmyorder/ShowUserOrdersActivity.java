package com.challengers.trackmyorder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class ShowUserOrdersActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ListView productOrdersLV;
    private ArrayList<Order> orders;
    private String username;
    private ProgressDialog progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_orders);
        setTitle("My Parcels");
        productOrdersLV = findViewById(R.id.productOrdersLV);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        username = getIntent().getStringExtra(Constants.CURRENT_USER);
        /*BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute();*/
        orders = new ArrayList<>();
        progBar = new ProgressDialog(ShowUserOrdersActivity.this);
        progBar.setTitle("Fetching Parcels List.");
        progBar.setMessage("Please wait");
        progBar.setCancelable(false);
        progBar.show();
        if(getIntent().hasExtra(Constants.CURRENT_USER)) {
            CollectionReference colRef = firestore.collection("orders");
            colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot obj:queryDocumentSnapshots){
                        Order order = obj.toObject(Order.class);
                        if(order.getUserId().equals(username)){
                            orders.add(order);
                        }
                    }
                    progBar.dismiss();
                    productOrdersLV.setAdapter(new CustomUserOrderArrayAdapter(ShowUserOrdersActivity.this,orders,username));
                }
            });
        } else {
            progBar.dismiss();
            Toast.makeText(ShowUserOrdersActivity.this, "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class CustomUserOrderArrayAdapter extends  ArrayAdapter<Order> {
        Context context;
        ArrayList<Order> orders;
        String userId;
        public CustomUserOrderArrayAdapter(Context context, ArrayList<Order> ordersList,String userId) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.user_order_list_item,parent,false);
            }
            TextView parcelName = convertView.findViewById(R.id.parcelNameTV);
            TextView parcelTo = convertView.findViewById(R.id.parcelToTV);
            TextView parcelFrom = convertView.findViewById(R.id.parcelFromTV);
            TextView parcelDestination = convertView.findViewById(R.id.parcelDestinationTV);
            TextView parcelDescription = convertView.findViewById(R.id.parcelDescriptionTV);
            TextView parcelStatus = convertView.findViewById(R.id.parcelStatus);

            Button trackMapBtn = convertView.findViewById(R.id.trackBtn);

            parcelName.setText(parcel.getName());
            parcelTo.setText(parcel.getTo());
            parcelFrom.setText(parcel.getFrom());
            parcelDescription.setText(parcel.getDescription());
            parcelDestination.setText(parcel.getDestinationName());
            parcelStatus.setText(parcel.getStatus());

            switch (parcel.getStatus()) {
                case "Pending":
                    parcelStatus.setTextColor(Color.BLACK);
                    parcelStatus.setBackgroundColor(Color.YELLOW);
                    break;
                case "Success":
                    parcelStatus.setTextColor(Color.WHITE);
                    parcelStatus.setBackgroundColor(Color.GREEN);
                    break;
                case "Failed":
                case "Cancelled":
                    parcelStatus.setTextColor(Color.WHITE);
                    parcelStatus.setBackgroundColor(Color.RED);
                    break;
                default:
                    parcelStatus.setTextColor(Color.BLACK);
                    parcelStatus.setBackgroundColor(Color.GREEN);
                    break;
            }

            trackMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                    boolean gps_enabled = false;
                    boolean network_enabled = false;

                    try {
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch(Exception ex) {}

                    try {
                        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    } catch(Exception ex) {}

                    if(!gps_enabled && !network_enabled) {
                        // notify user
                        new AlertDialog.Builder(context)
                                .setMessage("Location service has been disabled. Please enable it to continue.")
                                .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    }
                                })
                                .setNegativeButton("Cancel",null)
                                .show();
                    }
                    else{
                        if(order.getDeliveryBoyId() == null){
                            Toast.makeText(context, "Please wait while your parcel is assigned to a delivery boy.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent mapsActivity = new Intent(ShowUserOrdersActivity.this, CustomerMapsActivity.class);
                            mapsActivity.putExtra(Constants.MAPS_TYPE,"customer");
                            mapsActivity.putExtra(Constants.CURRENT_USER,userId);
                            mapsActivity.putExtra(Constants.DELIVERY_BOY, order.getDeliveryBoyId());
                            mapsActivity.putExtra("Latitude", parcel.getDestinationLatLng().get("Latitude"));
                            mapsActivity.putExtra("Longitude", parcel.getDestinationLatLng().get("Longitude"));
                            startActivity(mapsActivity);
                        }
                    }
                }
            });

            return convertView;
        }
    }
   /* protected class BackgroundTask extends AsyncTask<Void,Void,ArrayList<Parcel>>{
        ProgressDialog progBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progBar = new ProgressDialog(ShowUserOrdersActivity.this);
            progBar.setTitle("Fetching Parcels List.");
            progBar.setMessage("Please wait");
            progBar.setCancelable(false);
            progBar.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Parcel> parcels) {
            super.onPostExecute(parcels);
            progBar.dismiss();
            productOrdersLV.setAdapter(new CustomUserOrderArrayAdapter(ShowUserOrdersActivity.this,parcels,username));
        }

        @Override
        protected ArrayList<Parcel> doInBackground(Void... voids) {
            if(getIntent().hasExtra(Constants.CURRENT_USER)) {
                String username = getIntent().getStringExtra(Constants.CURRENT_USER);
                CollectionReference colRef = firestore.collection("orders");
                colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot obj:queryDocumentSnapshots){
                            Order order = obj.toObject(Order.class);
                            order.setOrderId(obj.getId());
                            if(order.getUserId().equals(username)){
                                parcels.add(order.getParcel());
                            }
                        }
                    }
                });
                return null;
            }
            return null;
        }
    }*/
}