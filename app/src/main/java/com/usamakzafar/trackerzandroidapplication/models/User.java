package com.usamakzafar.trackerzandroidapplication.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by usamazafar on 29/08/2017.
 */

public class User {

    private String email;
    private String name;
    private String dob;
    private String truck;
    private List<String> following = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getTruck() {
        return truck;
    }

    public void setTruck(String truck) {
        this.truck = truck;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }
}
