package com.challengers.trackmyorder.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DelBoy extends RealmObject{
    @PrimaryKey
    private String id;
    private String name;
    private String currentOrderIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentOrderIds() {
        return currentOrderIds;
    }

    public void setCurrentOrderIds(String currentOrderIds) {
        this.currentOrderIds = currentOrderIds;
    }
}
