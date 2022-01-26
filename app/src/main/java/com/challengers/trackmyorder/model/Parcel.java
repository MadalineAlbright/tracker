package com.challengers.trackmyorder.model;


import java.util.HashMap;

public class Parcel {
    private String parcelId, from, to, name, description, destinationName, status;
    private HashMap<String,String> destinationLatLng;
    public Parcel(){

    }
    public Parcel(String from, String to, String name, String description,HashMap<String,String> destinationLatLng,String destinationName){
        this.from = from;
        this.to = to;
        this.name = name;
        this.description = description;
        this.destinationLatLng = destinationLatLng;
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

    public HashMap<String, String> getDestinationLatLng() {
        return destinationLatLng;
    }

    public void setDestinationLatLng(HashMap<String, String> destinationLatLng) {
        this.destinationLatLng = destinationLatLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
