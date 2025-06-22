package com.planitsquare.holidaykeeper.infrastructure.api;

import com.planitsquare.holidaykeeper.domain.dto.CountryResponse;
import com.planitsquare.holidaykeeper.domain.dto.PublicHolidayResponse;

import java.util.List;

public interface NagerApiClient {
    List<CountryResponse> getAvailableCountries();
    List<PublicHolidayResponse> getPublicHolidays(String countryCode, int year);
}