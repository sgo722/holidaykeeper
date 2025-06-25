package com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager;

import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.CountryNagerResponse;
import com.planitsquare.holidaykeeper.domain.holiday.infrastructure.api.nager.response.HolidayNagerResponse;

import java.util.List;

public interface NagerApiClient {
    List<CountryNagerResponse> getAvailableCountries();
    List<HolidayNagerResponse> getPublicHolidays(String countryCode, int year);
}