package com.example.foodiechef.ui;

public class Review {
    private int star;
    private String feedback;
    private String userID;

    public Review() {
    }

    public Review(int star, String feedback, String userID) {
        this.star = star;
        this.feedback = feedback;
        this.userID = userID;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
