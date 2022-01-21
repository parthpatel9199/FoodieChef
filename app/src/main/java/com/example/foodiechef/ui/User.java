package com.example.foodiechef.ui;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String id;
    private String name;
    private String password;
    private String email;
    private String mobile;
    private String image;
    private String location;
    private String biography;
    private double longitude;
    private double latitude;
    private List<String> favourite;
    private List<Review> reviews;
    private String token;

    public User() {
    }

    public User(String id, String name, String password, String email, String mobile, String image) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<String> getFavourite() {
        if (this.favourite == null){
            this.favourite = new ArrayList<>();
        }
        return favourite;
    }

    public void setFavourite(List<String> favourite) {
        this.favourite = favourite;
    }

    public List<Review> getReviews() {
        if(this.reviews == null){
            this.reviews = new ArrayList<>();
        }
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
