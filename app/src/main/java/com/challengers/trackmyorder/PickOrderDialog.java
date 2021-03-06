package com.challengers.trackmyorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import com.challengers.trackmyorder.util.Constants;


public class PickOrderDialog extends DialogFragment{

    private String[] orders;

    public void setOrders(String[] orders){
        this.orders = orders;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please select your order")
                .setItems(orders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                        intent.putExtra(Constants.ORDER_ID, orders[index]);
                        intent.putExtra(Constants.MAPS_TYPE, "Driver");
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}