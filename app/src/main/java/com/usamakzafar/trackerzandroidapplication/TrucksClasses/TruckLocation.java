package com.usamakzafar.trackerzandroidapplication.TrucksClasses;

/**
 * Created by usamazafar on 18/05/2017.
 */

public class TruckLocation {

    private Double latitude;
    private Double longitude;

    public TruckLocation(){

    }
    public TruckLocation(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
