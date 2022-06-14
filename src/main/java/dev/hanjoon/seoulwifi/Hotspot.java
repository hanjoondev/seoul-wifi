package dev.hanjoon.seoulwifi;

public class Hotspot {
    private int id;
    private String district;
    private String address;
    private String detail;
    private String indoor;
    private String requestedAt;
    private double lat, lng, dist;
    private static final double rad = 6378137.0;

    public Hotspot(int id, double lat, double lng, String requestedAt) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.requestedAt = requestedAt;
    }

    public Hotspot(int id, String district, String address, String detail, String indoor, double lat, double lng) {
        this.id = id;
        this.district = district;
        this.address = address;
        this.detail = detail;
        this.indoor = indoor;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIndoor() {
        return indoor;
    }

    public void setIndoor(String indoor) {
        this.indoor = indoor;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDist() {
        return this.dist;
    }

    public String getStringDist() {
        return String.format("%.3fkm", this.dist);
    }

    public void setDist(double newLat, double newLng) {
        double dLat = Math.toRadians(newLat - this.lat), dLng = Math.toRadians(newLng - this.lng);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                 + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(newLat))
                 * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        this.dist = rad * c / 1000;
    }

    public String getRequestedAt() {
        return this.requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String[] getHistory() {
        return new String[] { String.valueOf(this.id),
                String.valueOf(this.lat), String.valueOf(this.lng), this.requestedAt };
    }

    public String[] getEverythingIncludingId() {
        return new String[] { String.valueOf(this.id), this.district, this.address, this.detail, this.indoor,
                String.valueOf(this.lat), String.valueOf(this.lng) };
    }

    public String[] getEverythingIncludingDistance() {
        return new String[] { String.format("%.3f", this.dist), this.district, this.address, this.detail, this.indoor,
                String.valueOf(this.lat), String.valueOf(this.lng) };
    }
}
