package com.example.foodiechef.ui;


public class Cart {

    private String itemID;
    private String price;
    public int item_quantity;

    public Cart() {
    }

    public Cart(String itemID, String price, int item_quantity) {
        this.itemID = itemID;
        this.price = price;
        this.item_quantity = item_quantity;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity() {
        this.item_quantity = 1;
    }
}
