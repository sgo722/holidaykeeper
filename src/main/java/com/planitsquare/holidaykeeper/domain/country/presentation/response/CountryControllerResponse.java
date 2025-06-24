package com.planitsquare.holidaykeeper.domain.country.presentation.response;

import com.planitsquare.holidaykeeper.domain.country.business.response.CountryServiceResponse;

public record CountryControllerResponse(String code, String name) {
    public static CountryControllerResponse from(CountryServiceResponse serviceResponse) {
        return new CountryControllerResponse(serviceResponse.code(), serviceResponse.name());
    }
}
