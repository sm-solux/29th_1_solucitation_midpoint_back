package com.solucitation.midpoint_backend.domain.places;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfigPlaces {

    @Bean(name = "placesRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}