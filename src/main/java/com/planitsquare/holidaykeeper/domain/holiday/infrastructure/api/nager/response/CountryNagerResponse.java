package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response;

import com.planitsquare.holidaykeeper.domain.country.business.request.CountrySaveServiceRequest;

public record CountryNagerResponse(
        String countryCode,
        String name
) {
    public CountrySaveServiceRequest toService() {
        return new CountrySaveServiceRequest(countryCode, name);
    }
}
