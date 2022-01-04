package com.challengers.trackmyorder.model;

import com.google.type.LatLng;

public class Parcel {
    private String time, parcelId, from, to, name, description, destinationName;
    private com.google.android.gms.maps.model.LatLng destination;
    public Parcel(String from, String to, String name, String description, com.google.android.gms.maps.model.LatLng destinationLatLng,String destinationName){
        this.from = from;
        this.to = to;
        this.name = name;
        this.description = description;
        this.destination = destinationLatLng;
        this.destinationName = destinationName;
    }
    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setDestination(com.google.android.gms.maps.model.LatLng destination) {
        this.destination = destination;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
