package com.planitsquare.holidaykeeper.domain.country.presentation.response;

import com.planitsquare.holidaykeeper.domain.country.business.response.CountryViewServiceResponse;

public record CountryControllerResponse(Long id, String code, String name) {
    public static CountryControllerResponse from(CountryViewServiceResponse serviceResponse) {
        return new CountryControllerResponse(serviceResponse.id(), serviceResponse.code(), serviceResponse.name());
    }
}
