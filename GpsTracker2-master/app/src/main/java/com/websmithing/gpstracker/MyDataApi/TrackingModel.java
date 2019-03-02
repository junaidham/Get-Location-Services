package com.websmithing.gpstracker.MyDataApi;

import io.realm.RealmObject;

public class TrackingModel extends RealmObject {

//    @PrimaryKey
//    int tracking_id;

    private String latitude, longitude, location,internet_status,gps_status,date,time;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInternet_status() {
        return internet_status;
    }

    public void setInternet_status(String internet_status) {
        this.internet_status = internet_status;
    }

    public String getGps_status() {
        return gps_status;
    }

    public void setGps_status(String gps_status) {
        this.gps_status = gps_status;
    }

//    public int getTracking_id() {
//        return tracking_id;
//    }
//
//    public void setTracking_id(int tracking_id) {
//        this.tracking_id = tracking_id;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
