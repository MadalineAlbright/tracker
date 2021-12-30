package com.challengers.trackmyorder.model;

public class Customer extends User{
    public Customer() {
    }

    public Customer(String userId, String username) {
        super(userId, username, "customer");
    }
}
