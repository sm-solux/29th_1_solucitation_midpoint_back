package com.solucitation.midpoint_backend.domain.logic;

import jakarta.validation.constraints.Size;

import java.util.List;

public class CoordinateRequest {
    @Size(min = 2, max = 20, message = "장소의 갯수가 2에서 20 사이여야 합니다.")
    private List<Double> latitudes;

    @Size(min = 2, max = 20, message = "장소의 갯수가 2에서 20 사이여야 합니다")
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


