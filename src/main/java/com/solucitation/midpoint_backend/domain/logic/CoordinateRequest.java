package com.solucitation.midpoint_backend.domain.logic;

import java.util.List;

public class CoordinateRequest {
    private List<Double> latitudes;
    private List<Double> longitudes;

    public CoordinateRequest() {
    }

    public CoordinateRequest(List<Double> latitudes, List<Double> longitudes) {
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public List<Double> getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(List<Double> latitudes) {
        this.latitudes = latitudes;
    }

    public List<Double> getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(List<Double> longitudes) {
        this.longitudes = longitudes;
    }
}


