package com.example.foodiechef.ui;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String chef_id;
    private String customer_id;
    private String price;
    private String delivery_type;
    private Date order_date;
    private String status;
    private List<Cart> carts;

    public Order() {
    }

    public Order(String id, String chef_id, String customer_id, String price, String delivery_type, Date order_date, String status, List<Cart> carts) {
        this.id = id;
        this.chef_id = chef_id;
        this.customer_id = customer_id;
        this.price = price;
        this.delivery_type = delivery_type;
        this.order_date = order_date;
        this.status = status;
        this.carts = carts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChef_id() {
        return chef_id;
    }

    public void setChef_id(String chef_id) {
        this.chef_id = chef_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
}
