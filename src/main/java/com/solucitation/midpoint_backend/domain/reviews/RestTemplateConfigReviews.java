package com.solucitation.midpoint_backend.domain.reviews;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfigReviews {

    @Bean(name = "reviewsRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
