package com.solucitation.midpoint_backend.domain.logic;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MidpointController {

    @PostMapping("/midpoint/api/logic")
    public Coordinate calculateMidpoint(@RequestBody CoordinateRequest request) {
        List<Double> latitudes = request.getLatitudes();
        List<Double> longitudes = request.getLongitudes();

        int count = latitudes.size();
        if (count != longitudes.size()) {
            throw new IllegalArgumentException("The number of latitudes and longitudes must be the same.");
        }

        double sumLatitude = 0;
        double sumLongitude = 0;

        for (int i = 0; i < count; i++) {
            sumLatitude += latitudes.get(i);
            sumLongitude += longitudes.get(i);
        }

        double avgLatitude = sumLatitude / count;
        double avgLongitude = sumLongitude / count;

        return new Coordinate(avgLatitude, avgLongitude);
    }
}

