package com.example.foodiechef.ui;


import java.sql.Timestamp;
import java.util.Date;

public class Food {

    private String id;
    private String Name;
    private String Category;
    private String Ingredient;
    private String Price;
    private String Time;
    private String image;
    private String userID;
    private int status;

    public Food() {
    }

    public Food(String id, String name, String category, String ingredient, String price, String time, String image, String userID) {
        this.id = id;
        Name = name;
        Category = category;
        Ingredient = ingredient;
        Price = price;
        Time = time;
        this.image = image;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getIngredient() {
        return Ingredient;
    }

    public void setIngredient(String ingredient) {
        Ingredient = ingredient;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
