package com.planitsquare.holidaykeeper.domain.country.business.response;

import com.planitsquare.holidaykeeper.domain.country.entity.Country;

public record CountryViewServiceResponse(Long id, String code, String name) {
    public static CountryViewServiceResponse from(Country entity) {
        return new CountryViewServiceResponse(entity.getId(), entity.getCode(), entity.getName());
    }
}