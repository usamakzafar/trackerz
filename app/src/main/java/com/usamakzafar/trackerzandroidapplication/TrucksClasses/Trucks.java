package com.usamakzafar.trackerzandroidapplication.TrucksClasses;

import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;

import java.io.Serializable;

/**
 * Created by usamazafar on 11/05/2017.
 */

public class Trucks {
    public static int TYPE_FOOD = 1;
    public static int TYPE_COFFEE = 2;
    public static int TYPE_EVENT = 3;
    public static int TYPE_OTHER = 4;

    private String _name;
    private TruckLocation _location;
    private String _description;
    private int _type;
    private int _paidSubscriber;
    private String _id;
    private int _open;
    private int reviewsCount =0;
    private int followers = 0;

    private int rating;

    private String logoURL;
    private String menuReference;
    private String picturesReference;
    private String reviewsReference;

    private String facebook;
    private String twitter;
    private String google;

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    private String youtube;

    public Trucks(String name, TruckLocation location, String description, int type) {
        this._name = name;
        this._location = location;
        this._description = description;
        this._type = type;
    }

    public Trucks(){
    }


    public TruckLocation get_location() {
        return _location;
    }

    public void set_location(TruckLocation location) {
        this._location = location;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String name) {
        this._name = name;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String description) {
        this._description = description;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int type) {
        this._type = type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int get_open() {
        return _open;
    }

    public void set_open(int _open) {
        this._open = _open;
    }

    public String getMenuReference() {
        return menuReference;
    }

    public void setMenuReference(String menuReference) {
        this.menuReference = menuReference;
    }

    public String getPicturesReference() {
        return picturesReference;
    }

    public void setPicturesReference(String picturesReference) {
        this.picturesReference = picturesReference;
    }

    public String getReviewsReference() {
        return reviewsReference;
    }

    public void setReviewsReference(String reviewsReference) {
        this.reviewsReference = reviewsReference;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public int get_paidSubscriber() {
        return _paidSubscriber;
    }

    public void set_paidSubscriber(int _paidSubscriber) {
        this._paidSubscriber = _paidSubscriber;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
