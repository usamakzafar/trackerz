package com.usamakzafar.trackerzandroidapplication.TrucksClasses;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by usamazafar on 23/05/2017.
 */

public class TruckReview {

    private String ID;
    private String pictureUrl;
    private String name;
    private int rating;
    private long timestamp;
    private String review;

    public TruckReview(){}

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}
