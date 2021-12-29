package com.challengers.trackmyorder.model;

public class Parcel {
    private String time, parcelId, from, to, name, description, destination;
    public Parcel(){
    }

    public Parcel(String from, String to, String name, String description, String destination){
        this.from = from;
        this.to = to;
        this.name = name;
        this.description = description;
        this.destination = destination;
    }
    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
