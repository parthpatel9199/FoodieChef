package com.example.foodiechef.ui;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Chef_cart {

    private String id;
    private List<Cart> cart;

    public Chef_cart() {
    }

    public Chef_cart(String id, List<Cart> cart) {
        this.id = id;
        this.cart = cart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Cart> getCart() {
        if (cart == null){
            cart = new ArrayList<>();
        }
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }
}
