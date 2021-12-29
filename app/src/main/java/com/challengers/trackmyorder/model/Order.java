package com.challengers.trackmyorder.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Order{
    private String orderId;
    private String status;
    private String orderTime;
    private String arrivalTime;
    private String userId;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    private String item;
    private Parcel parcel;

    public Order() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Order(String orderTime, Parcel parcel, String userId) {
        this.status = "Pending";
        this.orderTime = orderTime;
        this.arrivalTime = "In progress";
        this.parcel = parcel;
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }
}
