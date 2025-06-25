package com.planitsquare.holidaykeeper.domain.country.business.request;

public record CountrySaveServiceRequest(
        String countryCode,
        String name
) {
    
}
