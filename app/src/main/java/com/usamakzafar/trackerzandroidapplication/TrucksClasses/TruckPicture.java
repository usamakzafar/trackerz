package com.usamakzafar.trackerzandroidapplication.TrucksClasses;

/**
 * Created by usamazafar on 23/05/2017.
 */

public class TruckPicture {

    private String pictureURL;
    private String pictureID;

    public TruckPicture(){}

    public String getPictureID() {
        return pictureID;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
