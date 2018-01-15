package com.choudou5.datatools.map.bean;

import java.io.Serializable;

/**
 * @Name: 经纬度Bo
 * @Author: xuhaowen
 * @Date: 2018-01-15
 */
public class GeoBean implements Serializable {

    private double lng;
    private double lat;

    public GeoBean() {
    }

    public GeoBean(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public GeoBean setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public GeoBean setLat(double lat) {
        this.lat = lat;
        return this;
    }

    @Override
    public String toString() {
        return lat + "," + lng;
    }
}
