package com.challengers.trackmyorder.model;

public class Product {
    String userId;
    String prodName, prodDes, date;
    String prodPrice;


    public Product(){

    }
    public Product(String userId,String prodName, String prodDes, String prodPrice, String date) {
        this.userId = userId;
        this.prodName = prodName;
        this.prodDes = prodDes;
        this.prodPrice = prodPrice;
        this.date = date;
    }

    public String getProdName() {
        return prodName;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getProdDes() {
        return prodDes;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public void setProdDes(String prodDes) {
        this.prodDes = prodDes;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }
}
