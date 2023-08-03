package com.example.registration;

import java.io.Serializable;

public class ServiceProviderDetails implements Serializable {
    String name,address;
    boolean isVerified;
    float rating;
    float latitude,longitude;
    int popularity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public ServiceProviderDetails(String name, String address, boolean isVerified, float rating, float latitude, float longitude, int popularity) {
        this.name = name;
        this.address = address;
        this.isVerified = isVerified;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.popularity = popularity;
    }

    public ServiceProviderDetails(){

    }
}
