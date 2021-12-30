package com.challengers.trackmyorder.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DeliveryBoy extends User{
    private String currentStatus, currentOrderId;
    private HashMap<String, LatLng> currentLocation;

    public DeliveryBoy() {
    }

    public DeliveryBoy(String userId, String username) {
        super(userId, username,"deliveryBoy");
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public HashMap<String, LatLng> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(HashMap<String, LatLng> currentLocation) {
        this.currentLocation = currentLocation;
    }
}
