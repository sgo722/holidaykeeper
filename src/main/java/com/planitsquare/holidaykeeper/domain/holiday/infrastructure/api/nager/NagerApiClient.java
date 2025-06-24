package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager;

import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.PublicHolidayResponse;

import java.util.List;

public interface NagerApiClient {
    List<CountryResponse> getAvailableCountries();
    List<PublicHolidayResponse> getPublicHolidays(String countryCode, int year);
}