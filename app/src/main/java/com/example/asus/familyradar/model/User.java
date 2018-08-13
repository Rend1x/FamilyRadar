package com.example.asus.familyradar.model;

public class User {

    private int id;
    private String name;
    private String photo;
    private String email;
    private double latitude;
    private double longitude;

    public User(String name, String photo, String email, double latitude, double longitude) {
        this.name = name;
        this.photo = photo;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User(){}


    //Getter


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    //Setter


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
