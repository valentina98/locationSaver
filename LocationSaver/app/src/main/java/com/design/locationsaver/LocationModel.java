package com.design.locationsaver;

public class LocationModel {

    private int id;
    private String placeName;
    private double x;
    private double y;
    //42.1471991,24.7261537 - youth hill, Plovdiv
    private boolean isCurrentLocation;

    public LocationModel(int id, String placeName, double x, double y, boolean isCurrentLocation) {
        this.id = id;
        this.placeName = placeName;
        this.x = x;
        this.y = y;
        this.isCurrentLocation = isCurrentLocation;
    }

    public LocationModel() {
    }

    @Override
    public String toString() {
        return id + ". " +
                placeName +
                ", longitude: " + x +
                ", latitude: " + y +
                ", saved as current location:" + isCurrentLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public void setCurrentLocation(boolean currentLocation) {
        isCurrentLocation = currentLocation;
    }
}
