package com.planitsquare.holidaykeeper.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient nagerRestClient() {
        return RestClient.builder()
                .baseUrl("https://date.nager.at")
                .build();
    }
}