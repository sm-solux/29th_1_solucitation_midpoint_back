package com.solucitation.midpoint_backend.domain.favorites.entity;

public class LocationDetails {
    private final double latitude;
    private final double longitude;
    private final String googlePlaceName;
    private final String googlePlaceAddress;

    public LocationDetails(double latitude, double longitude, String googlePlaceName, String googlePlaceAddress) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.googlePlaceName = googlePlaceName;
        this.googlePlaceAddress = googlePlaceAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGooglePlaceName() {
        return googlePlaceName;
    }

    public String getGooglePlaceAddress() {
        return googlePlaceAddress;
    }
}
