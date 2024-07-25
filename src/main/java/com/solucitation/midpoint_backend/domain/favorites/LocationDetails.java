package com.solucitation.midpoint_backend.domain.favorites;

public class LocationDetails {
    private final double latitude;
    private final double longitude;

    public LocationDetails(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}