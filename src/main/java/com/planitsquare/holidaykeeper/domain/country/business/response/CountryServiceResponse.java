package com.planitsquare.holidaykeeper.domain.country.business.response;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;

public record CountryServiceResponse(String code, String name) {
    public static CountryServiceResponse from(Country entity) {
        return new CountryServiceResponse(entity.getCode(), entity.getName());
    }
}